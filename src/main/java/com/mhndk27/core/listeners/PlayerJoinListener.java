package com.mhndk27.core.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.entity.Player;
import com.mhndk27.core.utils.TeleportUtils;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        TeleportUtils.teleportToLobby(player); // Teleport player to the lobby
        player.sendMessage("Welcome! You have been teleported to the lobby.");
    }
}
