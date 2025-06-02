package com.mhndk27.core.partysys.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import com.mhndk27.core.partysys.managers.PartyChatManager;

public class PartyChatListener implements Listener {

    public PartyChatListener() {
        // Constructor remains empty as no fields are needed
    }

    @EventHandler
    @SuppressWarnings("deprecation")
    public void onChat(AsyncPlayerChatEvent event) {
        Player sender = event.getPlayer();
        boolean isEnabled = PartyChatManager.getInstance().isPartyChatEnabled(sender.getUniqueId());

        if (isEnabled) {
            event.setCancelled(true); // Cancel public chat
            String message = event.getMessage();
            PartyChatManager.getInstance().sendPartyMessage(sender, message);
        }
    }
}
