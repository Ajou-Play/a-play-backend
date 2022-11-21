package com.paran.aplay.meeting;

import com.paran.aplay.jwt.JwtPrincipal;
import com.paran.aplay.meeting.dto.request.JoinMeetingRequest;
import com.paran.aplay.meeting.dto.request.LeaveMeetingRequest;
import com.paran.aplay.meeting.dto.request.OnIceCandidateRequest;
import com.paran.aplay.meeting.dto.request.ReceiveVideoFromRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Slf4j
@RequiredArgsConstructor
@Controller
@MessageMapping("/meeting")
public class MeetingController {

    private final WebRtcService webRtcService;

    @MessageMapping("/joinMeeting")
    public void joinMeeting(JwtPrincipal principal, JoinMeetingRequest request) {
        log.info("INCOMING JOIN, user {}", principal.getUser().getId());
        webRtcService.join(principal.getUser(), request.getChannelId());
    }

    @MessageMapping("/onIceCandidate")
    public void onIceCandidate(JwtPrincipal principal, OnIceCandidateRequest request) {
        log.info("INCOMING onIceCandidate, user {}", principal.getUser().getId());

    }

    @MessageMapping("/receiveVideoFrom")
    public void receiveVideoFrom(JwtPrincipal principal, ReceiveVideoFromRequest request) {
        log.info("INCOMING receiveVideoFrom, user {}", principal.getUser().getId());
        webRtcService.offer(principal.getUser().getId(), request.getUserId(), request.getSdpOffer());
    }

    @MessageMapping("/leaveMeeting")
    public void joinMeeting(JwtPrincipal principal, LeaveMeetingRequest request) {
        log.info("INCOMING JOIN, user {}", principal.getUser().getId());
    }

}
