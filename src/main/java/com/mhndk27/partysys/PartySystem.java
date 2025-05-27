package com.mhndk27.partysys;

import com.mhndk27.partysys.commands.PartyCommand;
import com.mhndk27.partysys.commands.PartyTabCompleter;
import com.mhndk27.partysys.listeners.PartyChatListener;
import com.mhndk27.partysys.listeners.PlayerJoinListener;
import com.mhndk27.partysys.listeners.PlayerQuitListener;
import org.bukkit.plugin.java.JavaPlugin;

public class PartySystem extends JavaPlugin {

    private PartyManager partyManager;

    @Override
    public void onEnable() {
        partyManager = new PartyManager();

        // تسجيل الأمر مع التنفيذ والتنقيح (TabCompleter)
        getCommand("party").setExecutor(new PartyCommand(partyManager));
        getCommand("party").setTabCompleter(new PartyTabCompleter(partyManager)); // تعديل احترافي

        // تسجيل المستمعين للأحداث
        getServer().getPluginManager().registerEvents(new PartyChatListener(partyManager), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(partyManager), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(partyManager), this);
    }

    @Override
    public void onDisable() {
        // ممكن تضيف تنظيف لو احتاج الأمر هنا
    }
}
