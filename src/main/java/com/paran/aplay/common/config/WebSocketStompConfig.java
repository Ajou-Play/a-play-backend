package com.paran.aplay.common.config;

import com.paran.aplay.common.SessionHandshakeHandler;
import com.paran.aplay.meeting.RoomManager;
import com.paran.aplay.user.domain.UserRegistry;
import com.paran.aplay.user.service.UserUtilService;
import lombok.RequiredArgsConstructor;
import org.kurento.client.KurentoClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketStompConfig implements WebSocketMessageBrokerConfigurer {

  private final UserUtilService utilService;

  @Bean
  public UserRegistry registry() {
    return new UserRegistry();
  }

  @Bean
  public RoomManager roomManager() {
    return new RoomManager();
  }

  @Bean
  public KurentoClient kurentoClient() {
    return KurentoClient.create();
  }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) {
    config.enableSimpleBroker("/sub"); // 메세지 구독 요청
    config.setApplicationDestinationPrefixes("/pub"); // 메세지 발행 요청
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint( "/api/socket/chat", "/api/socket/meeting").setAllowedOriginPatterns("*")
            .setHandshakeHandler(new SessionHandshakeHandler())
            .withSockJS();
  }
}
