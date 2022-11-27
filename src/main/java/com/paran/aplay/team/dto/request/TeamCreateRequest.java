package com.paran.aplay.team.dto.request;

import com.paran.aplay.chat.domain.MessageType;
import javax.validation.constraints.NotBlank;

import lombok.*;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamCreateRequest {
  @NotBlank
  private String name;
  private String description;
  private String[] members;
  private Boolean isPublic;
}
