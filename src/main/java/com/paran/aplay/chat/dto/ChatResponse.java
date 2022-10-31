package com.paran.aplay.chat.dto;

import com.paran.aplay.chat.domain.ChatMessage;
import com.paran.aplay.chat.domain.MessageType;
import com.paran.aplay.user.domain.User;
import com.paran.aplay.user.dto.ChatSender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatResponse {
  private MessageType type;

  private ChatSender sender;

  private Long channelId;

  private String content;

  public static ChatResponse from(ChatMessage chatMessage) {
    User sender = chatMessage.getSender();
    return ChatResponse.builder()
        .channelId(chatMessage.getChannel().getId())
        .sender(ChatSender.from(chatMessage.getSender()))
        .content(chatMessage.getContent())
        .type(chatMessage.getMessageType())
        .build();
  }

  public void setContent(String content) {
    this.content = content;
  }
}
