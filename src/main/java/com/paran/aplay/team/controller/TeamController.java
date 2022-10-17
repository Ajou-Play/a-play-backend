package com.paran.aplay.team.controller;

import static org.springframework.http.HttpStatus.*;

import com.paran.aplay.channel.domain.Channel;
import com.paran.aplay.channel.dto.response.ChannelResponse;
import com.paran.aplay.channel.service.ChannelService;
import com.paran.aplay.common.ApiResponse;
import com.paran.aplay.common.entity.CurrentUser;
import com.paran.aplay.team.domain.Team;
import com.paran.aplay.team.dto.request.TeamCreateRequest;
import com.paran.aplay.team.dto.response.TeamResponse;
import com.paran.aplay.team.repository.TeamRepository;
import com.paran.aplay.team.service.TeamService;
import com.paran.aplay.user.domain.Authority;
import com.paran.aplay.user.domain.User;
import com.paran.aplay.user.service.UserService;
import java.net.URI;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/teams")
@SuppressWarnings({"rawtypes", "unchecked"})
public class TeamController {
  private final TeamService teamService;
  private final ChannelService channelService;

  private final UserService userService;

  @GetMapping("/{teamId}/channels")
  public ResponseEntity<ApiResponse<List<ChannelResponse>>> getAllChannelsByTeam(@PathVariable("teamId") Long teamId) {
    Team team = teamService.getTeamById(teamId);
    List<ChannelResponse> channelResponseList = channelService.getAllChannelsByTeam(team).stream().map(
        ChannelResponse::from).toList();
    ApiResponse apiResponse = ApiResponse.builder()
        .message("팀에 해당하는 채널 다건 조회에 성공하였습니다.")
        .status(OK.value())
        .data(channelResponseList)
        .build();
    return ResponseEntity.ok(apiResponse);
  }

  @PostMapping
  public ResponseEntity<ApiResponse<TeamResponse>> createTeam(@RequestBody @Valid TeamCreateRequest request) {
    Team newTeam = teamService.createTeam(request.getName());
    ApiResponse apiResponse = ApiResponse.builder()
        .message("팀 생성에 성공했습니다.")
        .status(CREATED.value())
        .data(TeamResponse.from(newTeam))
        .build();
    channelService.createChannel(Channel.defaultName, newTeam);
    return ResponseEntity.created(URI.create("/")).body(apiResponse);
  }
}
