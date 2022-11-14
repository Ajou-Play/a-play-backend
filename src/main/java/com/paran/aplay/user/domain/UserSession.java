package com.paran.aplay.user.domain;

import com.google.gson.JsonObject;
import com.paran.aplay.user.dto.Participant;
import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.Getter;
import org.kurento.client.Continuation;
import org.kurento.client.EventListener;
import org.kurento.client.IceCandidate;
import org.kurento.client.IceCandidateFoundEvent;
import org.kurento.client.MediaPipeline;
import org.kurento.client.WebRtcEndpoint;
import org.kurento.jsonrpc.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Getter
public class UserSession implements Closeable {

    private final Logger log = LoggerFactory.getLogger(UserSession.class);

    private final Participant participant;

    private final WebSocketSession session;

    private final MediaPipeline pipeline;

    private final Long roomId;

    private final WebRtcEndpoint outgoingMedia;

    private final ConcurrentMap<Long, WebRtcEndpoint> incomingMedia = new ConcurrentHashMap<>();

    public UserSession(final Participant participant, Long roomId, final WebSocketSession session,
                       MediaPipeline pipeline) {

        this.pipeline = pipeline;
        this.participant = participant;
        this.session = session;
        this.roomId = roomId;
        this.outgoingMedia = new WebRtcEndpoint.Builder(pipeline).build();
        this.outgoingMedia.addIceCandidateFoundListener(new EventListener<IceCandidateFoundEvent>() {
            @Override
            public void onEvent(IceCandidateFoundEvent event) {
                JsonObject response = new JsonObject();
                response.addProperty("eventType", "iceCandidate");
                response.add("participant", JsonUtils.toJsonObject(participant));
                response.add("candidate", JsonUtils.toJsonObject(event.getCandidate()));
                try {
                    synchronized (session) {
                        log.debug(response.toString());
                        session.sendMessage(new TextMessage(response.toString()));
                    }
                } catch (IOException e) {
                    log.debug(e.getMessage());
                }
            }
        });
    }

    public void receiveVideoFrom(UserSession sender, String sdpOffer) throws IOException {
        Long senderId = sender.participant.getUserId();
        Long currentUserId = this.participant.getUserId();

        log.info("USER {}: connecting with {} in room {}", currentUserId, senderId, this.roomId);

        log.trace("USER {}: SdpOffer for {} is {}", currentUserId, senderId, sdpOffer);

        final String ipSdpAnswer = this.getEndpointForUser(sender).processOffer(sdpOffer);
        final JsonObject scParams = new JsonObject();
        scParams.addProperty("eventType", "receiveVideoAnswer");
        scParams.add("participant", JsonUtils.toJsonObject(sender.participant));
        scParams.addProperty("sdpAnswer", ipSdpAnswer);

        log.trace("USER {}: SdpAnswer for {} is {}", currentUserId, senderId, ipSdpAnswer);
        this.sendMessage(scParams);
        log.debug("gather candidates");
        this.getEndpointForUser(sender).gatherCandidates();
    }

    private WebRtcEndpoint getEndpointForUser(final UserSession sender) {
        Long senderId = sender.participant.getUserId();
        Long currentUserId = this.participant.getUserId();

        if (senderId.equals(currentUserId)) {
            log.debug("PARTICIPANT {}: configuring loopback", currentUserId);
            return outgoingMedia;
        }

        log.debug("PARTICIPANT {}: receiving video from {}", currentUserId, senderId);

        WebRtcEndpoint incoming = incomingMedia.get(sender.participant.getUserId());
        if (incoming == null) {
            log.debug("PARTICIPANT {}: creating new endpoint for {}", currentUserId, senderId);
            incoming = new WebRtcEndpoint.Builder(pipeline).build();

            incoming.addIceCandidateFoundListener(new EventListener<IceCandidateFoundEvent>() {

                @Override
                public void onEvent(IceCandidateFoundEvent event) {
                    JsonObject response = new JsonObject();
                    response.addProperty("id", "iceCandidate");
                    response.add("participant", JsonUtils.toJsonObject(sender.participant));
                    response.add("candidate", JsonUtils.toJsonObject(event.getCandidate()));
                    try {
                        synchronized (session) {
                            session.sendMessage(new TextMessage(response.toString()));
                        }
                    } catch (IOException e) {
                        log.debug(e.getMessage());
                    }
                }
            });

            incomingMedia.put(senderId, incoming);
        }

        log.debug("PARTICIPANT {}: obtained endpoint for {}", currentUserId, senderId);
        sender.outgoingMedia.connect(incoming);

        return incoming;
    }

