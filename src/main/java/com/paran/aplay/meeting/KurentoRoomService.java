package com.paran.aplay.meeting;

import com.paran.aplay.meeting.dto.Candidate;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kurento.client.EventListener;
import org.kurento.client.IceCandidate;
import org.kurento.client.IceCandidateFoundEvent;
import org.kurento.client.KurentoClient;
import org.kurento.client.MediaPipeline;
import org.kurento.client.WebRtcEndpoint;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KurentoRoomService {

    private final KurentoClient kurentoClient;

    // roomId, MediaPipeline
    private final ConcurrentMap<Long, MediaPipeline> roomMediaPipelines = new ConcurrentHashMap<>();
    // userId, EndPoint
    private final ConcurrentMap<Long, WebRtcEndpoint> outgoingEndpoints = new ConcurrentHashMap<>();
    // userId, (senderId, EndPoint)
    private final ConcurrentMap<Long, ConcurrentMap<Long, WebRtcEndpoint>> incomingEndpoints = new ConcurrentHashMap<>();

    // create
    public void createOutgoingEndpoint(
            Long roomId,
            Long userId,
            EventListener<IceCandidateFoundEvent> listener
    ) {
        final MediaPipeline mediaPipeline = getMediaPipeline(roomId);

        log.info("Create [OUTGOING_ENDPOINT] for identifier [{}]", userId);
        final WebRtcEndpoint outgoingEndpoint = new WebRtcEndpoint.Builder(mediaPipeline).build();
        outgoingEndpoint.addIceCandidateFoundListener(listener);
        outgoingEndpoints.put(userId, outgoingEndpoint);
    }

    private MediaPipeline getMediaPipeline(Long roomId) {
        if (!roomMediaPipelines.containsKey(roomId)) {
            log.info("Create [ROOM_PIPELINE] for identifier [{}]", roomId);
            roomMediaPipelines.put(roomId, kurentoClient.createMediaPipeline());
        }

        return roomMediaPipelines.get(roomId);
    }

    public WebRtcEndpoint createIncomingEndpoint(
            Long roomId,
            Long userId,
            Long senderId,
            EventListener<IceCandidateFoundEvent> listener
    ) {
        if (!incomingEndpoints.containsKey(userId)) {
            incomingEndpoints.put(userId, new ConcurrentHashMap<>());
        }

        final MediaPipeline mediaPipeline = getMediaPipeline(roomId);

        log.info("Create [INCOMING_ENDPOINT] for identifier [{}]", userId + "-" + senderId);
        final WebRtcEndpoint incomingEndpoint = new WebRtcEndpoint.Builder(mediaPipeline).build();
        incomingEndpoint.addIceCandidateFoundListener(listener);

        this.incomingEndpoints.get(userId).put(senderId, incomingEndpoint);

        return incomingEndpoint;
    }

    // get
    public WebRtcEndpoint getOutgoingEndpoint(Long userId) {
        return outgoingEndpoints.get(userId);
    }

    public WebRtcEndpoint getIncomingEndpoint(Long userId, Long senderId) {
        if (incomingEndpoints.containsKey(userId)) {
            return incomingEndpoints.get(userId).get(senderId);
        }

        return null;
    }

    // remove
    public void removeIncomingEndpoint(Long userId, Long senderId) {
        if (this.incomingEndpoints.containsKey(userId) && incomingEndpoints.get(userId).containsKey(senderId)) {
            log.info("Release [INCOMING_ENDPOINT] for identifier [{}]", userId + "-" + senderId);
            this.incomingEndpoints.get(userId).remove(senderId).release();
        }
    }

    public void removeIncomingEndpoint(Long userId) {
        if (incomingEndpoints.containsKey(userId)) {
            incomingEndpoints.remove(userId)
                    .forEach((user, incomingEndpoint) -> {
                        log.info("Release [INCOMING_ENDPOINT] for identifier [{}]", user);
                        incomingEndpoint.release();
                    });
        }
    }

    public void removeOutgoingEndpoint(Long userId) {
        if (outgoingEndpoints.containsKey(userId)) {
            log.info("Release [OUTGOING_ENDPOINT] for identifier [{}]", userId);
            outgoingEndpoints.remove(userId).release();
        }
    }

    public void removeRoomMediaPipeline(Long roodId) {
        if (roomMediaPipelines.containsKey(roodId)) {
            log.info("Release [ROOM_PIPELINE] for identifier [{}]", roodId);
            roomMediaPipelines.remove(roodId).release();
        }
    }

    // add
    public void addIceCandidateToOutgoingEndpoint(
            Long userId,
            Candidate candidate
    ) {
        if (this.outgoingEndpoints.containsKey(userId)) {
            this.outgoingEndpoints.get(userId)
                    .addIceCandidate(new IceCandidate(candidate.getCandidate(), candidate.getSdpMid(), candidate.getSdpMLineIndex()));
        }
    }

    public void addIceCandidateToIncomingEndpoint(
            Long userId,
            Long senderId,
            Candidate candidate
    ) {
        if (incomingEndpoints.containsKey(userId) && incomingEndpoints.get(userId).containsKey(senderId)) {
            incomingEndpoints.get(userId).get(senderId)
                    .addIceCandidate(new IceCandidate(candidate.getCandidate(), candidate.getSdpMid(),
                            candidate.getSdpMLineIndex()));
        }
    }

    @PreDestroy
    public void preDestroy() {
        log.info("Start release all endpoints and pipelines.");

        outgoingEndpoints.forEach((userId, endpoint) -> {
            log.info("Release [OUTGOING_ENDPOINT] for identifier [{}]", userId);
            endpoint.release();
        });

        incomingEndpoints.forEach(
                (userId, endpoints) -> endpoints.forEach(
                        (senderId, endpoint) -> {
                            log.info("Release [INCOMING_ENDPOINT] for identifier [{}]", userId + "|" + senderId);
                            endpoint.release();
                        }
                )
        );

        roomMediaPipelines.forEach((roomId, pipeline) -> {
            log.info("Release [ROOM_PIPELINE] for identifier [{}]", roomId);
            pipeline.release();
        });
    }
}
