package com.paran.aplay.document.service;

import com.paran.aplay.channel.domain.Channel;
import com.paran.aplay.channel.service.ChannelUtilService;
import com.paran.aplay.chat.domain.ChatMessage;
import com.paran.aplay.chat.dto.ChatResponse;
import com.paran.aplay.common.ErrorCode;
import com.paran.aplay.common.error.exception.NotFoundException;
import com.paran.aplay.document.domain.Document;
import com.paran.aplay.document.dto.request.DocumentCreateRequest;
import com.paran.aplay.document.dto.response.DocumentMetaResponse;
import com.paran.aplay.document.dto.response.DocumentResponse;
import com.paran.aplay.document.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;

    private final ChannelUtilService channelUtilService;

    @Transactional
    public Document createDocument(DocumentCreateRequest createRequest) {
        Channel channel = channelUtilService.getChannelById(createRequest.getChannelId());
        Document document = Document.builder()
                .type(createRequest.getType())
                .channel(channel)
                .title(createRequest.getTitle())
                .content("")
                .build();
        return documentRepository.save(document);
    }

    public Page<DocumentMetaResponse> getDocuments(Long channelId, Pageable pageable) {
        channelUtilService.validateChannelId(channelId);
        Page<Document> documents = documentRepository.findDoucmentsByChannelId(channelId, pageable);
        return documents.map(DocumentMetaResponse::from);
    }

    public Document getDocumentById(Long documentId) {
        return documentRepository.findById(documentId).orElseThrow(() -> new NotFoundException(ErrorCode.DOCUMENT_NOT_FOUND));
    }
}
