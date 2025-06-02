package com.mhndk27.core.partysys.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import com.mhndk27.core.partysys.PartyManager;
import com.mhndk27.core.partysys.utils.TeleportUtils;
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

        // غيرت isInAnyParty إلى isInParty
        if (partyManager.isInParty(playerUUID)) {
            partyManager.leaveParty(playerUUID);
            TeleportUtils.teleportToLobby(player);
        }
    }
}
