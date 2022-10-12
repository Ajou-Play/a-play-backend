package com.paran.aplay.channel.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Builder
@Getter
public class ChannelCreateResponse {

  private final Long channelId;

  private final String name;

  private final Long teamId;
}
