package com.paran.aplay.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientOutboundChannelService {

    private final MessageChannel clientOutboundChannel;

    private final ObjectMapper objectMapper;

    // TODO : SneakyThrows 알아보기 및 ErrorResponse <-> ResponseEntity Scheme
    @SneakyThrows
    public void sendError(final ErrorCode reason, final String sessionId) {
        final byte[] payload = this.objectMapper.writeValueAsString(new ErrorResponse(reason.getStatus(), reason.getCode(), reason.getMessage())).getBytes();

        final StompHeaderAccessor headerAccessor = StompHeaderAccessor.create(StompCommand.ERROR);
        headerAccessor.setSessionId(sessionId);
        headerAccessor.setMessage(reason.toString());

        this.clientOutboundChannel.send(
                MessageBuilder
                        .withPayload(payload)
                        .setHeaders(headerAccessor)
                        .build()
        );
    }
}
