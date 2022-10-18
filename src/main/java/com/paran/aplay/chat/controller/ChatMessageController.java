package com.paran.aplay.chat.controller;

import com.paran.aplay.chat.domain.Chat;
import com.paran.aplay.chat.domain.MessageType;
import com.paran.aplay.chat.dto.ChatRequest;
import com.paran.aplay.chat.dto.ChatResponse;
import com.paran.aplay.chat.repository.ChatRepository;
import com.paran.aplay.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
public class ChatMessageController {

  private final SimpMessageSendingOperations messagingTemplate;

  private final ChatService chatService;

  private final MessageConverter messageConverter = new MappingJackson2MessageConverter();

  @MessageMapping("/chat/message")
  public void message(ChatRequest request) {
    Chat newMessage = chatService.createChatMessage(request);
    ChatResponse response = ChatResponse.from(newMessage);
    if (request.getType().equals(MessageType.JOIN)) {
      response.setContent(response.getSender() + "님이 입장하셨습니다.");
    }
    messagingTemplate.convertAndSend("/sub/chat/message/channel/" +request.getChannelId(), response);
  }
}
