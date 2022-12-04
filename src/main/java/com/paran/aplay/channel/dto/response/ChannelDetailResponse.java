package com.paran.aplay.channel.dto.response;

import com.paran.aplay.channel.domain.Channel;
import com.paran.aplay.user.domain.User;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ChannelDetailResponse {
    private final Long channelId;

    private final String name;

    private final Long teamId;

    private final List<ChannelDetailUser> members;

    private final String profileImage;

    public static ChannelDetailResponse from(Channel channel, List<User> members) {
        return ChannelDetailResponse.builder()
                .teamId(channel.getTeam().getId())
                .channelId(channel.getId())
                .name(channel.getName())
                .members(ChannelDetailUser.from(members))
                .profileImage("")
                .build();
    }


    @Builder
    @Getter
    private static class ChannelDetailUser {
        private final Long userId;
        private final String name;
        private final String email;
        private final String profileImage;

        public static List<ChannelDetailResponse.ChannelDetailUser> from(List<User> users) {
            return users.stream().map(ChannelDetailResponse.ChannelDetailUser::from)
                    .collect(Collectors.toList());
        }

        public static ChannelDetailResponse.ChannelDetailUser from(User user) {
            return ChannelDetailResponse.ChannelDetailUser.builder()
                    .userId(user.getId())
                    .name(user.getName())
                    .email(user.getEmail())
                    .profileImage(user.getProfileImage())
                    .build();
        }
    }
}
