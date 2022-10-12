package com.paran.aplay.channel.controller;

import static org.springframework.http.HttpStatus.*;

import com.paran.aplay.channel.domain.Channel;
import com.paran.aplay.channel.dto.request.ChannelCreateRequest;
import com.paran.aplay.channel.dto.response.ChannelCreateResponse;
import com.paran.aplay.channel.service.ChannelService;
import com.paran.aplay.common.ApiResponse;
import com.paran.aplay.team.domain.Team;
import com.paran.aplay.team.service.TeamService;
import java.net.URI;
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
  public ResponseEntity<ApiResponse<ChannelCreateResponse>> createChannel(@RequestBody @Valid ChannelCreateRequest request) {
    Team team = teamService.getTeamById(request.getTeamId());
    Channel channel = channelService.createChannel(request.getName(), team);
    ChannelCreateResponse channelResponse = ChannelCreateResponse.builder()
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
