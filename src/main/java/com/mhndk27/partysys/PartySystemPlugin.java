package com.mhndk27.partysys;

import com.mhndk27.partysys.commands.PartyCommand;
import com.mhndk27.partysys.commands.PartyTabCompleter;
import com.mhndk27.partysys.listeners.PartyChatListener;
import com.mhndk27.partysys.listeners.PlayerJoinListener;
import com.mhndk27.partysys.listeners.PlayerQuitListener;
import org.bukkit.plugin.java.JavaPlugin;

public class PartySystemPlugin extends JavaPlugin {

    private PartyManager partyManager;

    @Override
    public void onEnable() {
        partyManager = new PartyManager();

        getCommand("party").setExecutor(new PartyCommand(partyManager));
        getCommand("party").setTabCompleter(new PartyTabCompleter(partyManager));  // هنا التعديل
        getServer().getPluginManager().registerEvents(new PartyChatListener(partyManager), this);
        
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(partyManager), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(partyManager), this);
    }

    @Override
    public void onDisable() {
        // تنظيف إذا احتاج الأمر
    }
}
