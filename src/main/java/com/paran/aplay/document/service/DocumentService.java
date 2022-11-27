package com.paran.aplay.document.service;

import com.paran.aplay.channel.domain.Channel;
import com.paran.aplay.channel.service.ChannelUtilService;
import com.paran.aplay.document.domain.Document;
import com.paran.aplay.document.dto.request.DocumentCreateRequest;
import com.paran.aplay.document.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
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
                .build();
        return documentRepository.save(document);
    }
}
