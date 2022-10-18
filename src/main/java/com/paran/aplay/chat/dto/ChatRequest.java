package com.paran.aplay.chat.dto;

import com.paran.aplay.chat.domain.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRequest {

  private MessageType type;

  private Long senderId;

  private Long channelId;

  private String content;

}
