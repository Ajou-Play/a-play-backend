package com.paran.aplay.channel.controller;

import com.paran.aplay.channel.domain.Channel;
import com.paran.aplay.channel.dto.request.ChannelCreateRequest;
import com.paran.aplay.channel.dto.response.ChannelResponse;
import com.paran.aplay.channel.service.ChannelService;
import com.paran.aplay.chat.dto.ChatResponse;
import com.paran.aplay.chat.service.ChatService;
import com.paran.aplay.common.ApiResponse;
import com.paran.aplay.common.PageResponse;
import com.paran.aplay.common.entity.CurrentUser;
import com.paran.aplay.team.domain.Team;
import com.paran.aplay.team.service.TeamService;
import com.paran.aplay.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

import static org.springframework.http.HttpStatus.OK;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/channels")
@SuppressWarnings({"rawtypes", "unchecked"})
public class ChannelController {
  private final ChannelService channelService;

  private final TeamService teamService;

  private final ChatService chatService;

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


  // TODO : 날짜 입력 받아서 현재 시점부터 그 날짜까지 조회할 수 있도록
  @GetMapping("/{channelId}/chats")
  public ResponseEntity<ApiResponse<PageResponse<ChatResponse>>> getChatMessages(@PageableDefault(
          sort = {"createdAt"},
          direction = Sort.Direction.DESC
  ) Pageable pageable, @PathVariable Long channelId) {
    System.out.println(pageable);
    PageResponse<ChatResponse> pageResponse = new PageResponse<>(chatService.getChatMessages(channelId, pageable));
    ApiResponse apiResponse = ApiResponse.builder()
            .message("채팅 이력 조회 성공하였습니다.")
            .status(HttpStatus.OK.value())
            .data(pageResponse)
            .build();
    return ResponseEntity.ok(apiResponse);
  }
}
