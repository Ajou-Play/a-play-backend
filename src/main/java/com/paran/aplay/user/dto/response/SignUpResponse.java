package com.paran.aplay.user.dto.response;

import com.paran.aplay.user.domain.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SignUpResponse {

  private final Long userId;

  private final String email;

  private final String name;

  public static SignUpResponse from(User user) {
    return SignUpResponse.builder()
        .userId(user.getId())
        .email(user.getEmail())
        .name(user.getName())
        .build();
  }
}
