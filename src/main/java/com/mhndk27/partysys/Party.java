package com.mhndk27.partysys;

import java.util.*;

public class Party {

    private UUID leaderUUID;
    private final Set<UUID> members = new HashSet<>();

    public Party(UUID leaderUUID) {
        this.leaderUUID = leaderUUID;
        members.add(leaderUUID);
    }

    public UUID getLeaderUUID() {
        return leaderUUID;
    }

    public void setLeader(UUID newLeaderUUID) {
        if (members.contains(newLeaderUUID)) {
            this.leaderUUID = newLeaderUUID;
        }
    }

    public boolean isLeader(UUID playerUUID) {
        return leaderUUID.equals(playerUUID);
    }

    public Set<UUID> getMembers() {
        return Collections.unmodifiableSet(members);
    }

    public boolean addMember(UUID playerUUID) {
        if (members.size() >= 4) return false; // حد الأعضاء 4
        return members.add(playerUUID);
    }

    public boolean removeMember(UUID playerUUID) {
        return members.remove(playerUUID);
    }

    public boolean contains(UUID playerUUID) {
        return members.contains(playerUUID);
    }

    public boolean isFull() {
        return members.size() >= 4;
    }

    public void transferLeadership(UUID newLeaderUUID) {
        if (members.contains(newLeaderUUID)) {
            this.leaderUUID = newLeaderUUID;
        }
    }
}
