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
            rooms.put(i, new Room(118, 85, 480 + (100 * i))); // Raised Y-coordinate by 3 blocks
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

    public boolean reserveRoom(UUID playerId) {
        Room room = getAvailableRoom();
        if (room != null) {
            room.setOccupied(true, playerId); // Allow reservation for individual players
            return true;
        }
        return false;
    }

    public boolean reserveRoomForParty(UUID leaderId, Iterable<UUID> partyMembers) {
        Room room = getAvailableRoom();
        if (room != null) {
            room.setOccupied(true, leaderId); // Reserve room for the leader
            for (UUID memberId : partyMembers) {
                room.addOccupant(memberId); // Add all party members as occupants
            }
            return true;
        }
        return false;
    }

    public void releaseRoom(UUID playerId) {
        for (Room room : rooms.values()) {
            if (room.getPartyId() != null && room.getPartyId().equals(playerId)) {
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

    public HashMap<Integer, Room> getRooms() {
        return rooms; // Expose the rooms map for external access
    }

    public Room getRoomByPlayer(UUID playerId) {
        for (Room room : rooms.values()) {
            if (room.isOccupied() && room.getPartyId().equals(playerId)) {
                return room;
            }
        }
        return null;
    }
}
