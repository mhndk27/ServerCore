package com.mhndk27.partysys.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TeleportUtils {

    private static final Location LOBBY_LOCATION = new Location(
            Bukkit.getWorld("world"), 0, 100, 0
    );

    public static void teleportToLobby(Player player) {
        player.teleport(LOBBY_LOCATION);
    }
}
