package com.paran.aplay.user.dto;

import com.paran.aplay.user.domain.User;
import com.paran.aplay.user.dto.response.SignUpResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatSender {

  private Long userId;

  private String email;

  private String name;

  public static ChatSender from(User user) {
    return ChatSender.builder()
        .userId(user.getId())
        .email(user.getEmail())
        .name(user.getName())
        .build();
  }

}
