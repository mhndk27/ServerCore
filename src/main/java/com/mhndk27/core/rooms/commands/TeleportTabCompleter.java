package com.mhndk27.core.rooms.commands;

import java.util.Arrays;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class TeleportTabCompleter implements TabCompleter {

    private final List<String> destinations = Arrays.asList("lobby", "zombie_shooter");

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias,
            String[] args) {
        if (args.length == 1) {
            return destinations.stream()
                    .filter(destination -> destination.startsWith(args[0].toLowerCase())).toList();
        }
        return List.of();
    }
}
