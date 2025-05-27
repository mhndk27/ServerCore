package com.mhndk27.tpm;

import com.mhndk27.tpm.commands.ReturnCommand;
import com.mhndk27.tpm.commands.TPCommand;
import com.mhndk27.tpm.core.NPCManager;
import com.mhndk27.tpm.core.PartyChecker;
import com.mhndk27.tpm.core.RoomManager;

import org.bukkit.plugin.java.JavaPlugin;

public class TPMPlugin extends JavaPlugin {

    private static TPMPlugin instance;

    private RoomManager roomManager;
    private NPCManager npcManager;
    private PartyChecker partyChecker;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        roomManager = new RoomManager(this);
        npcManager = new NPCManager(this);
        partyChecker = new PartyChecker(this);

        getCommand("zstp").setExecutor(new TPCommand(this));
        getCommand("lobbytp").setExecutor(new ReturnCommand(this));
    }

    public static TPMPlugin getInstance() {
        return instance;
    }

    public RoomManager getRoomManager() {
        return roomManager;
    }

    public NPCManager getNpcManager() {
        return npcManager;
    }

    public PartyChecker getPartyChecker() {
        return partyChecker;
    }
}
