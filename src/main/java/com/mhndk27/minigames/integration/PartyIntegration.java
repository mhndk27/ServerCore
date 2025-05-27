package com.mhndk27.minigames.integration;

import com.mhndk27.minigames.MiniGamesPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PartyIntegration {

    private final MiniGamesPlugin plugin;
    private final Object partyManager; // Object لأننا لا نعرف نوعه الحقيقي

    public PartyIntegration(MiniGamesPlugin plugin, Object partyManager) {
        this.plugin = plugin;
        this.partyManager = partyManager;
    }

    public boolean isPartyLeader(Player player) {
        if (partyManager == null) return false;

        try {
            Method getPartyMethod = partyManager.getClass().getMethod("getParty", UUID.class);
            Object party = getPartyMethod.invoke(partyManager, player.getUniqueId());

            if (party == null) return false;

            Method getLeaderMethod = party.getClass().getMethod("getLeaderUUID");
            UUID leaderUUID = (UUID) getLeaderMethod.invoke(party);

            return leaderUUID.equals(player.getUniqueId());

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Player> getPartyMembers(Player leader) {
        if (partyManager == null) return null;

        try {
            Method getPartyMethod = partyManager.getClass().getMethod("getParty", UUID.class);
            Object party = getPartyMethod.invoke(partyManager, leader.getUniqueId());
            if (party == null) return null;

            Method getMembersMethod = party.getClass().getMethod("getMembers");
            Object membersObj = getMembersMethod.invoke(party);

            if (!(membersObj instanceof List<?>)) return null;
            @SuppressWarnings("unchecked")
            List<UUID> membersUUIDs = (List<UUID>) membersObj;

            List<Player> players = new ArrayList<>();
            for (UUID uuid : membersUUIDs) {
                Player p = Bukkit.getPlayer(uuid);
                if (p != null && p.isOnline()) players.add(p);
            }
            return players;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean startMiniGameForPartyLeader(Player leader) {
        if (!isPartyLeader(leader)) return false;

        List<Player> members = getPartyMembers(leader);
        if (members == null || members.isEmpty()) return false;

        Location waitingRoomLocation = plugin.getWaitingRoomManager().getNextAvailableLocation();

        for (Player p : members) {
            p.teleport(waitingRoomLocation);
        }
        return true;
    }
}
