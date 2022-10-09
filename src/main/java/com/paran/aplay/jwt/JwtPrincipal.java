package com.paran.aplay.jwt;

import static com.paran.aplay.common.ErrorCode.*;
import static java.util.Objects.*;
import static org.springframework.util.StringUtils.*;

import com.paran.aplay.common.error.exception.InvalidRequestException;
import com.paran.aplay.user.domain.User;
import lombok.Getter;

@Getter
public class JwtPrincipal {

  private final String accessToken;

  private final User user;

  JwtPrincipal(String accessToken, User user) {
    if(!hasText(accessToken)) throw new InvalidRequestException(EMAIL_REQUIRED);
    if(isNull(user)) throw new InvalidRequestException(USER_PARAM_REQUIRED);

    this.accessToken = accessToken;
    this.user = user;
  }
}
