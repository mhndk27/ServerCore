package com.mhndk27.minigames.listeners;

import com.mhndk27.minigames.integration.PartyIntegration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

public class NPCInteractionListener implements Listener {

    private final PartyIntegration partyIntegration;

    public NPCInteractionListener(PartyIntegration partyIntegration) {
        this.partyIntegration = partyIntegration;
    }

    @EventHandler
    public void onPlayerInteractNPC(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();

        if (!partyIntegration.isPartyLeader(player)) {
            player.sendMessage("§cYou must be a party leader to start the minigame!");
            return;
        }

        boolean started = partyIntegration.startMiniGameForPartyLeader(player);

        if (started) {
            player.sendMessage("§aMini-game started! Teleporting your party...");
        } else {
            player.sendMessage("§cFailed to start mini-game.");
        }
    }
}
