package com.mhndk27.teamsys;

import org.bukkit.plugin.java.JavaPlugin;

public class TeamsSystem extends JavaPlugin {

    private TeamManager teamManager;
    private InviteManager inviteManager;
    private TeamCommand teamCommand;

    @Override
    public void onEnable() {
        inviteManager = new InviteManager();
        teamManager = new TeamManager(this, inviteManager);
        teamCommand = new TeamCommand(teamManager);

        getServer().getPluginManager().registerEvents(new PlayerQuitListener(teamManager), this);
        getCommand("team").setExecutor(teamCommand);
        getLogger().info("TeamsSystem enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("TeamsSystem disabled!");
    }
}
