package com.paran.aplay.chat.controller;

import com.paran.aplay.chat.domain.Chat;
import com.paran.aplay.chat.domain.MessageType;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
public class ChatMessageController {

  private final SimpMessageSendingOperations messagingTemplate;

  @MessageMapping("/chat/message")
  public void message(Chat message) {
    if (message.getMessageType().equals(MessageType.JOIN)) {
      message.setContent(message.getSender().getId() + "님이 입장하셨습니다.");
    }
    messagingTemplate.convertAndSend("/sub/chat/message/channel/" + message.getChannel().getId(), message);
  }
}
