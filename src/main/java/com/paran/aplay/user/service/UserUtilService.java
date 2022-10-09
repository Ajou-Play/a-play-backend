package com.paran.aplay.user.service;

import static com.paran.aplay.common.ErrorCode.*;

import com.paran.aplay.common.error.exception.NotFoundException;
import com.paran.aplay.user.domain.User;
import com.paran.aplay.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserUtilService {
  private final UserRepository userRepository;

  public UserUtilService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Transactional(readOnly = true)
  public User getUserById(Long userId) {
    return userRepository.findByIdAndIsQuit(userId, false).orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
  }

  @Transactional(readOnly = true)
  public boolean checkEmailUnique(String email) {
    return !userRepository.existsByEmailAndIsQuit(email, false);
  }
}
