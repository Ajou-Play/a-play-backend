package com.paran.aplay.jwt;

import static com.paran.aplay.common.ErrorCode.*;
import static java.util.Objects.*;
import static org.springframework.util.StringUtils.*;

import com.paran.aplay.common.error.exception.InvalidRequestException;
import com.paran.aplay.user.domain.User;
import java.security.Principal;
import lombok.Getter;

@Getter
public class JwtPrincipal implements Principal {

  private final String accessToken;

  private final User user;

  public JwtPrincipal(String accessToken, User user) {
    if(!hasText(accessToken)) throw new InvalidRequestException(EMAIL_REQUIRED);
    if(isNull(user)) throw new InvalidRequestException(USER_PARAM_REQUIRED);

    this.accessToken = accessToken;
    this.user = user;
  }

  @Override
  public String getName() {
    return user.getId().toString();
  }
}
