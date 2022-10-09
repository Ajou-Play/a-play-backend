package com.paran.aplay.user.dto.request;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSignUpRequest {
  @NotBlank
  private String email;

  @NotBlank
  private String name;

  @NotBlank
  private String password;
}
