package com.mhndk27.partysys;

import java.util.*;

public class PartyManager {

    private final Map<UUID, PlayerPartyData> playerDataMap = new HashMap<>();
    private final Set<Party> parties = new HashSet<>();

    public PlayerPartyData getPlayerData(UUID playerUUID) {
        return playerDataMap.computeIfAbsent(playerUUID, PlayerPartyData::new);
    }

    public Party createParty(UUID leaderUUID) {
        if (isInAnyParty(leaderUUID)) return null;
        Party party = new Party(leaderUUID);
        parties.add(party);
        getPlayerData(leaderUUID).setParty(party);
        return party;
    }

    public void disbandParty(Party party) {
        for (UUID member : party.getMembers()) {
            PlayerPartyData data = getPlayerData(member);
            data.setParty(null);
        }
        parties.remove(party);
    }

    public boolean isInAnyParty(UUID playerUUID) {
        PlayerPartyData data = playerDataMap.get(playerUUID);
        return data != null && data.isInParty();
    }

    public Party getParty(UUID playerUUID) {
        PlayerPartyData data = playerDataMap.get(playerUUID);
        if (data == null) return null;
        return data.getParty();
    }

    public boolean addMemberToParty(UUID leaderUUID, UUID targetUUID) {
        Party party = getParty(leaderUUID);
        if (party == null || !party.getLeader().equals(leaderUUID)) return false;
        if (isInAnyParty(targetUUID)) return false;
        if (party.isFull()) return false;
        boolean added = party.addMember(targetUUID);
        if (added) {
            getPlayerData(targetUUID).setParty(party);
        }
        return added;
    }

    public boolean removeMemberFromParty(UUID leaderUUID, UUID targetUUID) {
        Party party = getParty(leaderUUID);
        if (party == null || !party.getLeader().equals(leaderUUID)) return false;
        if (!party.contains(targetUUID)) return false;
        boolean removed = party.removeMember(targetUUID);
        if (removed) {
            getPlayerData(targetUUID).setParty(null);
        }
        return removed;
    }

    public boolean leaveParty(UUID playerUUID) {
        Party party = getParty(playerUUID);
        if (party == null) return false;

        if (party.getLeader().equals(playerUUID)) {
            // قائد خرج، حل البارتي
            disbandParty(party);
        } else {
            party.removeMember(playerUUID);
            getPlayerData(playerUUID).setParty(null);
        }
        return true;
    }

    public boolean transferLeadership(UUID currentLeaderUUID, UUID newLeaderUUID) {
        Party party = getParty(currentLeaderUUID);
        if (party == null || !party.getLeader().equals(currentLeaderUUID)) return false;
        if (!party.contains(newLeaderUUID)) return false;

        party.transferLeadership(newLeaderUUID);
        return true;
    }
}
