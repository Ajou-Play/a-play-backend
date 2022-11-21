package com.paran.aplay.meeting.dto.response;

import com.paran.aplay.meeting.EventType;
import com.paran.aplay.user.dto.Participant;
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
    Participant user;
}
