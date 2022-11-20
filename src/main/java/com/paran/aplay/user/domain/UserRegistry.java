package com.paran.aplay.user.domain;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.web.socket.WebSocketSession;

public class UserRegistry {

    private final ConcurrentHashMap<Long, UserSession> usersById = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, UserSession> usersBySessionId = new ConcurrentHashMap<>();

    public void register(UserSession user) {
        usersById.put(user.getParticipant().getUserId(), user);
        usersBySessionId.put(user.getSession().getId(), user);
    }

    public UserSession getById (Long id) {
        return usersById.get(id);
    }

    public UserSession getBySession(WebSocketSession session) {
        return usersBySessionId.get(session.getId());
    }

    public boolean exists(Long id) {
        return usersById.keySet().contains(id);
    }

    public UserSession removeBySession(WebSocketSession session) {
        final UserSession user = getBySession(session);
        usersById.remove(user.getParticipant().getUserId());
        usersBySessionId.remove(session.getId());
        return user;
    }

}
