package com.mhndk27.partysys;

import java.util.*;

public class Party {

    private UUID leader;
    private final Set<UUID> members = new HashSet<>();
    private static final int MAX_MEMBERS = 4;

    public Party(UUID leader) {
        this.leader = leader;
        members.add(leader);
    }

    public UUID getLeader() {
        return leader;
    }

    public Set<UUID> getMembers() {
        return Collections.unmodifiableSet(members);
    }

    public boolean isLeader(UUID playerUUID) {
        return leader.equals(playerUUID);
    }

    public boolean isFull() {
        return members.size() >= MAX_MEMBERS;
    }

    public boolean addMember(UUID playerUUID) {
        if (isFull()) return false;
        return members.add(playerUUID);
    }

    public boolean removeMember(UUID playerUUID) {
        if (leader.equals(playerUUID)) return false;
        return members.remove(playerUUID);
    }

    public boolean contains(UUID playerUUID) {
        return members.contains(playerUUID);
    }

    public void transferLeadership(UUID newLeaderUUID) {
        if (members.contains(newLeaderUUID)) {
            this.leader = newLeaderUUID;
        }
    }
}
