package com.paran.aplay.meeting.dto.request;

import com.paran.aplay.meeting.EventType;
import com.paran.aplay.meeting.dto.Candidate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveMeetingRequest {
    private EventType eventType;
    private Long userId;
}
