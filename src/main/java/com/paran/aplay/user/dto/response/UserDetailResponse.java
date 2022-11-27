package com.paran.aplay.user.dto.response;

import com.paran.aplay.user.domain.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserDetailResponse {
    private Long userId;
    private String name;
    private String email;
    private String profileImage;
    private String type;

    public static UserDetailResponse from(User user) {
        return UserDetailResponse.builder()
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .profileImage(user.getProfileImage())
                .type("LOCAL")
                .build();
    }
}
