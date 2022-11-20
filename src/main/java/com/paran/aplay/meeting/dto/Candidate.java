package com.paran.aplay.meeting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Candidate {
    private String candidate;
    private String sdpMid;
    private int sdpMLineIndex;
}
