package com.paran.aplay.meeting;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.paran.aplay.user.domain.UserSession;
import com.paran.aplay.user.dto.Participant;
import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.annotation.PreDestroy;
import org.kurento.client.Continuation;
import org.kurento.client.MediaPipeline;
import org.kurento.jsonrpc.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketSession;

public class Room implements Closeable {
    private final Logger log = LoggerFactory.getLogger(Room.class);

    private final ConcurrentMap<Long, UserSession> sessions = new ConcurrentHashMap<>();
    private final MediaPipeline pipeline;
    private final Long roomId;

    public Room(Long roomId, MediaPipeline pipeline) {
        this.roomId = roomId;
        this.pipeline = pipeline;
        log.info("ROOM {} has been created", roomId);
    }

    public Collection<UserSession> getParticipantSessions() {
        return sessions.values();
    }

    public UserSession getParticipantSession(Long userId) {
        return sessions.get(userId);
    }

    @PreDestroy
    private void shutdown() {
        this.close();
    }

    public Long getRoomId() {
        return this.roomId;
    }

    public UserSession join(Participant participant, WebSocketSession session) throws IOException {
        log.info("ROOM {}: adding participant {}", this.roomId, participant.getUserId());
        final UserSession userSession = new UserSession(participant, this.roomId, session, this.pipeline);
        joinRoom(userSession);
        sessions.put(userSession.getParticipant().getUserId(), userSession);
        sendParticipantsInfo(userSession);
        return userSession;
    }

    public void leave(UserSession user) throws IOException {
        log.debug("PARTICIPANT {}: Leaving room {}", user.getParticipant().getUserId(), this.roomId);
        this.removeParticipant(user.getParticipant());
        user.close();
    }

    private Collection<Long> joinRoom(UserSession userSession) throws IOException {
        final JsonObject newParticipantMsg = new JsonObject();
        newParticipantMsg.addProperty("eventType", "newParticipantArrived");
        newParticipantMsg.add("participant", JsonUtils.toJsonObject(userSession.getParticipant()));

        final List<Long> participantUserIds = new ArrayList<>(sessions.values().size());
        log.debug("ROOM {}: notifying other participants of new participant {}", roomId,
                userSession.getParticipant().getUserId());

        for (final UserSession currentSession : sessions.values()) {
            try {
                currentSession.sendMessage(newParticipantMsg);
            } catch (final IOException e) {
                log.debug("ROOM {}: participant {} could not be notified", roomId, currentSession.getParticipant().getUserId(), e);
            }
            participantUserIds.add(currentSession.getParticipant().getUserId());
        }

        return participantUserIds;
    }

    private void removeParticipant(Participant leavingUser) throws IOException {
        sessions.remove(leavingUser.getUserId());

        log.debug("ROOM {}: notifying all users that {} is leaving the room", this.roomId, leavingUser.getUserId());

        final List<Long> unnotifiedParticipants = new ArrayList<>();
        final JsonObject participantLeftJson = new JsonObject();
        participantLeftJson.addProperty("eventType", "participantLeft");
        participantLeftJson.add("participant", JsonUtils.toJsonObject(leavingUser));
        for (final UserSession session : sessions.values()) {
            try {
                session.cancelVideoFrom(leavingUser.getUserId());
                session.sendMessage(participantLeftJson);
            } catch (final IOException e) {
                unnotifiedParticipants.add(session.getParticipant().getUserId());
            }
        }

        if (!unnotifiedParticipants.isEmpty()) {
            log.debug("ROOM {}: The users {} could not be notified that {} left the room", this.roomId,
                    unnotifiedParticipants, leavingUser.getUserId());
        }
    }

    public void sendParticipantsInfo(UserSession currentSession) throws IOException {

        final JsonArray participantsArray = new JsonArray();
        for (final UserSession session : this.getParticipantSessions()) {
            if (!session.equals(currentSession)) {
                final JsonElement participantId = new JsonPrimitive(session.getParticipant().getUserId());
                participantsArray.add(participantId);
            }
        }

        final JsonObject existingParticipantsMsg = new JsonObject();
        existingParticipantsMsg.addProperty("eventType", "existingParticipants");
        existingParticipantsMsg.add("data", participantsArray);
        log.debug("PARTICIPANT {}: sending a list of {} participants", currentSession.getParticipant().getUserId(),
                participantsArray.size());
        currentSession.sendMessage(existingParticipantsMsg);
    }





    @Override
    public void close() {
        for (final UserSession user : sessions.values()) {
            try {
                user.close();
            } catch (IOException e) {
                log.debug("ROOM {}: Could not invoke close on participant {}", this.roomId, user.getParticipant().getUserId(),
                        e);
            }
        }

        sessions.clear();

        pipeline.release(new Continuation<Void>() {

            @Override
            public void onSuccess(Void result) throws Exception {
                log.trace("ROOM {}: Released Pipeline", Room.this.roomId);
            }

            @Override
            public void onError(Throwable cause) throws Exception {
                log.warn("PARTICIPANT {}: Could not release Pipeline", Room.this.roomId);
            }
        });

        log.debug("Room {} closed", this.roomId);
    }
}
