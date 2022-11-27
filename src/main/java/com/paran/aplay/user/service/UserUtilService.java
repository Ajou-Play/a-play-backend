package com.paran.aplay.user.service;

import static com.paran.aplay.common.ErrorCode.*;

import com.paran.aplay.channel.domain.Channel;
import com.paran.aplay.common.error.exception.NotFoundException;
import com.paran.aplay.team.domain.Team;
import com.paran.aplay.user.domain.User;
import com.paran.aplay.user.domain.UserTeam;
import com.paran.aplay.user.repository.UserChannelRepository;
import com.paran.aplay.user.repository.UserRepository;
import com.paran.aplay.user.repository.UserTeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class UserUtilService {
  private final UserRepository userRepository;

  private final UserChannelRepository userChannelRepository;

  private final UserTeamRepository userTeamRepository;

  @Transactional(readOnly = true)
  public User getUserById(Long userId) {
    return userRepository.findByIdAndIsQuit(userId, false).orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
  }

  @Transactional(readOnly = true)
  public boolean checkEmailUnique(String email) {
    return !userRepository.existsByEmailAndIsQuit(email, false);
  }

  @Transactional(readOnly = true)
  public boolean checkUserExistsInChannel(User user, Channel channel) {
    return userChannelRepository.existsByUserIdAndChannelId(user.getId(), channel.getId());
  }

  @Transactional(readOnly = true)
  public boolean checkUserExistsInTeam(User user, Team team) {
    return userTeamRepository.existsByUserIdAndTeamId(user.getId(), team.getId());
  }

  @Transactional()
  public void deleteUserFromTeam(User user, Team team) {
    UserTeam userTeam = userTeamRepository.findByUserIdAndTeamId(user.getId(), team.getId());
    userTeamRepository.delete(userTeam);
    return;
  }

  @Transactional(readOnly = true)
  public List<UserTeam> getUserTeamsByTeam(Team team) {
    return userTeamRepository.findAllByTeamId(team.getId());
  }

  @Transactional(readOnly = true)
  public List<UserTeam> getUserTeamsByUser(User user) {
    return userTeamRepository.findAllByUserId(user.getId());
  }
}
