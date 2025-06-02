package com.mhndk27.core.partysys.listeners;

import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import com.mhndk27.core.partysys.PartyManager;
import com.mhndk27.core.utils.TeleportUtils;

public class PlayerQuitListener implements Listener {

    private final PartyManager partyManager;

    public PlayerQuitListener(PartyManager partyManager) {
        this.partyManager = partyManager;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        if (partyManager.isInParty(playerUUID)) {
            partyManager.leaveParty(playerUUID);
            TeleportUtils.teleportToLobby(player); // Use general TeleportUtils
        }
    }
}
