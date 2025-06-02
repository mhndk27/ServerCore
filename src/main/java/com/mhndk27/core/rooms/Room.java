package com.mhndk27.core.rooms;

import java.util.UUID;

public class Room {
    private final int x, y, z;
    private boolean occupied;
    private UUID partyId;

    public Room(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.occupied = false;
        this.partyId = null;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public void setOccupied(boolean occupied, UUID partyId) {
        this.occupied = occupied;
        this.partyId = partyId;
    }

    public UUID getPartyId() {
        return partyId;
    }

    public int[] getCoordinates() {
        return new int[]{x, y, z};
    }
}
