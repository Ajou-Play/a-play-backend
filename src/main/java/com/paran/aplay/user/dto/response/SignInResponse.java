package com.paran.aplay.user.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SignInResponse {

  private final Long userId;

  private final String accessToken;

  private final String refreshToken;
}
