package com.mhndk27.core.rooms;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Room {
    private final int x, y, z;
    private boolean occupied;
    private UUID partyId;
    private final Set<UUID> occupants = new HashSet<>();

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
        return new int[] {x, y, z};
    }

    public void addOccupant(UUID playerId) {
        occupants.add(playerId);
    }

    public void removeOccupant(UUID playerId) {
        occupants.remove(playerId);
    }

    public Set<UUID> getOccupants() {
        return Collections.unmodifiableSet(occupants);
    }
}
