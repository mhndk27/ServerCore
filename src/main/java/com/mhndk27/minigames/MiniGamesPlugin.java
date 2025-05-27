package com.mhndk27.minigames;

import com.mhndk27.minigames.arenas.WaitingRoomManager;
import com.mhndk27.minigames.integration.PartyIntegration;
import com.mhndk27.minigames.listeners.NPCInteractionListener;
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

        // جلب البلوقن الخاص بالبارتي من السيرفر
        Plugin partyPlugin = getServer().getPluginManager().getPlugin("PartySystem");

        if (partyPlugin == null) {
            getLogger().warning("PartySystem plugin not found! Party features disabled.");
            partyIntegration = new PartyIntegration(this, null);
        } else {
            // تأكد أن PartyManager يمكن الوصول إليه من البلوقن
            // فرضًا PartyManager هنا كائن يتم جلبه من البلوقن
            Object partyManager = null;
            try {
                partyManager = partyPlugin.getClass().getMethod("getPartyManager").invoke(partyPlugin);
            } catch (Exception e) {
                getLogger().warning("Failed to get PartyManager from PartySystem plugin.");
            }
            partyIntegration = new PartyIntegration(this, partyManager);
        }

        getServer().getPluginManager().registerEvents(new NPCInteractionListener(partyIntegration), this);

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
