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
            if (room.getOccupants().contains(memberId)) {
                room.removeOccupant(memberId);
            }
            // Check if this player is the room owner and release if true
            if (room.getPartyId() != null && room.getPartyId().equals(memberId)) {
                room.setOccupied(false, null);
            }
        }
    }

    public void transferToNewRoom(UUID playerId, Room newRoom) {
        Room currentRoom = getRoomByPlayer(playerId); // Get the player's current room
        if (currentRoom != null) {
            releaseRoomForMember(playerId); // Release the current room
        }
        newRoom.addOccupant(playerId); // Add the player to the new room
    }

    public HashMap<Integer, Room> getRooms() {
        return rooms; // Expose the rooms map for external access
    }

    public Room getRoomByPlayer(UUID playerId) {
        for (Room room : rooms.values()) {
            if (room.getOccupants().contains(playerId)
                    || (room.getPartyId() != null && room.getPartyId().equals(playerId))) {
                return room;
            }
        }
        return null;
    }

    public boolean isPlayerInRoom(UUID playerId) {
        for (Room room : rooms.values()) {
            if (room.isOccupied() && room.getOccupants().contains(playerId)) {
                return true; // Player is already in a room
            }
        }
        return false;
    }
}
