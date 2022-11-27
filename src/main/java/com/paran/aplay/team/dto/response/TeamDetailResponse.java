package com.paran.aplay.team.dto.response;

import com.paran.aplay.team.domain.Team;
import com.paran.aplay.user.domain.User;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class TeamDetailResponse {
    private final Long teamId;
    private final String name;
    private final List<TeamDetailUser> members;
    private final String profileImage;
    private final String description;
    private final Boolean isPublic;

    public static TeamDetailResponse from(Team team, List<User> members) {
        return TeamDetailResponse.builder()
                .teamId(team.getId())
                .name(team.getName())
                .members(TeamDetailUser.from(members))
                .profileImage(team.getProfileImage())
                .description(team.getDescription())
                .isPublic(team.getIsPublic())
                .build();
    }

    @Builder
    @Getter
    private static class TeamDetailUser {
        private final Long userId;
        private final String name;
        private final String email;
        private final String profileImage;
        private final String type;

        public static List<TeamDetailUser> from(List<User> users) {
            return users.stream().map(TeamDetailUser::from)
                    .collect(Collectors.toList());
        }

        public static TeamDetailUser from(User user) {
            return TeamDetailUser.builder()
                    .userId(user.getId())
                    .name(user.getName())
                    .email(user.getEmail())
                    .profileImage(user.getProfileImage())
                    .type("LOCAL")
                    .build();
        }
    }
}