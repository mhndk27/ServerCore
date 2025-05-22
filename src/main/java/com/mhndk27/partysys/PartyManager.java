package com.mhndk27.partysys;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.mhndk27.partysys.utils.MessageUtils;
import com.mhndk27.partysys.utils.TeleportUtils;

public class PartyManager {

    private final Map<UUID, Party> playerPartyMap = new HashMap<>();
    private final Set<Party> parties = new HashSet<>();

    // نظام الدعوات: UUID اللاعب المدعو => بيانات الدعوة
    private final Map<UUID, InviteData> pendingInvites = new ConcurrentHashMap<>();

    // إحداثيات اللوبي الثابتة (مثلاً 0,7,0)
    private final Location lobbyLocation = new Location(Bukkit.getWorld("world"), 0, 7, 0);

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

    // ======= إدارة البارتيات =======

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
            Player p = Bukkit.getPlayer(member);
            if (p != null) {
                TeleportUtils.teleportToLocation(p, lobbyLocation); // نقل اللاعب للوبّي تلقائي
                p.sendMessage(MessageUtils.info("You have been teleported to the lobby because the party was disbanded."));
            }
        }
        parties.remove(party);
    }

    private final Set<UUID> partyChatEnabled = new HashSet<>();

    public boolean togglePartyChat(UUID playerUUID) {
        if (partyChatEnabled.contains(playerUUID)) {
            partyChatEnabled.remove(playerUUID);
            return false;
        } else {
            partyChatEnabled.add(playerUUID);
            return true;
        }
    }

    public boolean isPartyChatEnabled(UUID playerUUID) {
        return partyChatEnabled.contains(playerUUID);
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
        if (removed) {
            playerPartyMap.remove(targetUUID);
            Player p = Bukkit.getPlayer(targetUUID);
            if (p != null) {
                TeleportUtils.teleportToLocation(p, lobbyLocation);
                p.sendMessage(MessageUtils.info("You have been removed from the party and teleported to the lobby."));
            }
        }
        return removed;
    }

    // مغادرة اللاعب للبارتي (مع تحديث القائد)
    public boolean leaveParty(UUID playerUUID) {
        Party party = getParty(playerUUID);
        if (party == null) return false;

        boolean isLeader = party.isLeader(playerUUID);
        party.removeMember(playerUUID);
        playerPartyMap.remove(playerUUID);

        Player player = Bukkit.getPlayer(playerUUID);
        if (player != null) {
            TeleportUtils.teleportToLocation(player, lobbyLocation);
            player.sendMessage(MessageUtils.info("You left the party and teleported to the lobby."));
        }

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

            Player player = Bukkit.getPlayer(targetUUID);
            if (player != null) {
                player.sendMessage(MessageUtils.success("You joined the party!"));
            }
            Player leader = Bukkit.getPlayer(invite.getLeaderUUID());
            if (leader != null) {
                leader.sendMessage(MessageUtils.info(getPlayerName(targetUUID) + " joined your party."));
            }

            return true;
        }

        return false;
    }

    public void denyInvite(UUID targetUUID) {
        removeInvite(targetUUID);
        Player player = Bukkit.getPlayer(targetUUID);
        if (player != null) {
            player.sendMessage(MessageUtils.error("You denied the party invite."));
        }
    }
}
