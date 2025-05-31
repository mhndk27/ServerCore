package com.mhndk27.servercore;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    private static Main instance;
    private RoomManager roomManager;
    private PartySystemAPI partyAPI; // يجب ربطها لاحقًا

    @Override
    public void onEnable() {
        instance = this;
        roomManager = new RoomManager();
        // partyAPI = ... // اربطها هنا مع بلوقن البارتي الحقيقي
        TptCommand tptCommand = new TptCommand(this);
        getCommand("tpt").setExecutor(tptCommand);
        getCommand("tpt").setTabCompleter(tptCommand);
    }

    public static Main getInstance() {
        return instance;
    }

    public RoomManager getRoomManager() {
        return roomManager;
    }

    public PartySystemAPI getPartyAPI() {
        return partyAPI;
    }
}
