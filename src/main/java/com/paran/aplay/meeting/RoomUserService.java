package com.paran.aplay.meeting;

import com.paran.aplay.user.domain.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.stereotype.Service;

@Service
public class RoomUserService {

    // roomId, <userId, User>
    private final ConcurrentMap<Long, ConcurrentMap<Long, User>> roomMap = new ConcurrentHashMap<>();

    public void add(Long roomId, User user) {
        if (!roomMap.containsKey(roomId)) {
            roomMap.put(roomId, new ConcurrentHashMap<>());
        }
        roomMap.get(roomId).put(user.getId(), user);
    }

    public Optional<User> findById(Long roomId, Long userId) {
        if (!roomMap.containsKey(roomId)) return Optional.empty();
        final User user = roomMap.get(roomId).get(userId);
        return Objects.isNull(user) ? Optional.empty() : Optional.of(user);
    }

    public void delete(Long roomId, Long userId) {
        if (roomMap.containsKey(roomId)) {
            roomMap.get(roomId).remove(userId);
        }
    }

    public List<User> findAllByRoomId(Long roomId) {
        if(roomMap.containsKey(roomId)) return new ArrayList<>(roomMap.get(roomId).values());
        else return new ArrayList<>();
    }
}
