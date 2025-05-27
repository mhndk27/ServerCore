package com.mhndk27.minigames;

import com.mhndk27.minigames.arenas.WaitingRoomManager;
import com.mhndk27.minigames.commands.ZombieShooterCommand;
import com.mhndk27.minigames.commands.ReturnToLobbyCommand;
import com.mhndk27.minigames.integration.PartyIntegration;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class MiniGamesPlugin extends JavaPlugin {

    private static MiniGamesPlugin instance;
    private WaitingRoomManager waitingRoomManager;
    private PartyIntegration partyIntegration;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        waitingRoomManager = new WaitingRoomManager(this);

        // ربط بلوقن Party-System
        Plugin partyPlugin = Bukkit.getPluginManager().getPlugin("PartySystem");

        if (partyPlugin == null || !partyPlugin.isEnabled()) {
            getLogger().warning("Party-System plugin not found or disabled! Party features disabled.");
            partyIntegration = new PartyIntegration(this, null);
        } else {
            Object partyManagerInstance = null;
            try {
                partyManagerInstance = partyPlugin.getClass().getMethod("getPartyManager").invoke(partyPlugin);
            } catch (Exception e) {
                getLogger().warning("Failed to get PartyManager from Party-System plugin.");
                e.printStackTrace();
            }
            partyIntegration = new PartyIntegration(this, partyManagerInstance);
        }

        // تسجيل الأوامر اللي تنفذ من الكونسول بواسطة NPC
        getCommand("zombieshooter").setExecutor(new ZombieShooterCommand(partyIntegration));
        getCommand("returntolobby").setExecutor(new ReturnToLobbyCommand(partyIntegration));

        getLogger().info("MiniGamesManager enabled!");
    }

    @Override
    public void onDisable() {
        waitingRoomManager.clearAllRooms();
        getLogger().info("MiniGamesManager disabled!");
    }

    public WaitingRoomManager getWaitingRoomManager() {
        return waitingRoomManager;
    }

    public PartyIntegration getPartyIntegration() {
        return partyIntegration;
    }

    public static MiniGamesPlugin getInstance() {
        return instance;
    }
}
