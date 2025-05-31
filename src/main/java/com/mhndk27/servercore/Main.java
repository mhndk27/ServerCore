package com.mhndk27.servercore;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.mhndk27.partysys.Party;
import com.mhndk27.partysys.PartySystem;

public class Main extends JavaPlugin {
    private static Main instance;
    private RoomManager roomManager;
    private PartySystemAPI partyAPI;

    @Override
    public void onEnable() {
        instance = this;
        roomManager = new RoomManager();

        // استخدم PartyManager من PartySystem عبر Bukkit
        PartySystem partySystem = (PartySystem) Bukkit.getPluginManager().getPlugin("PartySystem");
        if (partySystem != null && partySystem.isEnabled()) {
            partyAPI = new PartySystemPartyManagerAPI(partySystem.getPartyManager());
            getLogger().info("PartyManager initialized and PartySystemAPI linked.");
        } else {
            partyAPI = null;
            getLogger().warning("PartySystem not found or disabled! Party features will be unavailable.");
        }

        TptCommand tptCommand = new TptCommand(this);
        getCommand("tpt").setExecutor(tptCommand);
        getCommand("tpt").setTabCompleter(tptCommand);

        // Teleport all players to lobby on join
        getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onPlayerJoin(PlayerJoinEvent event) {
                event.getPlayer().teleport(new Location(event.getPlayer().getWorld(), 0, 16, 0));
            }
        }, this);
    }

    public static Main getInstance() {
        return instance;
    }

    public RoomManager getRoomManager() {
        return roomManager;
    }

    public PartySystemAPI getPartyAPI() {
        return partyAPI;
    }

    /**
     * PartySystemAPI implementation for your PartyManager.
     */
    public static class PartySystemPartyManagerAPI implements PartySystemAPI {
        private final com.mhndk27.partysys.PartyManager partyManager;

        public PartySystemPartyManagerAPI(com.mhndk27.partysys.PartyManager partyManager) {
            this.partyManager = partyManager;
        }

        @Override
        public boolean isPlayerInParty(UUID playerUUID) {
            return partyManager.isInParty(playerUUID);
        }

        @Override
        public UUID getPartyLeader(UUID playerUUID) {
            Party party = partyManager.getParty(playerUUID);
            return (party != null) ? party.getLeaderUUID() : null;
        }

        @Override
        public List<UUID> getPartyMembersOfPlayer(UUID playerUUID) {
            return partyManager.getPartyMembers(playerUUID);
        }

        @Override
        public Object getPlayerParty(UUID playerUUID) {
            return partyManager.getParty(playerUUID);
        }

        @Override
        public int getPartySize(UUID playerUUID) {
            Party party = partyManager.getParty(playerUUID);
            return (party != null) ? party.getMembers().size() : 0;
        }

        @Override
        public void kickPlayerFromParty(UUID targetUUID) {
            Party party = partyManager.getParty(targetUUID);
            if (party != null) {
                UUID leaderUUID = party.getLeaderUUID();
                if (!leaderUUID.equals(targetUUID)) {
                    partyManager.removeMember(leaderUUID, targetUUID);
                }
            }
        }
    }
}
