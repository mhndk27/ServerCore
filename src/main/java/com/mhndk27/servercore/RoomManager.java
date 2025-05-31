package com.mhndk27.servercore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class RoomManager {
    private final int ROOM_COUNT = 20;
    private final Map<Integer, List<UUID>> rooms = new HashMap<>();
    private final Map<Integer, Location> roomCenters = new HashMap<>();

    public RoomManager() {
        for (int i = 1; i <= ROOM_COUNT; i++) {
            rooms.put(i, new ArrayList<>());
            // Room 1: 118 85 490, Room 2: 118 85 590, ..., Room 20: 118 85 2390
            roomCenters.put(i, new Location(
                    Bukkit.getWorld("world"), // تأكد أن اسم العالم صحيح
                    118, 85, 490 + (i - 1) * 100));
        }
    }

    // Returns the first available (empty) room id, or null if all are occupied
    public Integer findEmptyRoom() {
        for (int i = 1; i <= ROOM_COUNT; i++) {
            if (rooms.get(i).isEmpty())
                return i;
        }
        return null;
    }

    public void addPlayersToRoom(int roomId, List<UUID> players) {
        rooms.get(roomId).addAll(players);
    }

    public void removePlayer(UUID playerUUID) {
        for (List<UUID> members : rooms.values()) {
            members.remove(playerUUID);
        }
    }

    public void clearRoom(int roomId) {
        rooms.get(roomId).clear();
    }

    public Integer getPlayerRoom(UUID playerUUID) {
        for (Map.Entry<Integer, List<UUID>> entry : rooms.entrySet()) {
            if (entry.getValue().contains(playerUUID))
                return entry.getKey();
        }
        return null;
    }

    public Location getRoomCenter(int roomId) {
        return roomCenters.get(roomId);
    }

    // Returns the roomId if any party member is in a room, otherwise null
    public Integer getPartyRoom(List<UUID> partyMembers) {
        for (UUID member : partyMembers) {
            Integer room = getPlayerRoom(member);
            if (room != null)
                return room;
        }
        return null;
    }
}
