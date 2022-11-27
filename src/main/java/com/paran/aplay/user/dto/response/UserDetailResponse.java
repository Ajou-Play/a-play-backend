package com.paran.aplay.user.dto.response;

import com.paran.aplay.user.domain.LocalUser;
import com.paran.aplay.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class UserDetailResponse {
    private final Long userId;

    private final String email;

    private final String name;

    private final String profileImage;

    private String type;

    public static UserDetailResponse from(User user) {
        return UserDetailResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .profileImage(user.getProfileImage())
                .type(user instanceof LocalUser ? "LOCAL" : "SOCIAL")
                .build();
    }
}
