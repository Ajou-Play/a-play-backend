package com.paran.aplay.room;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.kurento.client.KurentoClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class RoomManager {
    private final Logger log = LoggerFactory.getLogger(RoomManager.class);
    @Autowired
    private KurentoClient kurento;

    private final ConcurrentMap<Long, Room> rooms = new ConcurrentHashMap<>();

    /**
     * Looks for a room in the active room list.
     *
     * @param roomId
     *          the name of the room
     * @return the room if it was already created, or a new one if it is the first time this room is
     *         accessed
     */
    public Room getRoom(Long roomId) {
        log.debug("Searching for room {}", roomId);
        Room room = rooms.get(roomId);

        if (room == null) {
            log.debug("Room {} not existent. Will create now!", roomId);
            room = new Room(roomId, kurento.createMediaPipeline());
            rooms.put(roomId, room);
        }
        log.debug("Room {} found!", roomId);
        return room;
    }

    /**
     * Removes a room from the list of available rooms.
     *
     * @param room
     *          the room to be removed
     */
    public void removeRoom(Room room) {
        this.rooms.remove(room.getRoomId());
        room.close();
        log.info("Room {} removed and closed", room.getRoomId());
    }

}
