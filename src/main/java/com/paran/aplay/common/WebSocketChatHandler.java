package com.paran.aplay.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paran.aplay.channel.domain.Channel;
import com.paran.aplay.channel.service.ChannelService;
import com.paran.aplay.chat.domain.Chat;
import com.paran.aplay.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketChatHandler extends TextWebSocketHandler{

  private final ObjectMapper objectMapper;

  private final ChannelService channelService;

  private final ChatService chatService;
  @Override
  protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception{
    //String payload = new String(message.getPayload().array(), StandardCharsets.UTF_8);
    String payload = message.getPayload();
    log.info("payload {}", payload);
    Chat chat = objectMapper.readValue(payload, Chat.class);
    Channel channel = channelService.getChannelById(chat.getChannel().getId());
    channel.handleActions(session, chat, chatService);
  }

}
