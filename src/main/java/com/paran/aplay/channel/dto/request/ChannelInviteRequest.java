package com.paran.aplay.channel.dto.request;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ChannelInviteRequest {
    @NotBlank
    private String[] members;
}