    public void cancelVideoFrom(final UserSession sender) {
        this.cancelVideoFrom(sender.participant.getUserId());
    }

    public void cancelVideoFrom(final Long senderId) {
        log.debug("PARTICIPANT {}: canceling video reception from {}", this.participant.getUserId(), senderId);
        final WebRtcEndpoint incoming = incomingMedia.remove(senderId);

        log.debug("PARTICIPANT {}: removing endpoint for {}", this.participant.getUserId(), senderId);
        incoming.release(new Continuation<>() {
            @Override
            public void onSuccess(Void result) throws Exception {
                log.trace("PARTICIPANT {}: Released successfully incoming EP for {}",
                        UserSession.this.participant.getUserId(), senderId);
            }

            @Override
            public void onError(Throwable cause) throws Exception {
                log.warn("PARTICIPANT {}: Could not release incoming EP for {}",
                        UserSession.this.participant.getUserId(),
                        senderId);
            }
        });
    }



    @Override
    public void close() throws IOException {
        log.debug("PARTICIPANT {}: Releasing resources", this.participant.getName());
        for (final Long remoteParticipantId : incomingMedia.keySet()) {

            log.trace("PARTICIPANT {}: Released incoming EP for {}", this.participant.getUserId(), remoteParticipantId);

            final WebRtcEndpoint ep = this.incomingMedia.get(remoteParticipantId);

            ep.release(new Continuation<Void>() {

                @Override
                public void onSuccess(Void result) throws Exception {
                    log.trace("PARTICIPANT {}: Released successfully incoming EP for {}",
                            UserSession.this.participant.getUserId(), remoteParticipantId);
                }

                @Override
                public void onError(Throwable cause) throws Exception {
                    log.warn("PARTICIPANT {}: Could not release incoming EP for {}", UserSession.this.participant.getUserId(),
                            remoteParticipantId);
                }
            });
        }

        outgoingMedia.release(new Continuation<Void>() {

            @Override
            public void onSuccess(Void result) throws Exception {
                log.trace("PARTICIPANT {}: Released outgoing EP",  UserSession.this.participant.getUserId());
            }

            @Override
            public void onError(Throwable cause) throws Exception {
                log.warn("USER {}: Could not release outgoing EP",  UserSession.this.participant.getUserId());
            }
        });

    }

    public void sendMessage(JsonObject message) throws IOException {
        log.debug("USER {}: Sending message {}", this.participant.getUserId(), message);
        synchronized (session) {
            session.sendMessage(new TextMessage(message.toString()));
        }
    }

    public void addCandidate(IceCandidate candidate, Long userId) {
        if (this.participant.getUserId().compareTo(userId) == 0) {
            outgoingMedia.addIceCandidate(candidate);
        } else {
            WebRtcEndpoint webRtc = incomingMedia.get(userId);
            if (webRtc != null) {
                webRtc.addIceCandidate(candidate);
            }
        }
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof UserSession)) {
            return false;
        }
        UserSession other = (UserSession) obj;
        boolean eq = this.participant.getUserId().equals(other.participant.getUserId());
        eq &= roomId.equals(other.roomId);
        return eq;
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = (int) (31 * result + this.participant.getUserId());
        result = (int) (31 * result + roomId);
        return result;
    }

}
