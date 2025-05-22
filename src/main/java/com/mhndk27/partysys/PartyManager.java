package com.mhndk27.partysys;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.mhndk27.partysys.utils.MessageUtils;

public class PartyManager {

    private final Map<UUID, Party> playerPartyMap = new HashMap<>();
    private final Set<Party> parties = new HashSet<>();

    // نظام الدعوات: UUID اللاعب المدعو => بيانات الدعوة
    private final Map<UUID, InviteData> pendingInvites = new ConcurrentHashMap<>();

    public static class InviteData {
        private final UUID leaderUUID;
        private final long expireTime;

        public InviteData(UUID leaderUUID, long expireTime) {
            this.leaderUUID = leaderUUID;
            this.expireTime = expireTime;
        }

        public UUID getLeaderUUID() {
            return leaderUUID;
        }

        public long getExpireTime() {
            return expireTime;
        }
    }

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
        return (player != null) ? player.getName() : null;
    }

    public UUID getUUIDFromName(String name) {
        Player player = Bukkit.getPlayerExact(name);
        return (player != null) ? player.getUniqueId() : null;
    }

    // ======== نظام الدعوات ========

    public void addInvite(UUID targetUUID, UUID leaderUUID) {
        long expireTime = System.currentTimeMillis() + 60_000; // 60 ثانية
        pendingInvites.put(targetUUID, new InviteData(leaderUUID, expireTime));
    }

    public boolean hasInvite(UUID targetUUID) {
        if (!pendingInvites.containsKey(targetUUID)) return false;
        InviteData invite = pendingInvites.get(targetUUID);
        if (System.currentTimeMillis() > invite.getExpireTime()) {
            pendingInvites.remove(targetUUID);
            return false;
        }
        return true;
    }

    public InviteData getInviteData(UUID targetUUID) {
        return pendingInvites.get(targetUUID);
    }

    public void removeInvite(UUID targetUUID) {
        pendingInvites.remove(targetUUID);
    }

    // ======== القبول والرفض ========

    public boolean acceptInvite(UUID targetUUID) {
        if (!hasInvite(targetUUID)) return false;

        InviteData invite = getInviteData(targetUUID);
        Party leaderParty = getParty(invite.getLeaderUUID());

        if (leaderParty == null || leaderParty.isFull()) {
            removeInvite(targetUUID);
            return false;
        }

        boolean added = leaderParty.addMember(targetUUID);
        if (added) {
            addParty(leaderParty);
            removeInvite(targetUUID);
            return true;
        }

        return false;
    }

    public void denyInvite(UUID targetUUID) {
        removeInvite(targetUUID);
    }

    public void leaveParty(UUID playerUUID) {
    Party party = getParty(playerUUID);
    if (party == null) return;

    boolean isLeader = party.isLeader(playerUUID);
    party.removeMember(playerUUID);
    playerPartyMap.remove(playerUUID);

    if (party.getMembers().isEmpty()) {
        removeParty(party);
    } else if (isLeader) {
        // تعيين أول عضو كقائد جديد
        UUID newLeaderUUID = party.getMembers().iterator().next();
        party.setLeader(newLeaderUUID);
        Player newLeader = Bukkit.getPlayer(newLeaderUUID);
        if (newLeader != null) {
            newLeader.sendMessage(MessageUtils.success("You have been promoted to party leader because the previous leader left."));
        }
    }
}

}
