package com.mhndk27.core.partysys.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import com.mhndk27.core.partysys.managers.PartyChatManager;
import io.papermc.paper.event.player.AsyncChatEvent;

public class PartyChatListener implements Listener {

    public PartyChatListener() {
        // Constructor remains empty as no fields are needed
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(AsyncChatEvent event) {
        if (PartyChatManager.getInstance().isPartyChatEnabled(event.getPlayer().getUniqueId())) {
            // If party chat is enabled, send message to party and cancel the original event
            String message = event.message().toString(); // Convert Component to String
            PartyChatManager.getInstance().sendPartyMessage(event.getPlayer(), message);
            event.setCancelled(true);
        } else {
            // For non-party chat players, modify recipients
            event.viewers().removeIf(audience -> audience instanceof org.bukkit.entity.Player player
                    && PartyChatManager.getInstance().isPartyChatEnabled(player.getUniqueId()));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.joinMessage(null);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.quitMessage(null);
    }
}
