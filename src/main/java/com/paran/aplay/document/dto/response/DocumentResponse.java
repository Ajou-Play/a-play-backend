package com.paran.aplay.document.dto.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.paran.aplay.chat.domain.ChatMessage;
import com.paran.aplay.chat.dto.ChatResponse;
import com.paran.aplay.document.domain.Document;
import com.paran.aplay.document.domain.DocumentType;
import com.paran.aplay.user.domain.User;
import com.paran.aplay.user.dto.ChatSender;
import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class DocumentResponse {

    private Long documentId;

    private Long channelId;

    private String title;

    private DocumentType type;

    private String content;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createdAt;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime updatedAt;

    public static DocumentResponse from(Document document) {
        return DocumentResponse.builder()
                .documentId(document.getId())
                .channelId(document.getChannel().getId())
                .title(document.getTitle())
                .type(document.getType())
                .content(document.getContent())
                .createdAt(document.getCreatedAt())
                .updatedAt(document.getUpdatedAt())
                .build();
    }

}
