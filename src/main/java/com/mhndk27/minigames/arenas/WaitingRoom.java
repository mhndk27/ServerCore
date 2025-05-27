package com.mhndk27.minigames.arenas;

import org.bukkit.Location;

import java.util.UUID;

public class WaitingRoom {

    private final UUID roomId;
    private final Location location;

    public WaitingRoom(UUID roomId, Location location) {
        this.roomId = roomId;
        this.location = location;
    }

    public UUID getRoomId() {
        return roomId;
    }

    public Location getLocation() {
        return location;
    }
}
