package com.mhndk27.partysys.listeners;

import com.mhndk27.partysys.PartyManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PartyChatListener implements Listener {

    private final PartyManager partyManager;

    public PartyChatListener(PartyManager partyManager) {
        this.partyManager = partyManager;
    }

    @EventHandler
    @SuppressWarnings("deprecation")
    public void onChat(AsyncPlayerChatEvent event) {
        Player sender = event.getPlayer();

        if (!partyManager.isPartyChatEnabled(sender.getUniqueId())) return;

        event.setCancelled(true); // ✅ نوقف الشات العام

        String message = event.getMessage(); // ✅ الرسالة النصية

        partyManager.sendPartyMessage(sender, message);
    }
}
