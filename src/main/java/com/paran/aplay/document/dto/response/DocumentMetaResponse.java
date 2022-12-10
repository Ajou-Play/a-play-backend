package com.paran.aplay.document.dto.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.paran.aplay.document.domain.Document;
import com.paran.aplay.document.domain.DocumentType;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class DocumentMetaResponse {

    private Long documentId;

    private Long channelId;

    private String title;

    private DocumentType type;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createdAt;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime updatedAt;

    public static DocumentMetaResponse from(Document document) {
        return DocumentMetaResponse.builder()
                .documentId(document.getId())
                .channelId(document.getChannel().getId())
                .title(document.getTitle())
                .type(document.getType())
                .createdAt(document.getCreatedAt())
                .updatedAt(document.getUpdatedAt())
                .build();
    }

}

