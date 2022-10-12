package com.paran.aplay.team.service;

import com.paran.aplay.common.ErrorCode;
import com.paran.aplay.common.error.exception.NotFoundException;
import com.paran.aplay.team.domain.Team;
import com.paran.aplay.team.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TeamService {
  private final TeamRepository teamRepository;

  public Team getTeamById(Long teamId) {
    return teamRepository.findById(teamId).orElseThrow(() -> new NotFoundException(ErrorCode.TEAM_NOT_FOUND));
  }
}
