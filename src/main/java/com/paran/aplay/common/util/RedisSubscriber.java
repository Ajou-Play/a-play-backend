package com.paran.aplay.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paran.aplay.chat.domain.ChatMessage;
import com.paran.aplay.chat.domain.MessageType;
import com.paran.aplay.chat.dto.ChatRequest;
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
public class RedisSubscriber implements MessageListener {

  private final ObjectMapper objectMapper;
  private final RedisTemplate redisTemplate;
  private final SimpMessageSendingOperations messagingTemplate;

  private final ChatService chatService;

  @Override
  public void onMessage(Message message, byte[] pattern) {
    try{
      String publishedMessage = (String) redisTemplate.getStringSerializer().deserialize(message.getBody());
      ChatResponse chatResponse = objectMapper.readValue(publishedMessage, ChatResponse.class);
      System.out.println("type : "+chatResponse.getType()+"\nsenderName : "+chatResponse.getSender().getEmail()+"\nmessage : "+chatResponse.getContent());
      messagingTemplate.convertAndSend("/sub/chat/message/channel/" +chatResponse.getChannelId(), chatResponse);
    } catch (Exception e) {
      log.error(e.getMessage());
    }
  }
}
