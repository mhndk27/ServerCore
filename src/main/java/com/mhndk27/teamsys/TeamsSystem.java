package com.mhndk27.teamsys;

import org.bukkit.plugin.java.JavaPlugin;

public class TeamsSystem extends JavaPlugin {

    private TeamManager teamManager;

    @Override
    public void onEnable() {
        this.teamManager = new TeamManager(this);
        this.getCommand("team").setExecutor(new TeamCommand(teamManager));
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(teamManager), this);
        getLogger().info("TeamsSystem enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("TeamsSystem disabled!");
    }

    public TeamManager getTeamManager() {
        return teamManager;
    }
}
