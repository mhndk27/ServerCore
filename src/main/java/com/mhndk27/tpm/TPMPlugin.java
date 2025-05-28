package com.mhndk27.tpm;

import com.mhndk27.tpm.commands.LobbyTPCommand;
import com.mhndk27.tpm.commands.ZSTPCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class TPMPlugin extends JavaPlugin {

    private static TPMPlugin instance;

    @Override
    public void onEnable() {
        instance = this;

        // تسجيل أوامر الكونسل
        getCommand("zstp").setExecutor(new ZSTPCommand());
        getCommand("lobbytp").setExecutor(new LobbyTPCommand());

        getLogger().info("TPMPlugin enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("TPMPlugin disabled!");
    }

    public static TPMPlugin getInstance() {
        return instance;
    }
}
