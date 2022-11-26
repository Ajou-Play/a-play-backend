package com.paran.aplay.user.dto;

import com.paran.aplay.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Participant {
    private Long userId;

    private String email;

    private String name;

    private String profileImage;

    public static Participant from(User user) {
        return Participant.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .profileImage(user.getProfileImage())
                .build();
    }
}
