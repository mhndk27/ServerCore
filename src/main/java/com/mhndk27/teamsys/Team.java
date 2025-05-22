package com.mhndk27.teamsys;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Team {

    private UUID leader;
    private final Set<UUID> members;

    public Team(UUID leader) {
        this.leader = leader;
        this.members = new HashSet<>();
        this.members.add(leader);
    }

    public UUID getLeader() {
        return leader;
    }

    public void setLeader(UUID leader) {
        this.leader = leader;
    }

    public Set<UUID> getMembers() {
        return members;
    }

    public void addMember(UUID playerUUID) {
        members.add(playerUUID);
    }

    public void removeMember(UUID playerUUID) {
        members.remove(playerUUID);
        if (leader.equals(playerUUID)) {
            if (!members.isEmpty()) {
                // عين قائد جديد عشوائي
                leader = members.iterator().next();
            }
        }
    }

    public boolean isLeader(UUID playerUUID) {
        return leader.equals(playerUUID);
    }
}
