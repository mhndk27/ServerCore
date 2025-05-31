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
            sender.sendMessage("§cPlayers only.");
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

        switch (target) {
            case "zombie_shooter" -> {
                boolean result = roomManager.handleRoomJoinRequest(partyAPI, uuid);
                if (!result)
                    return true; // الرسائل ترسل تلقائياً من RoomManager
            }
            case "lobby" -> {
                Location lobbyLocation = new Location(player.getWorld(), 0.5, 16, 0.5);
                if (partyAPI != null) {
                    roomManager.handleLobbyCommand(partyAPI, uuid, lobbyLocation);
                } else {
                    player.teleport(lobbyLocation);
                    roomManager.removePlayer(uuid);
                }
            }
            default -> player.sendMessage("§cUsage: /tpt <zombie_shooter|lobby>");
        }
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
