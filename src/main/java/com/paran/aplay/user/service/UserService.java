package com.paran.aplay.user.service;

import static com.paran.aplay.common.ErrorCode.*;
import static org.springframework.util.StringUtils.*;

import com.paran.aplay.channel.domain.Channel;
import com.paran.aplay.common.error.exception.AlreadyExistsException;
import com.paran.aplay.common.error.exception.InvalidRequestException;
import com.paran.aplay.common.error.exception.NotFoundException;
import com.paran.aplay.common.error.exception.PermissionDeniedException;
import com.paran.aplay.team.domain.Team;
import com.paran.aplay.user.domain.Authority;
import com.paran.aplay.user.domain.LocalUser;
import com.paran.aplay.user.domain.User;
import com.paran.aplay.user.domain.UserChannel;
import com.paran.aplay.user.domain.UserTeam;
import com.paran.aplay.user.dto.request.UserSignUpRequest;
import com.paran.aplay.user.repository.UserChannelRepository;
import com.paran.aplay.user.repository.UserRepository;
import com.paran.aplay.user.repository.UserTeamRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

  private final PasswordEncoder passwordEncoder;

  private final UserRepository userRepository;


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

  @Transactional
  public User signUp(UserSignUpRequest request) {
    if (userRepository.existsByEmailAndIsQuit(request.getEmail(), false)) {
      throw new AlreadyExistsException(USER_ALREADY_EXISTS);
    }

    //TODO: 이메일 인증 로직

    LocalUser.validatePassword(request.getPassword());

    User newUser = LocalUser.builder()
        .name(request.getName())
        .email(request.getEmail())
        .password(passwordEncoder.encode(request.getPassword()))
        .authority(Authority.EDITOR)
        .build();

    return userRepository.save(newUser);
  }


}
