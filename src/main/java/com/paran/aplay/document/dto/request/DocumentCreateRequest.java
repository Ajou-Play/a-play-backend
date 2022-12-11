package com.paran.aplay.document.dto.request;

import com.paran.aplay.document.domain.DocumentType;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentCreateRequest {

    @NotNull
    private Long channelId;

    private String title;

    private DocumentType type;
}
