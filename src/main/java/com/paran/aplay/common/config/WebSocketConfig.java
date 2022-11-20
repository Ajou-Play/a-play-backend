package com.paran.aplay.common.config;

import com.paran.aplay.room.MeetingHandler;
import com.paran.aplay.room.RoomManager;
import com.paran.aplay.user.domain.UserRegistry;
import com.paran.aplay.user.service.UserUtilService;
import lombok.RequiredArgsConstructor;
import org.kurento.client.KurentoClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

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
    public MeetingHandler meetingHandler(UserUtilService utilService) {
        return new MeetingHandler(utilService);
    }
    @Bean
    public KurentoClient kurentoClient() {
        return KurentoClient.create();
    }
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(meetingHandler(utilService), "/api/socket/meeting");
    }
}
