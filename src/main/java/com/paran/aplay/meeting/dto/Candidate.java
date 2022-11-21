package com.paran.aplay.meeting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.kurento.client.IceCandidate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Candidate {
    private String candidate;
    private String sdpMid;
    private int sdpMLineIndex;

    public static Candidate of(IceCandidate candidate) {
        return Candidate.builder()
                .candidate(candidate.getCandidate())
                .sdpMid(candidate.getSdpMid())
                .sdpMLineIndex(candidate.getSdpMLineIndex())
                .build();
    }
}
