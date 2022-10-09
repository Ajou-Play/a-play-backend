package com.paran.aplay.user.service;

import static com.paran.aplay.common.ErrorCode.*;
import static org.springframework.util.StringUtils.*;

import com.paran.aplay.common.error.exception.InvalidRequestException;
import com.paran.aplay.common.error.exception.NotFoundException;
import com.paran.aplay.user.domain.LocalUser;
import com.paran.aplay.user.domain.User;
import com.paran.aplay.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

  private final PasswordEncoder passwordEncoder;

  private final UserRepository userRepository;

  private final UserUtilService userUtilService;

  @Transactional(readOnly = true)
  public User signIn(String principal, String credentials) {
    if (!hasText(principal) || !hasText(credentials)) {
      throw new InvalidRequestException(LOGIN_PARAM_REQUIRED);
    }
    LocalUser user = (LocalUser) userRepository.findByEmailAndIsQuit(principal, false)
        .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
    user.checkPassword(passwordEncoder, credentials);
    return user;
  }
}
