package com.paran.aplay.document.controller;

import static org.springframework.http.HttpStatus.OK;

import com.paran.aplay.common.ApiResponse;
import com.paran.aplay.document.dto.request.DocumentCreateRequest;
import com.paran.aplay.document.dto.response.DocumentResponse;
import com.paran.aplay.document.service.DocumentService;
import java.net.URI;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/docs")
@SuppressWarnings({"rawtypes", "unchecked"})
public class DocumentController {
    private final DocumentService documentService;

    @PostMapping
    public ResponseEntity<ApiResponse<DocumentResponse>> createDocument(
            @RequestBody @Valid DocumentCreateRequest createRequest) {
        DocumentResponse documentResponse = DocumentResponse.from(documentService.createDocument(createRequest));
        ApiResponse apiResponse = ApiResponse.builder()
                .message("문서 생성 성공")
                .status(OK.value())
                .data(documentResponse)
                .build();
        return ResponseEntity.created(URI.create("/")).body(apiResponse);
    }
}
