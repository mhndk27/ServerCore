package com.mhndk27.partysys;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.mhndk27.partysys.utils.MessageUtils;
import com.mhndk27.partysys.utils.TeleportUtils;

import net.kyori.adventure.text.Component;

public class PartyManager {

    private static PartyManager instance;

    public PartyManager() {
        instance = this;
    }

    public static PartyManager getInstance() {
        return instance;
    }

    private final Map<UUID, Party> playerPartyMap = new HashMap<>();
    private final Set<Party> parties = new HashSet<>();

    private final Map<UUID, InviteData> pendingInvites = new ConcurrentHashMap<>();
    private final Set<UUID> partyChatEnabled = new HashSet<>();

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

    // ===== Getters =====

    public Party getParty(UUID playerUUID) {
        return playerPartyMap.get(playerUUID);
    }

    public boolean isInParty(UUID playerUUID) {
        return playerPartyMap.containsKey(playerUUID);
    }

    // ===== Party Creation and Management =====

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
            partyChatEnabled.remove(member); // ⛔ نوقف الشات عن كل الأعضاء

            Player p = Bukkit.getPlayer(member);
            if (p != null) {
                TeleportUtils.teleportToLocation(p, lobbyLocation);
                p.sendMessage(MessageUtils.info("You have been teleported to the lobby because the party was disbanded."));
            }
        }
        parties.remove(party);
    }

    // ===== Party Chat Toggle =====

    public boolean togglePartyChat(UUID playerUUID) {
        if (!isInParty(playerUUID)) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player != null) {
                player.sendMessage(MessageUtils.error("You must be in a party to use party chat."));
            }
            return false;
        }

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

    // ===== Party Chat Messaging =====

    public void sendPartyMessage(Player sender, String message) {
        UUID senderUUID = sender.getUniqueId();
        Party party = getParty(senderUUID);

        if (party == null) {
            sender.sendMessage(MessageUtils.error("You are not in a party."));
            return;
        }

        Component formattedMessage = MessageUtils.partyChat(sender.getName(), message);

        for (UUID memberUUID : party.getMembers()) {
            Player member = Bukkit.getPlayer(memberUUID);
            if (member != null) {
                member.sendMessage(formattedMessage);
            }
        }
    }

    // ===== Members Management =====

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
            partyChatEnabled.remove(targetUUID); // ⛔ نلغي الشات عن المطرود

            Player p = Bukkit.getPlayer(targetUUID);
            if (p != null) {
                TeleportUtils.teleportToLocation(p, lobbyLocation);
                p.sendMessage(MessageUtils.info("You have been removed from the party and teleported to the lobby."));
            }
        }
        return removed;
    }

    public boolean leaveParty(UUID playerUUID) {
        Party party = getParty(playerUUID);
        if (party == null) return false;

        boolean isLeader = party.isLeader(playerUUID);
        party.removeMember(playerUUID);
        playerPartyMap.remove(playerUUID);
        partyChatEnabled.remove(playerUUID); // ⛔ نلغي الشات عن اللي طلع

        Player player = Bukkit.getPlayer(playerUUID);
        if (player != null) {
            TeleportUtils.teleportToLocation(player, lobbyLocation);
            player.sendMessage(MessageUtils.info("You left the party and teleported to the lobby."));
        }

        if (party.getMembers().isEmpty()) {
            removeParty(party);
        } else if (isLeader) {
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

        party.setLeader(newLeaderUUID);

        Player newLeader = Bukkit.getPlayer(newLeaderUUID);
        if (newLeader != null) {
            newLeader.sendMessage(MessageUtils.success("You have been promoted to party leader."));
        }
        return true;
    }

    public boolean isInAnyParty(UUID playerUUID) {
        return isInParty(playerUUID);
    }

    // ===== Player Info Helpers =====

    public String getPlayerName(UUID playerUUID) {
        Player player = Bukkit.getPlayer(playerUUID);
        return (player != null) ? player.getName() : null;
    }

    public UUID getUUIDFromName(String name) {
        Player player = Bukkit.getPlayerExact(name);
        return (player != null) ? player.getUniqueId() : null;
    }

    // ===== Invitations =====

    public void addInvite(UUID targetUUID, UUID leaderUUID) {
        long expireTime = System.currentTimeMillis() + 60_000;
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

    // ===== Accept & Deny Invites =====

    public boolean acceptInvite(UUID targetUUID) {
        if (!hasInvite(targetUUID)) return false;

        InviteData invite = getInviteData(targetUUID);
        Party leaderParty = getParty(invite.getLeaderUUID());

        if (leaderParty == null || leaderParty.isFull()) {
            removeInvite(targetUUID);
            Player player = Bukkit.getPlayer(targetUUID);
            if (player != null) {
                player.sendMessage(MessageUtils.error("The party is no longer available or full."));
            }
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
                leader.sendMessage(MessageUtils.info(getPlayerName(targetUUID) + " joined the party."));
            }

            return true;
        }
        return false;
    }

    public boolean denyInvite(UUID targetUUID) {
        if (!hasInvite(targetUUID)) return false;

        InviteData invite = getInviteData(targetUUID);
        removeInvite(targetUUID);

        Player player = Bukkit.getPlayer(targetUUID);
        if (player != null) {
            player.sendMessage(MessageUtils.info("You denied the party invite."));
        }

        Player leader = Bukkit.getPlayer(invite.getLeaderUUID());
        if (leader != null) {
            leader.sendMessage(MessageUtils.info(getPlayerName(targetUUID) + " denied the party invite."));
        }
        return true;
    }

        // هل اللاعب قائد بارتي؟
    public boolean isLeader(UUID uuid) {
        Party party = getParty(uuid);
        return party != null && party.getLeaderUUID().equals(uuid);
    }

    // جلب كل أعضاء البارتي
    public List<UUID> getPartyMembers(UUID uuid) {
        Party party = getParty(uuid);
        return party != null ? new ArrayList<>(party.getMembers()) : Collections.emptyList();
    }

    // جلب كائن البارتي
    public Party getPartyByLeader(UUID leader) {
        Party party = getParty(leader);
        return party != null && party.getLeaderUUID().equals(leader) ? party : null;
    }
}