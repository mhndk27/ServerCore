package com.mhndk27.servercore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
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
            if (partyAPI != null && partyAPI.isPlayerInParty(uuid)) {
                UUID leader = partyAPI.getPartyLeader(uuid);
                List<UUID> members = partyAPI.getPartyMembersOfPlayer(uuid);
                Integer partyRoom = roomManager.getPartyRoom(members);

                if (!leader.equals(uuid)) {
                    // إذا البارتي أصلاً في غرفة، انقل اللاعب لنفس الغرفة
                    if (partyRoom != null) {
                        Location center = roomManager.getRoomCenter(partyRoom);
                        player.teleport(center);
                        roomManager.addPlayersToRoom(partyRoom, Collections.singletonList(uuid));
                        player.sendMessage("§aYou have joined your party in Zombie Shooter (Room #" + partyRoom + ")");
                    } else {
                        player.sendMessage("§cWait for your party leader to teleport.");
                    }
                    return true;
                }

                // القائد: إذا البارتي أصلاً في غرفة، انقل الجميع لنفس الغرفة
                if (partyRoom != null) {
                    Location center = roomManager.getRoomCenter(partyRoom);
                    for (UUID member : members) {
                        Player p = Bukkit.getPlayer(member);
                        if (p != null && p.isOnline()) {
                            p.teleport(center);
                            roomManager.addPlayersToRoom(partyRoom, Collections.singletonList(member));
                            p.sendMessage(
                                    "§aYour party has been teleported to Zombie Shooter (Room #" + partyRoom + ")");
                        }
                    }
                    return true;
                }

                // إذا البارتي مو في غرفة، دور غرفة جديدة
                Integer roomId = roomManager.findEmptyRoom();
                if (roomId == null) {
                    player.sendMessage("§cNo available rooms at the moment.");
                    return true;
                }
                Location center = roomManager.getRoomCenter(roomId);
                for (UUID member : members) {
                    Player p = Bukkit.getPlayer(member);
                    if (p != null && p.isOnline()) {
                        p.teleport(center);
                        roomManager.addPlayersToRoom(roomId, Collections.singletonList(member));
                        p.sendMessage("§aYour party has been teleported to Zombie Shooter (Room #" + roomId + ")");
                    }
                }
            } else {
                // لاعب بدون بارتي: إذا هو أصلاً في غرفة لا تنقله لغرفة جديدة
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
            if (partyAPI != null && partyAPI.isPlayerInParty(uuid)) {
                UUID leader = partyAPI.getPartyLeader(uuid);
                if (!leader.equals(uuid)) {
                    partyAPI.kickPlayerFromParty(uuid);
                    player.sendMessage("§aYou have been kicked from the party and sent to the lobby.");
                    // PartySystem handles teleporting to lobby
                    return true;
                }
                List<UUID> members = partyAPI.getPartyMembersOfPlayer(uuid);
                for (UUID member : members) {
                    Player p = Bukkit.getPlayer(member);
                    if (p != null && p.isOnline()) {
                        p.teleport(new Location(p.getWorld(), 0, 16, 0));
                        roomManager.removePlayer(member);
                        p.sendMessage("§aYour party has been sent to the lobby.");
                    }
                }
            } else {
                player.teleport(new Location(player.getWorld(), 0, 16, 0));
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
