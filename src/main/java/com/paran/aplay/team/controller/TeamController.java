package com.paran.aplay.team.controller;

import static org.springframework.http.HttpStatus.*;

import com.paran.aplay.channel.domain.Channel;
import com.paran.aplay.channel.dto.response.ChannelResponse;
import com.paran.aplay.channel.service.ChannelService;
import com.paran.aplay.common.ApiResponse;
import com.paran.aplay.common.entity.CurrentUser;
import com.paran.aplay.team.domain.Team;
import com.paran.aplay.team.dto.request.TeamCreateRequest;
import com.paran.aplay.team.dto.request.TeamUpdateRequest;
import com.paran.aplay.team.dto.response.TeamDetailResponse;
import com.paran.aplay.team.dto.response.TeamResponse;
import com.paran.aplay.team.service.TeamService;
import com.paran.aplay.user.domain.User;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/teams")
@SuppressWarnings({"rawtypes", "unchecked"})
public class TeamController {
  private final TeamService teamService;
  private final ChannelService channelService;

  @GetMapping
  public ResponseEntity<ApiResponse<List<TeamResponse>>> getUserTeams(@CurrentUser User user) {
    List<TeamResponse> res = teamService.getAllTeamByUser(user);
    ApiResponse apiResponse = ApiResponse.builder()
            .message("유저가 속한 팀 조회에 성공하였습니다.")
            .status(OK.value())
            .data(res)
            .build();
    return ResponseEntity.ok(apiResponse);
  }

  @GetMapping("/{teamId}")
  public ResponseEntity<ApiResponse<TeamDetailResponse>> getTeamDetailByTeam(@CurrentUser User user, @PathVariable("teamId") Long teamId) {
    TeamDetailResponse res = teamService.getTeamDetailById(user, teamId);
    ApiResponse apiResponse = ApiResponse.builder()
            .message("팀에 해당하는 상세한 정보 조회에 성공하였습니다.")
            .status(OK.value())
            .data(res)
            .build();
    return ResponseEntity.ok(apiResponse);
  }

  @PatchMapping("/{teamId}")
  public ResponseEntity<ApiResponse> leaveUserFromTeam(@CurrentUser User user, @PathVariable("teamId") Long teamId) {
    Boolean res = teamService.deleteUserFromTeam(user, teamId);
    ApiResponse apiResponse = ApiResponse.builder()
            .message("팀 탈퇴 성공하였습니다.")
            .status(OK.value())
            .build();
    return ResponseEntity.ok(apiResponse);
  }

  @DeleteMapping("/{teamId}")
  public ResponseEntity<ApiResponse> removeTeam(@CurrentUser User user, @PathVariable("teamId") Long teamId) {
    Boolean res = teamService.deleteTeam(user, teamId);
    ApiResponse apiResponse = ApiResponse.builder()
            .message("팀 삭제 성공하였습니다.")
            .status(OK.value())
            .build();
    return ResponseEntity.ok(apiResponse);
  }

  @GetMapping("/{teamId}/channels")
  public ResponseEntity<ApiResponse<List<ChannelResponse>>> getAllChannelsByTeam(@CurrentUser User user, @PathVariable("teamId") Long teamId) {
    Team team = teamService.validateTeamByUser(user, teamId);
    List<ChannelResponse> channelResponseList = channelService.getAllChannelsByTeam(team).stream().map(
        ChannelResponse::from).toList();
    ApiResponse apiResponse = ApiResponse.builder()
        .message("팀에 해당하는 채널 다건 조회에 성공하였습니다.")
        .status(OK.value())
        .data(channelResponseList)
        .build();
    return ResponseEntity.ok(apiResponse);
  }

  @PutMapping(value = "/{teamId}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
  public ResponseEntity<ApiResponse<TeamDetailResponse>> updateTeam(
          @CurrentUser User user,
          @PathVariable("teamId") Long teamId,
          @RequestPart("data") TeamUpdateRequest request,
          @RequestPart(value = "profileImage", required = false) MultipartFile image
          ) {
    Team team = teamService.validateTeamByUser(user, teamId);
    teamService.updateTeam(team, request, image);

    TeamDetailResponse res = teamService.getTeamDetailById(user, teamId);
    ApiResponse apiResponse = ApiResponse.builder()
            .message("팀에 해당하는 상세한 정보 수정에 성공하였습니다.")
            .status(OK.value())
            .data(res)
            .build();
    return ResponseEntity.ok(apiResponse);
  }


  @PostMapping
  public ResponseEntity<ApiResponse<TeamResponse>> createTeam(@CurrentUser User user, @RequestBody @Valid TeamCreateRequest request) {
    Team newTeam = teamService.createTeam(request.getName());
    teamService.inviteUserToTeam(user, newTeam);
    Channel generalChannel = channelService.createChannel(Channel.defaultName, newTeam);
    channelService.inviteUserToChannel(user, generalChannel);
    ApiResponse apiResponse = ApiResponse.builder()
        .message("팀 생성에 성공했습니다.")
        .status(CREATED.value())
        .data(TeamResponse.from(newTeam))
        .build();
    return ResponseEntity.created(URI.create("/")).body(apiResponse);
  }
}
