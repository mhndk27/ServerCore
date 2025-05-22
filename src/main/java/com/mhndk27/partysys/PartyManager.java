package com.mhndk27.partysys;

import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PartyManager {

    private final Map<UUID, Party> playerPartyMap = new HashMap<>();
    private final Set<Party> parties = new HashSet<>();

    public Party getParty(UUID playerUUID) {
        return playerPartyMap.get(playerUUID);
    }

    public boolean isInParty(UUID playerUUID) {
        return playerPartyMap.containsKey(playerUUID);
    }

    public Party createParty(UUID leaderUUID) {
        if (isInParty(leaderUUID)) return null;
        Party party = new Party(leaderUUID);
        parties.add(party);
        addParty(party);
        return party;
    }

    public void addParty(Party party) {
        for (UUID member : party.getMembers()) {
            playerPartyMap.put(member, party);
        }
    }

    public void removeParty(Party party) {
        for (UUID member : party.getMembers()) {
            playerPartyMap.remove(member);
        }
        parties.remove(party);
    }

    public boolean addMember(UUID leaderUUID, UUID targetUUID) {
        Party party = getParty(leaderUUID);
        if (party == null || !party.isLeader(leaderUUID)) return false;
        if (isInParty(targetUUID)) return false;
        if (party.isFull()) return false;
        boolean added = party.addMember(targetUUID);
        if (added) addParty(party);
        return added;
    }

    public boolean removeMember(UUID leaderUUID, UUID targetUUID) {
        Party party = getParty(leaderUUID);
        if (party == null || !party.isLeader(leaderUUID)) return false;
        if (!party.contains(targetUUID)) return false;
        boolean removed = party.removeMember(targetUUID);
        if (removed) playerPartyMap.remove(targetUUID);
        return removed;
    }

    public boolean leaveParty(UUID playerUUID) {
        Party party = getParty(playerUUID);
        if (party == null) return false;

        if (party.isLeader(playerUUID)) {
            // قائد غادر => حل البارتي
            removeParty(party);
        } else {
            party.removeMember(playerUUID);
            playerPartyMap.remove(playerUUID);
        }
        return true;
    }

    public boolean transferLeadership(UUID currentLeaderUUID, UUID newLeaderUUID) {
        Party party = getParty(currentLeaderUUID);
        if (party == null || !party.isLeader(currentLeaderUUID)) return false;
        if (!party.contains(newLeaderUUID)) return false;
        party.transferLeadership(newLeaderUUID);
        return true;
    }

    public boolean isInAnyParty(UUID playerUUID) {
        return isInParty(playerUUID);
    }

    public String getPlayerName(UUID playerUUID) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player != null) return player.getName();
        return null;
    }
}
