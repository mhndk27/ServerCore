package com.mhndk27.partysys;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.mhndk27.partysys.managers.PartyChatManager;
import com.mhndk27.partysys.utils.MessageUtils;
import com.mhndk27.partysys.utils.TeleportUtils;

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

    private final Location lobbyLocation = new Location(Bukkit.getWorld("world"), 0, 7, 0);

    // ===== Getters =====

    public Party getParty(UUID playerUUID) {
        return playerPartyMap.get(playerUUID);
    }

    public boolean isInParty(UUID playerUUID) {
        return playerPartyMap.containsKey(playerUUID);
    }

    // ===== Party Creation and Management =====

    public Party createParty(UUID leaderUUID) {
        if (isInParty(leaderUUID))
            return null;
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
            PartyChatManager.getInstance().disablePartyChat(member); // ⛔ نوقف الشات عن كل الأعضاء

            Player p = Bukkit.getPlayer(member);
            if (p != null) {
                TeleportUtils.teleportToLocation(p, lobbyLocation);
                p.sendMessage(
                        MessageUtils.info("You have been teleported to the lobby because the party was disbanded."));
            }
        }
        parties.remove(party);
    }

    // ===== Members Management =====

    public boolean addMember(UUID leaderUUID, UUID targetUUID) {
        Party party = getParty(leaderUUID);
        if (party == null || !party.isLeader(leaderUUID))
            return false;
        if (isInParty(targetUUID))
            return false;
        if (party.isFull())
            return false;

        boolean added = party.addMember(targetUUID);
        if (added)
            addParty(party);
        return added;
    }

    public boolean removeMember(UUID leaderUUID, UUID targetUUID) {
        Party party = getParty(leaderUUID);
        if (party == null || !party.isLeader(leaderUUID))
            return false;
        if (!party.contains(targetUUID))
            return false;

        boolean removed = party.removeMember(targetUUID);
        if (removed) {
            playerPartyMap.remove(targetUUID);
            PartyChatManager.getInstance().disablePartyChat(targetUUID); // ⛔ نلغي الشات عن المطرود

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
        if (party == null)
            return false;

        boolean isLeader = party.isLeader(playerUUID);
        party.removeMember(playerUUID);
        playerPartyMap.remove(playerUUID);
        PartyChatManager.getInstance().disablePartyChat(playerUUID); // ⛔ نلغي الشات عن اللي طلع

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
                newLeader.sendMessage(MessageUtils
                        .success("You have been promoted to party leader because the previous leader left."));
            }
        }
        return true;
    }

    public boolean transferLeadership(UUID currentLeaderUUID, UUID newLeaderUUID) {
        Party party = getParty(currentLeaderUUID);
        if (party == null || !party.isLeader(currentLeaderUUID))
            return false;
        if (!party.contains(newLeaderUUID))
            return false;

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