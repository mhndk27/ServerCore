package com.mhndk27.partysys.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class TeleportUtils {

    private static final double LOBBY_X = 0.5;
    private static final int LOBBY_Y = 16;
    private static final double LOBBY_Z = 0.5;

    public static void teleportToLobby(Player player) {
        if (player == null)
            return;
        World world = Bukkit.getWorlds().get(0); // الافتراض أن العالم الأول هو العالم الرئيسي
        Location lobbyLocation = new Location(world, LOBBY_X, LOBBY_Y, LOBBY_Z);
        player.teleport(lobbyLocation);
    }

    // دالة تليبورتر عامة لأي مكان
    public static void teleportToLocation(Player player, Location location) {
        if (player == null || location == null)
            return;
        player.teleport(location);
    }
}
