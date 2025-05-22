package com.mhndk27.teamsys;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.entity.Player;

public class PlayerQuitListener implements Listener {

    private final TeamManager teamManager;

    public PlayerQuitListener(TeamManager teamManager) {
        this.teamManager = teamManager;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        teamManager.removePlayerFromTeam(player);
    }
}
