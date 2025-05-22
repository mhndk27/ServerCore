package com.mhndk27.partysys.listeners;

import com.mhndk27.partysys.PartyManager;
import com.mhndk27.partysys.utils.TeleportUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class PlayerQuitListener implements Listener {

    private final PartyManager partyManager;

    public PlayerQuitListener(PartyManager partyManager) {
        this.partyManager = partyManager;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        if (partyManager.isInAnyParty(playerUUID)) {
            partyManager.leaveParty(playerUUID);
            TeleportUtils.teleportToLobby(player);
        }
    }
}
