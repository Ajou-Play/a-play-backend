package com.paran.aplay.common.util;

import com.paran.aplay.meeting.EventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public final class StompMessagingService {

    private static final String CHAT_DESTINATION_PREFIX = "/sub/chat/message/channel/";

    private static final String MEETING_DESTINATION_PREFIX = "/sub/meeting/user/";
    private final SimpMessagingTemplate messagingTemplate;

    public <D> void sendToChannel(Long channelId, D message) {
        messagingTemplate.convertAndSend(CHAT_DESTINATION_PREFIX+channelId, message);
    }
    public <D> void sendToUser(Long userId, EventType type, D message) {
        String destination = MEETING_DESTINATION_PREFIX+userId+"/"+type;
        log.info("destination : {}",destination);
        messagingTemplate.convertAndSend(MEETING_DESTINATION_PREFIX+userId+"/"+type, message);
    }

}