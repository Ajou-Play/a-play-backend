package com.paran.aplay.chat.controller;

import com.paran.aplay.chat.dto.ChatRequest;
import com.paran.aplay.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
public class ChatMessageController {
  private final ChatService chatService;

  @MessageMapping("/chat/message")
  public void message(ChatRequest request) {
    chatService.sendMessage(request);
  }
}
