package com.paran.aplay.common.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paran.aplay.chat.dto.ChatResponse;
import com.paran.aplay.common.util.StompMessagingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatSubscriber implements MessageListener{
  private final ObjectMapper objectMapper;
  private final RedisTemplate redisTemplate;

  private final StompMessagingService messagingService;

  @Override
  public void onMessage(Message message, byte[] pattern) {
    try{
      String publishedMessage = (String) redisTemplate.getStringSerializer().deserialize(message.getBody());
      ChatResponse chatResponse = objectMapper.readValue(publishedMessage, ChatResponse.class);
      messagingService.sendToChannel(chatResponse.getChannelId(), chatResponse);
    } catch (Exception e) {
      log.error(e.getMessage());
    }
  }
}
