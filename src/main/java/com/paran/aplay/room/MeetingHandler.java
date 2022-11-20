package com.paran.aplay.room;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.paran.aplay.user.domain.UserRegistry;
import com.paran.aplay.user.domain.UserSession;
import com.paran.aplay.user.dto.Participant;
import com.paran.aplay.user.service.UserUtilService;
import java.io.IOException;
import org.kurento.client.IceCandidate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

// TODO : redis로 세션 / 방 정보 어떻게 뺄지
// TODO : exception 발생할 수 있는 부분 처리.
public class MeetingHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(MeetingHandler.class);

    private static final Gson gson = new GsonBuilder().create();

    private final UserUtilService userUtilService;

    @Autowired
    private RoomManager roomManager;

    @Autowired
    private UserRegistry registry;

    public MeetingHandler(UserUtilService userUtilService) {
        this.userUtilService = userUtilService;
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        final JsonObject jsonMessage = gson.fromJson(message.getPayload(), JsonObject.class);
        System.out.println(jsonMessage);
        final UserSession user = registry.getBySession(session);

        if (user != null) {
            log.debug("Incoming message from user '{}': {}", user.getParticipant().getName(), jsonMessage);
        } else {
            log.debug("Incoming message from new user: {}", jsonMessage);
        }

        switch (jsonMessage.get("eventType").getAsString()) {
            case "joinRoom":
                joinRoom(jsonMessage, session);
                break;
            case "receiveVideoFrom":
                final Long senderId = jsonMessage.get("userId").getAsLong();
                final UserSession sender = registry.getById(senderId);
                final String sdpOffer = jsonMessage.get("sdpOffer").getAsString();
                user.receiveVideoFrom(sender, sdpOffer);
                break;
            case "leaveRoom":
                leaveRoom(user);
                break;
            case "onIceCandidate":
                JsonObject candidate = jsonMessage.get("candidate").getAsJsonObject();

                if (user != null) {
                    IceCandidate cand = new IceCandidate(candidate.get("candidate").getAsString(),
                            candidate.get("sdpMid").getAsString(), candidate.get("sdpMLineIndex").getAsInt());
                    user.addCandidate(cand, jsonMessage.get("userId").getAsLong());
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        UserSession user = registry.removeBySession(session);
        roomManager.getRoom(user.getRoomId()).leave(user);
    }

    private void joinRoom(JsonObject params, WebSocketSession session) throws IOException {
        final Long roomId = params.get("roomId").getAsLong();
        final Long userId = params.get("userId").getAsLong();
        log.info("PARTICIPANT {}: trying to join room {}", userId, roomId);
        Room room = roomManager.getRoom(roomId);
        Participant participant = Participant.from(userUtilService.getUserById(userId));
        final UserSession user = room.join(participant, session);
        registry.register(user);
    }

    private void leaveRoom(UserSession user) throws IOException {
        final Room room = roomManager.getRoom(user.getRoomId());
        room.leave(user);
        if (room.getParticipantSessions().isEmpty()) {
            roomManager.removeRoom(room);
        }
    }
}
