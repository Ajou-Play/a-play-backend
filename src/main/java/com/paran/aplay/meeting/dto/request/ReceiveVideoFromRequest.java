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
public class ReceiveVideoFromRequest {
    private EventType eventType;
    private Long userId;
    private String sdpOffer;
}
