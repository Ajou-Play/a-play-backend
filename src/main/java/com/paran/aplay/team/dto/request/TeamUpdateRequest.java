package com.paran.aplay.team.dto.request;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamUpdateRequest {
    private String name;
    private String profileImage;
    private String description;
    private Boolean isPublic;
}
