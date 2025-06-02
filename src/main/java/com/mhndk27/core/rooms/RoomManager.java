package com.mhndk27.core.rooms;

import java.util.HashMap;
import java.util.UUID;

public class RoomManager {
    private final HashMap<Integer, Room> rooms = new HashMap<>();

    public RoomManager() {
        initializeRooms();
    }

    private void initializeRooms() {
        for (int i = 0; i < 10; i++) {
            rooms.put(i, new Room(137, 82, 490 + (100 * i)));
        }
    }

    public Room getAvailableRoom() {
        for (Room room : rooms.values()) {
            if (!room.isOccupied()) {
                return room;
            }
        }
        return null;
    }

    public boolean reserveRoom(UUID partyId) {
        Room room = getAvailableRoom();
        if (room != null) {
            room.setOccupied(true, partyId);
            return true;
        }
        return false;
    }

    public void releaseRoom(UUID partyId) {
        for (Room room : rooms.values()) {
            if (room.getPartyId() != null && room.getPartyId().equals(partyId)) {
                room.setOccupied(false, null);
                break;
            }
        }
    }

    public void releaseRoomForMember(UUID memberId) {
        for (Room room : rooms.values()) {
            if (room.getPartyId() != null && room.getPartyId().equals(memberId)) {
                room.setOccupied(false, null);
                break;
            }
        }
    }
}
