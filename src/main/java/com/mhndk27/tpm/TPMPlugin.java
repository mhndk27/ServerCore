package com.mhndk27.tpm;

import com.mhndk27.tpm.commands.LobbyTPCommand;
import com.mhndk27.tpm.commands.ZSTPCommand;
import com.mhndk27.tpm.managers.NPCFileManager;
import com.mhndk27.tpm.managers.RoomManager;
import org.bukkit.plugin.java.JavaPlugin;

public class TPMPlugin extends JavaPlugin {

    private static TPMPlugin instance;
    private RoomManager roomManager;
    private NPCFileManager npcFileManager;

    @Override
    public void onEnable() {
        instance = this;
        npcFileManager = new NPCFileManager();
        roomManager = new RoomManager(npcFileManager);

        getCommand("zstp").setExecutor(new ZSTPCommand(roomManager));
        getCommand("lobbytp").setExecutor(new LobbyTPCommand(roomManager));
    }

    public static TPMPlugin getInstance() {
        return instance;
    }

    public RoomManager getRoomManager() {
        return roomManager;
    }

    public NPCFileManager getNpcFileManager() {
        return npcFileManager;
    }
}
