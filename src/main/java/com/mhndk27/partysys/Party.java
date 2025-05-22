package com.mhndk27.partysys;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

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
        return members;
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

    public void transferLeadership(UUID newLeader) {
        if (members.contains(newLeader)) {
            leader = newLeader;
        }
    }

    public boolean contains(UUID playerUUID) {
        return members.contains(playerUUID);
    }
}
