package com.mhndk27.tpm.data;

import org.bukkit.Location;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class RoomData {
    private final Location origin;
    private final Set<UUID> members;

    public RoomData(Location origin) {
        this.origin = origin;
        this.members = new HashSet<>();
    }

    public Location getOrigin() {
        return origin;
    }

    public Set<UUID> getMembers() {
        return members;
    }

    public void addMember(UUID uuid) {
        members.add(uuid);
    }

    public void removeMember(UUID uuid) {
        members.remove(uuid);
    }

    public boolean isEmpty() {
        return members.isEmpty();
    }
}
