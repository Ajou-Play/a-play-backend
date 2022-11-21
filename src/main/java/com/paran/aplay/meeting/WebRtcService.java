package com.paran.aplay.meeting;

import com.paran.aplay.common.util.StompMessagingService;
import com.paran.aplay.meeting.dto.Candidate;
import com.paran.aplay.meeting.dto.response.ExistingUsersResponse;
import com.paran.aplay.meeting.dto.response.IceCandidateResponse;
import com.paran.aplay.meeting.dto.response.NewUserArrivedResponse;
import com.paran.aplay.user.domain.User;
import com.paran.aplay.user.dto.Participant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.kurento.client.IceCandidate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebRtcService {

    private final KurentoRoomService kurentoRoomService;

    private final RoomUserService roomUserService;

    private final StompMessagingService stompMessagingService;

    public void join(User user, Long roomId) {
        final List<User> roomUsers = roomUserService.findAllByRoomId(roomId);
        final Optional<User> roomUserResult = roomUserService.findById(roomId, user.getId());
        if (roomUserResult.isPresent()) {
            roomUsers.forEach(roomUser -> this.kurentoRoomService.removeIncomingEndpoint(roomUser.getId(), user.getId()));
            this.kurentoRoomService.removeIncomingEndpoint(user.getId());
            this.kurentoRoomService.removeOutgoingEndpoint(user.getId());
        }
        this.kurentoRoomService.createOutgoingEndpoint(
                roomId,
                user.getId(),
                event -> {
                    final IceCandidate iceCandidate = event.getCandidate();
                    this.stompMessagingService.sendToUser(user.getId(), EventType.iceCandidate,
                            IceCandidateResponse.builder()
                                    .eventType(EventType.iceCandidate)
                                    .userId(user.getId())
                                    .candidate(Candidate.of(iceCandidate))
                                    .build()
                    );
                }
        );
        roomUsers.forEach(roomUser -> this.stompMessagingService.sendToUser(
                roomUser.getId(),
                EventType.newUserArrived,
                NewUserArrivedResponse.builder()
                        .eventType(EventType.newUserArrived)
                        .user(Participant.from(user))
        ));
        final List<Participant> participants = roomUsers.stream().map(Participant::from).toList();
        if (roomUserResult.isEmpty()) roomUserService.add(roomId, user);
        this.stompMessagingService.sendToUser(user.getId(), EventType.existingUsers,
                ExistingUsersResponse.builder()
                        .eventType(EventType.existingUsers)
                        .data(participants)
                        .build()
        );
    }

    public void offer(Long userId, Long senderId, String sdpOffer) {

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
