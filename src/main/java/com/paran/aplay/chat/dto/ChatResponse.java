package com.paran.aplay.chat.dto;

import com.paran.aplay.chat.domain.Chat;
import com.paran.aplay.chat.domain.MessageType;
import com.paran.aplay.chat.repository.ChatRepository;
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

  public static ChatResponse from(Chat chat) {
    User sender = chat.getSender();
    return ChatResponse.builder()
        .channelId(chat.getChannel().getId())
        .sender(ChatSender.from(chat.getSender()))
        .content(chat.getContent())
        .type(chat.getMessageType())
        .build();
  }

  public void setContent(String content) {
    this.content = content;
  }
}
