package com.paran.aplay.common.config;

import com.paran.aplay.common.interceptor.ClientInboundChannelInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kurento.client.KurentoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Slf4j
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketStompConfig implements WebSocketMessageBrokerConfigurer {

    private final ClientInboundChannelInterceptor clientInboundChannelInterceptor;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/sub"); // 메세지 구독 요청
        config.setApplicationDestinationPrefixes("/pub"); // 메세지 발행 요청
    }

    @Override
    public final void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(clientInboundChannelInterceptor);
    }

    @Override
    public final void configureClientOutboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(final Message<?> message, final MessageChannel channel) {
                final StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
                log.info("OUTGOING {}", accessor.getDetailedLogMessage(message.getPayload()));
                return message;
            }
        });
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/api/socket/chat", "/api/socket/meeting").setAllowedOriginPatterns("*")
                .withSockJS();
    }
}
