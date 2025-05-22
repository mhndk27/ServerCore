package com.mhndk27.teamsys;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Team {

    private UUID leader;
    private final Set<UUID> members = new HashSet<>();

    public Team(UUID leader) {
        this.leader = leader;
        members.add(leader);
    }

    public UUID getLeader() {
        return leader;
    }

    public void setLeader(UUID newLeader) {
        if (members.contains(newLeader)) {
            this.leader = newLeader;
        }
    }

    public Set<UUID> getMembers() {
        return new HashSet<>(members);
    }

    public void addMember(UUID playerUUID) {
        members.add(playerUUID);
    }

    public void removeMember(UUID playerUUID) {
        members.remove(playerUUID);
    }

    public boolean isLeader(UUID playerUUID) {
        return leader.equals(playerUUID);
    }
}
