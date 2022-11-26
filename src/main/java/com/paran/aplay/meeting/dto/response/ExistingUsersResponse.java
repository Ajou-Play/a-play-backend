package com.paran.aplay.meeting.dto.response;

import com.paran.aplay.meeting.EventType;
import com.paran.aplay.user.dto.Participant;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExistingUsersResponse {
    private EventType eventType = EventType.existingUsers;
    private List<Participant> data = new ArrayList<>();
}
