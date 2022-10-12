package com.paran.aplay.channel.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChannelCreateRequest {

  @NotBlank
  private String name;
  @NotNull
  private Long teamId;
}
