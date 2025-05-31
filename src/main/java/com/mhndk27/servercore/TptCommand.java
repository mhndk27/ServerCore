package com.mhndk27.servercore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class TptCommand implements CommandExecutor, TabCompleter {
    private final Main plugin;

    public TptCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Players only.");
            return true;
        }
        if (args.length != 1) {
            player.sendMessage("§cUsage: /tpt <zombie_shooter|lobby>");
            return true;
        }

        String target = args[0].toLowerCase();
        PartySystemAPI partyAPI = plugin.getPartyAPI();
        RoomManager roomManager = plugin.getRoomManager();
        UUID uuid = player.getUniqueId();

        if (target.equals("zombie_shooter")) {
            if (partyAPI != null) {
                Integer roomId = roomManager.findEmptyRoom();
                if (roomId == null) {
                    player.sendMessage("§cNo available rooms at the moment.");
                    return true;
                }
                boolean result = roomManager.handleRoomJoinRequest(partyAPI, uuid, roomId);
                if (!result) {
                    // الرسائل ترسل تلقائياً من RoomManager
                    return true;
                }
                player.sendMessage("§aYou have been teleported to Zombie Shooter (Room #" + roomId + ").");
            } else {
                Integer currentRoom = roomManager.getPlayerRoom(uuid);
                if (currentRoom != null) {
                    Location center = roomManager.getRoomCenter(currentRoom);
                    player.teleport(center);
                    player.sendMessage("§aYou are already in Zombie Shooter (Room #" + currentRoom + ")");
                    return true;
                }
                Integer roomId = roomManager.findEmptyRoom();
                if (roomId == null) {
                    player.sendMessage("§cNo available rooms at the moment.");
                    return true;
                }
                Location center = roomManager.getRoomCenter(roomId);
                player.teleport(center);
                roomManager.addPlayersToRoom(roomId, Collections.singletonList(uuid));
                player.sendMessage("§aYou have been teleported to Zombie Shooter (Room #" + roomId + ")");
            }
            return true;
        }

        if (target.equals("lobby")) {
            Location lobbyLocation = new Location(player.getWorld(), 0, 16, 0);
            if (partyAPI != null) {
                roomManager.handleLobbyCommand(partyAPI, uuid, lobbyLocation);
            } else {
                player.teleport(lobbyLocation);
                roomManager.removePlayer(uuid);
                player.sendMessage("§aYou have been sent to the lobby.");
            }
            return true;
        }

        player.sendMessage("§cUsage: /tpt <zombie_shooter|lobby>");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> options = Arrays.asList("zombie_shooter", "lobby");
            String current = args[0].toLowerCase();
            List<String> suggestions = new ArrayList<>();
            for (String opt : options) {
                if (opt.startsWith(current))
                    suggestions.add(opt);
            }
            return suggestions;
        }
        return Collections.emptyList();
    }
}
