package com.paran.aplay.team.service;

import static com.paran.aplay.common.ErrorCode.*;

import com.paran.aplay.common.error.exception.AlreadyExistsException;
import com.paran.aplay.common.error.exception.NotFoundException;
import com.paran.aplay.team.domain.Team;
import com.paran.aplay.team.repository.TeamRepository;
import com.paran.aplay.user.domain.User;
import com.paran.aplay.user.domain.UserTeam;
import com.paran.aplay.user.repository.UserTeamRepository;
import com.paran.aplay.user.service.UserUtilService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TeamService {
  private final TeamRepository teamRepository;

  private final UserTeamRepository userTeamRepository;

  private final UserUtilService userUtilService;

  @Transactional(readOnly = true)
  public Team getTeamById(Long teamId) {
    return teamRepository.findById(teamId).orElseThrow(() -> new NotFoundException(TEAM_NOT_FOUND));
  }

  @Transactional
  public Team createTeam(String name) {
    Team team = new Team(name);
    return teamRepository.save(team);
  }

  @Transactional
  public void inviteUserToTeam(User user, Team team) {
    if (userUtilService.checkUserExistsInTeam(user, team)) throw new AlreadyExistsException(USER_ALREADY_IN_TEAM);
    UserTeam userTeam = new UserTeam(user, team);
    userTeamRepository.save(userTeam);
  }
}
