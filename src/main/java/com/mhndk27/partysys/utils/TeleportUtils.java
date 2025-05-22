package com.mhndk27.partysys.utils;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import org.bukkit.World;

public class TeleportUtils {

    private static final int LOBBY_X = 0;
    private static final int LOBBY_Y = 7;
    private static final int LOBBY_Z = 0;

    public static void teleportToLobby(Player player) {
        if (player == null) return;
        World world = Bukkit.getWorlds().get(0); // Assuming first world is main world
        Location lobbyLocation = new Location(world, LOBBY_X, LOBBY_Y, LOBBY_Z);
        player.teleport(lobbyLocation);
    }
}
