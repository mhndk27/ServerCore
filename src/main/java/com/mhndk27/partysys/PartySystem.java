package com.mhndk27.partysys;

import org.bukkit.plugin.java.JavaPlugin;

import com.mhndk27.partysys.commands.PartyCommand;
import com.mhndk27.partysys.commands.PartyTabCompleter;
import com.mhndk27.partysys.listeners.PartyChatListener;
import com.mhndk27.partysys.listeners.PlayerJoinListener;
import com.mhndk27.partysys.listeners.PlayerQuitListener;
import com.mhndk27.partysys.managers.PartyChatManager;
import com.mhndk27.partysys.managers.PartyInviteManager;

public class PartySystem extends JavaPlugin {

    private PartyManager partyManager;

    @Override
    public void onEnable() {
        partyManager = new PartyManager();

        // تسجيل مدراء جدد
        new PartyInviteManager(partyManager);
        new PartyChatManager(partyManager);

        // تسجيل الأمر مع التنفيذ والتنقيح (TabCompleter)
        getCommand("party").setExecutor(new PartyCommand(partyManager));
        getCommand("party").setTabCompleter(new PartyTabCompleter(partyManager));

        // تسجيل المستمعين للأحداث
        getServer().getPluginManager().registerEvents(new PartyChatListener(partyManager), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(partyManager), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(partyManager), this);
    }

    @Override
    public void onDisable() {
        // Cleanup إن احتجت
    }

    // ✅ Getter عشان نسمح لبلوقن ثاني يوصل للـ PartyManager
    public PartyManager getPartyManager() {
        return partyManager;
    }

    // يمكنك استدعاء PartySystem.getPlugin(PartySystem.class).getPartyManager() من
    // أي بلوقن آخر
}
