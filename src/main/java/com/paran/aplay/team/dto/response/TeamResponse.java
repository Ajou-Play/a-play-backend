package com.paran.aplay.team.dto.response;

import com.paran.aplay.team.domain.Team;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TeamResponse {
  private final Long teamId;

  private final String name;

  public static TeamResponse from(Team team) {
    return TeamResponse.builder()
        .teamId(team.getId())
        .name(team.getName())
        .build();
  }
}
