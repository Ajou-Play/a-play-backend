package com.paran.aplay.chat.dto;

import com.paran.aplay.chat.domain.Chat;
import com.paran.aplay.chat.domain.MessageType;
import com.paran.aplay.chat.repository.ChatRepository;
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

  private Long senderId;

  private Long channelId;

  private String content;

  public static ChatResponse from(Chat chat) {
    return ChatResponse.builder()
        .channelId(chat.getChannel().getId())
        .senderId(chat.getSender().getId())
        .content(chat.getContent())
        .type(chat.getMessageType())
        .build();
  }
}
