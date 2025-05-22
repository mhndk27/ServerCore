package com.mhndk27.partysys.listeners;

import com.mhndk27.partysys.PartyManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @SuppressWarnings("unused")
    private final PartyManager partyManager;

    public PlayerJoinListener(PartyManager partyManager) {
        this.partyManager = partyManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // تعليق لتجنب تحذير المتغير الغير مستخدم
        @SuppressWarnings("unused")
        var playerUUID = event.getPlayer().getUniqueId();

        // تعليق: حالياً لا يتم استخدام partyManager ولا playerUUID
    }
}
