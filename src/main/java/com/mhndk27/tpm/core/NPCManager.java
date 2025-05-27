package com.mhndk27.tpm.core;

import com.mhndk27.tpm.TPMPlugin;
import org.bukkit.entity.Player;

public class NPCManager {

    private final TPMPlugin plugin;

    public NPCManager(TPMPlugin plugin) {
        this.plugin = plugin;
    }

    public void createNPCForRoom(Player player) {
        // هنا تضيف الكود لإنشاء NPC لكل غرفة أو حسب احتياجك
        player.sendMessage("§aNPC created for your room (placeholder).");
    }
}
