package com.mhndk27.core;

import org.bukkit.plugin.java.JavaPlugin;
import com.mhndk27.core.listeners.PlayerJoinListener;
import com.mhndk27.core.partysys.PartyManager;
import com.mhndk27.core.partysys.commands.PartyCommand;
import com.mhndk27.core.partysys.commands.PartyTabCompleter;
import com.mhndk27.core.partysys.listeners.PartyChatListener;
import com.mhndk27.core.partysys.listeners.PlayerQuitListener;
import com.mhndk27.core.partysys.managers.PartyChatManager;
import com.mhndk27.core.partysys.managers.PartyInviteManager;
import com.mhndk27.core.rooms.RoomManager;
import com.mhndk27.core.rooms.commands.TeleportCommand;
import com.mhndk27.core.rooms.commands.TeleportTabCompleter;

public class Main extends JavaPlugin {

    private PartyManager partyManager;
    private RoomManager roomManager;

    @Override
    public void onEnable() {
        roomManager = new RoomManager();
        partyManager = new PartyManager(roomManager);

        new PartyInviteManager(partyManager, roomManager);
        new PartyChatManager(partyManager);

        getCommand("party").setExecutor(new PartyCommand(partyManager));
        getCommand("party").setTabCompleter(new PartyTabCompleter(partyManager));
        getCommand("tpt").setExecutor(new TeleportCommand(roomManager, partyManager));
        getCommand("tpt").setTabCompleter(new TeleportTabCompleter());

        getServer().getPluginManager().registerEvents(new PartyChatListener(), this);
        getServer().getPluginManager()
                .registerEvents(new PlayerQuitListener(partyManager, roomManager), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
    }

    @Override
    public void onDisable() {
        // Cleanup إن احتجت
    }

    public PartyManager getPartyManager() {
        return partyManager;
    }

    public RoomManager getRoomManager() {
        return roomManager;
    }
}
