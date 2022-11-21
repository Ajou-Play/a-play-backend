package com.paran.aplay.meeting;

import com.paran.aplay.common.util.StompMessagingService;
import com.paran.aplay.meeting.dto.Candidate;
import com.paran.aplay.meeting.dto.response.ExistingUsersResponse;
import com.paran.aplay.meeting.dto.response.IceCandidateResponse;
import com.paran.aplay.meeting.dto.response.NewUserArrivedResponse;
import com.paran.aplay.meeting.dto.response.ReceiveVideoAnswerResponse;
import com.paran.aplay.user.domain.User;
import com.paran.aplay.user.dto.Participant;
import com.paran.aplay.user.service.UserUtilService;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.kurento.client.IceCandidate;
import org.kurento.client.WebRtcEndpoint;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebRtcService {

    private final UserUtilService userUtilService;

    private final KurentoRoomService kurentoRoomService;

    private final RoomUserService roomUserService;

    private final StompMessagingService stompMessagingService;

    public void join(User user, Long roomId) {
        final List<User> roomUsers = roomUserService.findAllByRoomId(roomId);
        final Optional<User> roomUserResult = roomUserService.findById(roomId, user.getId());
        if (roomUserResult.isPresent()) {
            roomUsers.forEach(roomUser -> kurentoRoomService.removeIncomingEndpoint(roomUser.getId(), user.getId()));
            kurentoRoomService.removeIncomingEndpoint(user.getId());
            kurentoRoomService.removeOutgoingEndpoint(user.getId());
        }
        kurentoRoomService.createOutgoingEndpoint(
                roomId,
                user.getId(),
                event -> {
                    final IceCandidate iceCandidate = event.getCandidate();
                    stompMessagingService.sendToUser(user.getId(), EventType.iceCandidate,
                            IceCandidateResponse.builder()
                                    .eventType(EventType.iceCandidate)
                                    .userId(user.getId())
                                    .candidate(Candidate.of(iceCandidate))
                                    .build()
                    );
                }
        );
        roomUsers.forEach(roomUser -> stompMessagingService.sendToUser(
                roomUser.getId(),
                EventType.newUserArrived,
                NewUserArrivedResponse.builder()
                        .eventType(EventType.newUserArrived)
                        .user(Participant.from(user))
        ));
        final List<Participant> participants = roomUsers.stream().map(Participant::from).toList();
        if (roomUserResult.isEmpty()) roomUserService.add(roomId, user);
        stompMessagingService.sendToUser(user.getId(), EventType.existingUsers,
                ExistingUsersResponse.builder()
                        .eventType(EventType.existingUsers)
                        .data(participants)
                        .build()
        );
    }

    public void offer(Long userId, Long senderId, String sdpOffer) {
        kurentoRoomService.removeIncomingEndpoint(userId, senderId);
        final User sender = userUtilService.getUserById(senderId);
        final Optional<Long> roomIdResult = roomUserService.findRoomIdByUserId(userId);
        if (roomIdResult.isEmpty()) return;
        Long roomId = roomIdResult.get();
        final WebRtcEndpoint incomingEndpoint = Objects.equals(userId, senderId)
                ? kurentoRoomService.getOutgoingEndpoint(userId)
                : kurentoRoomService.createIncomingEndpoint(
                        roomId,
                        userId,
                        senderId,
                        event -> {
                            final IceCandidate iceCandidate = event.getCandidate();
                            stompMessagingService.sendToUser(userId, EventType.iceCandidate,
                                    IceCandidateResponse.builder()
                                            .eventType(EventType.iceCandidate)
                                            .userId(userId)
                                            .candidate(Candidate.of(iceCandidate))
                                            .build()
                            );
                        }
                );

        if (!Objects.equals(userId, senderId)) {
            kurentoRoomService.getOutgoingEndpoint(senderId).connect(incomingEndpoint);
        }

        final String sdpAnswer = incomingEndpoint.processOffer(sdpOffer);
        stompMessagingService.sendToUser(userId, EventType.receiveVideoAnswer,
                ReceiveVideoAnswerResponse.builder()
                        .eventType(EventType.receiveVideoAnswer)
                        .user(Participant.from(sender))
                        .sdpAnswer(sdpAnswer)
                        .build()
        );
        incomingEndpoint.gatherCandidates();
    }

    public void leave(Long userId) {

    }

    public void iceCandidate(
            Long userId,
            Long senderId,
            Candidate candidate
    ) {
        if (userId == senderId) {
            kurentoRoomService.addIceCandidateToOutgoingEndpoint(userId, candidate);
        } else {
            kurentoRoomService.addIceCandidateToIncomingEndpoint(userId, senderId, candidate);
        }
    }
}
