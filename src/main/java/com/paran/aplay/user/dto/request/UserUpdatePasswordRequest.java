package com.paran.aplay.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdatePasswordRequest {
    @NotBlank
    private String oldPassword;
    @NotBlank
    private String newPassword;
}
