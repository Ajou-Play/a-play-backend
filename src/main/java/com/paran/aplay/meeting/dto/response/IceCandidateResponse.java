package com.paran.aplay.meeting.dto.response;

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
public class IceCandidateResponse {
    private EventType eventType = EventType.iceCandidate;
    private Long userId;
    private Candidate candidate;
}
