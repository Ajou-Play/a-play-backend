package com.paran.aplay.channel.dto.response;

import com.paran.aplay.channel.domain.Channel;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ChannelResponse {

  private final Long channelId;

  private final String name;

  private final Long teamId;

  public static ChannelResponse from(Channel channel) {
    return ChannelResponse.builder()
        .channelId(channel.getId())
        .name(channel.getName())
        .teamId(channel.getTeam().getId())
        .build();
  }
}
