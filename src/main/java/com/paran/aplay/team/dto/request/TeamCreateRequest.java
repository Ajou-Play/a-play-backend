package com.paran.aplay.team.dto.request;

import com.paran.aplay.chat.domain.MessageType;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamCreateRequest {
  @NotBlank
  private String name;
}
