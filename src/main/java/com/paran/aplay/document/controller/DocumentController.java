package com.paran.aplay.document.controller;

import static org.springframework.http.HttpStatus.OK;

import com.paran.aplay.common.ApiResponse;
import com.paran.aplay.document.domain.Document;
import com.paran.aplay.document.dto.request.DocumentCreateRequest;
import com.paran.aplay.document.dto.request.DocumentUpdateRequest;
import com.paran.aplay.document.dto.response.DocumentResponse;
import com.paran.aplay.document.service.DocumentService;
import java.net.URI;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping("/{documentId}")
    public ResponseEntity<ApiResponse<DocumentResponse>> getDocument(@PathVariable Long documentId) {
        DocumentResponse documentResponse = DocumentResponse.from(documentService.getDocumentById(documentId));
        ApiResponse apiResponse = ApiResponse.builder()
                .message("문서 단건 조회")
                .status(OK.value())
                .data(documentResponse)
                .build();
        return ResponseEntity.ok().body(apiResponse);
    }

    @PatchMapping("/{documentId}")
    public ResponseEntity<ApiResponse<DocumentResponse>> updateDocument(@PathVariable Long documentId, @RequestBody
    DocumentUpdateRequest updateRequest) {
        Document document = documentService.getDocumentById(documentId);
        Document updatedDocument = documentService.saveDocument(document, updateRequest);
        DocumentResponse documentResponse = DocumentResponse.from(updatedDocument);
        ApiResponse apiResponse = ApiResponse.builder()
                .message("문서 저장 성공")
                .status(OK.value())
                .data(documentResponse)
                .build();
        return ResponseEntity.ok().body(apiResponse);
    }


}
