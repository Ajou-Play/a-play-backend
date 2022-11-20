package com.paran.aplay.meeting.dto.response;

import com.paran.aplay.meeting.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLeftResponse {
    EventType eventType;
}
