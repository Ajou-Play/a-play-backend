package com.paran.aplay.channel.controller;

import static org.springframework.http.HttpStatus.*;

import com.paran.aplay.channel.domain.Channel;
import com.paran.aplay.channel.dto.request.ChannelCreateRequest;
import com.paran.aplay.channel.dto.response.ChannelResponse;
import com.paran.aplay.channel.service.ChannelService;
import com.paran.aplay.common.ApiResponse;
import com.paran.aplay.common.PageResponse;
import com.paran.aplay.common.entity.CurrentUser;
import com.paran.aplay.team.domain.Team;
import com.paran.aplay.team.service.TeamService;

import com.paran.aplay.user.domain.User;
import java.net.URI;
import java.net.UnknownServiceException;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/channels")
@SuppressWarnings({"rawtypes", "unchecked"})
public class ChannelController {
  private final ChannelService channelService;

  private final TeamService teamService;

  @PostMapping
  public ResponseEntity<ApiResponse<ChannelResponse>> createChannel(@CurrentUser User user, @RequestBody @Valid ChannelCreateRequest request) {
    Team team = teamService.getTeamById(request.getTeamId());
    Channel channel = channelService.createChannel(request.getName(), team);
    channelService.inviteUserToChannel(user, channel);
    ChannelResponse channelResponse = ChannelResponse.builder()
        .channelId(channel.getId())
        .name(channel.getName())
        .teamId(channel.getTeam().getId())
        .build();
    ApiResponse response = ApiResponse.builder()
        .message("채널 생성 성공하였습니다.")
        .status(OK.value())
        .data(channelResponse)
        .build();
    return ResponseEntity.created(URI.create("/")).body(response);
  }
}
