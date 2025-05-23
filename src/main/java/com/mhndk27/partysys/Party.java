package com.mhndk27.partysys;

import org.bukkit.Location;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Party {

    private UUID leaderUUID;
    private final Set<UUID> members = new HashSet<>();
    private static final int MAX_MEMBERS = 4;

    // ✅ إضافة: موقع الميني قيم إذا البارتي داخلها
    private Location activeMiniGameLocation;

    public Party(UUID leaderUUID) {
        this.leaderUUID = leaderUUID;
        members.add(leaderUUID);
    }

    public UUID getLeaderUUID() {
        return leaderUUID;
    }

    public void setLeader(UUID leaderUUID) {
        this.leaderUUID = leaderUUID;
    }

    public Set<UUID> getMembers() {
        return Collections.unmodifiableSet(members);
    }

    public boolean isLeader(UUID playerUUID) {
        return leaderUUID.equals(playerUUID);
    }

    public boolean isFull() {
        return members.size() >= MAX_MEMBERS;
    }

    public boolean addMember(UUID playerUUID) {
        if (isFull()) return false;
        return members.add(playerUUID);
    }

    public boolean removeMember(UUID playerUUID) {
        return members.remove(playerUUID);
    }

    public boolean contains(UUID playerUUID) {
        return members.contains(playerUUID);
    }

    // ✅ Getter & Setter لمكان الميني قيم
    public Location getMiniGameLocation() {
        return activeMiniGameLocation;
    }

    public void setMiniGameLocation(Location location) {
        this.activeMiniGameLocation = location;
    }

    public boolean isInMiniGame() {
        return activeMiniGameLocation != null;
    }

    // ✅ مفيد عند نهاية الميني قيم
    public void clearMiniGameLocation() {
        this.activeMiniGameLocation = null;
    }
}
