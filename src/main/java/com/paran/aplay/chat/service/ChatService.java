package com.paran.aplay.chat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paran.aplay.channel.domain.Channel;
import com.paran.aplay.channel.service.ChannelService;
import com.paran.aplay.chat.domain.Chat;
import com.paran.aplay.chat.dto.ChatRequest;
import com.paran.aplay.chat.dto.ChatResponse;
import com.paran.aplay.user.domain.User;
import com.paran.aplay.user.service.UserUtilService;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Service
@RequiredArgsConstructor
public class ChatService {

  private final Logger log = LoggerFactory.getLogger(getClass());
  private final ObjectMapper objectMapper;

  private final UserUtilService userUtilService;

  private final ChannelService channelService;

  @Transactional(readOnly = true)
  public Chat createChatMessage (ChatRequest chatRequest) {
    User sender = userUtilService.getUserById(chatRequest.getSenderId());
    Channel channel = channelService.getChannelById(chatRequest.getChannelId());
    Chat chat = Chat.builder()
        .sender(sender)
        .channel(channel)
        .content(chatRequest.getContent())
        .build();
    chat.setMessageType(chatRequest.getType());
    return chat;
  }
}
