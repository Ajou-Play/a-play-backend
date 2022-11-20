package com.paran.aplay.meeting;

import com.paran.aplay.jwt.SessionPrincipal;
import com.paran.aplay.meeting.dto.request.JoinMeetingRequest;
import com.paran.aplay.user.domain.UserRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
public class MeetingController {

    private UserRegistry registry;

    @MessageMapping("/meeting/joinMeeting")
    public void joinMeeting(SessionPrincipal principal, JoinMeetingRequest request) {

    }


}
