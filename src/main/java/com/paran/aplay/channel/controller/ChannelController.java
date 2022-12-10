package com.paran.aplay.channel.controller;

import static org.springframework.http.HttpStatus.OK;

import com.paran.aplay.channel.domain.Channel;
import com.paran.aplay.channel.dto.request.ChannelCreateRequest;
import com.paran.aplay.channel.dto.request.ChannelInviteRequest;
import com.paran.aplay.channel.dto.request.ChannelUpdateRequest;
import com.paran.aplay.channel.dto.response.ChannelDetailResponse;
import com.paran.aplay.channel.dto.response.ChannelResponse;
import com.paran.aplay.channel.service.ChannelService;
import com.paran.aplay.chat.dto.ChatResponse;
import com.paran.aplay.chat.service.ChatService;
import com.paran.aplay.common.ApiResponse;
import com.paran.aplay.common.PageResponse;
import com.paran.aplay.common.entity.CurrentUser;
import com.paran.aplay.document.dto.response.DocumentMetaResponse;
import com.paran.aplay.document.dto.response.DocumentResponse;
import com.paran.aplay.document.service.DocumentService;
import com.paran.aplay.team.domain.Team;
import com.paran.aplay.team.service.TeamService;
import com.paran.aplay.user.domain.User;
import java.net.URI;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/channels")
@SuppressWarnings({"rawtypes", "unchecked"})
public class ChannelController {
  private final ChannelService channelService;

  private final TeamService teamService;

  private final ChatService chatService;

  private final DocumentService documentService;

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
    PageResponse<ChatResponse> pageResponse = new PageResponse<>(chatService.getChatMessages(channelId, pageable));
    ApiResponse apiResponse = ApiResponse.builder()
            .message("채팅 이력 조회 성공하였습니다.")
            .status(HttpStatus.OK.value())
            .data(pageResponse)
            .build();
    return ResponseEntity.ok(apiResponse);
  }
  @PatchMapping("/{channelId}")
  public ResponseEntity<ApiResponse<List<ChannelResponse>>> updateChannel(
          @CurrentUser User user,
          @PathVariable Long channelId,
          @RequestBody @Valid ChannelUpdateRequest req
  ) {
    ApiResponse apiResponse = ApiResponse.builder()
            .message("채널 수정에 성공하였습니다.")
            .status(OK.value())
            .data(channelService.updateChannel(channelId, user, req))
            .build();
    return ResponseEntity.ok(apiResponse);
  }

  @PatchMapping("/{channelId}/invite")
  public ResponseEntity<ApiResponse> inviteUserToChannel(
          @CurrentUser User user,
          @PathVariable Long channelId,
          @RequestBody @Valid ChannelInviteRequest req
  ) {
    channelService.inviteUsersToChannel(user, channelId, req);
    ApiResponse apiResponse = ApiResponse.builder()
            .message("채널 유저 초대에 성공")
            .status(OK.value())
            .build();
    return ResponseEntity.ok(apiResponse);
  }

  @GetMapping("/{channelId}")
  public ResponseEntity<ApiResponse<List<ChannelDetailResponse>>> getChannelByUser(@PathVariable Long channelId) {
    ApiResponse apiResponse = ApiResponse.builder()
            .message("채널 단건 조회 성공하였습니다.")
            .status(OK.value())
            .data(channelService.getChannelDetailById(channelId))
            .build();
    return ResponseEntity.ok(apiResponse);
  }

  @GetMapping("/{channelId}/docs")
  public ResponseEntity<ApiResponse<PageResponse<DocumentMetaResponse>>> getDocuments(@PageableDefault(
          sort = {"createdAt"},
          direction = Sort.Direction.DESC
  ) Pageable pageable, @PathVariable Long channelId) {
    PageResponse<DocumentMetaResponse> pageResponse = new PageResponse<>(documentService.getDocuments(channelId, pageable));
    ApiResponse apiResponse = ApiResponse.builder()
            .message("문서 다건 조회 성공")
            .status(OK.value())
            .data(pageResponse)
            .build();
    return ResponseEntity.ok(apiResponse);
  }
}
