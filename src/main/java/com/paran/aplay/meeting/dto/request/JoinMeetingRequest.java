package com.paran.aplay.meeting.dto.request;

import com.paran.aplay.meeting.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JoinMeetingRequest {
    private EventType eventType;
    private Long userId;
    private Long channelId;
}
