package com.paran.aplay.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paran.aplay.chat.dto.ChatResponse;
import com.paran.aplay.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisSubscriber {

  private final ObjectMapper objectMapper;
  private final RedisTemplate redisTemplate;

  private final StompMessagingService messagingService;

  public void onChatMessage(Message message) {
    try{
      String publishedMessage = (String) redisTemplate.getStringSerializer().deserialize(message.getBody());
      ChatResponse chatResponse = objectMapper.readValue(publishedMessage, ChatResponse.class);
      messagingService.sendToChannel(chatResponse.getChannelId(), chatResponse);
    } catch (Exception e) {
      log.error(e.getMessage());
    }
  }

  public void onMeetingMessage(Message message) {

  }
}
