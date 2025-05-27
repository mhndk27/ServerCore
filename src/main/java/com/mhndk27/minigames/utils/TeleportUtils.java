package com.mhndk27.minigames.utils;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TeleportUtils {

    /**
     * Teleport player safely to location (adds basic checks)
     * @param player Player to teleport
     * @param location Location to teleport to
     */
    public static void teleportPlayer(Player player, Location location) {
        if (player == null || location == null) return;

        if (!player.isOnline()) return;

        // يمكن إضافة فحص إضافي مثلاً تحقق من العالم أو المنطقة
        player.teleport(location);
    }
}
