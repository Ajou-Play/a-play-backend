package com.paran.aplay.user.dto.response;

import com.paran.aplay.user.domain.User;
import com.paran.aplay.user.dto.ChatSender;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class UserDetailResponse {
    private final Long userId;

    private final String email;

    private final String name;

    private final String profileImage;

    private final boolean type;

    public static UserDetailResponse from(User user) {
        return UserDetailResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .profileImage(user.getProfileImage())
                .build();
    }
}
