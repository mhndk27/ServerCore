package com.mhndk27.core.rooms.commands;

import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.mhndk27.core.partysys.PartyManager;
import com.mhndk27.core.rooms.Room;
import com.mhndk27.core.rooms.RoomManager;

public class TeleportCommand implements CommandExecutor {
    private final RoomManager roomManager;
    private final PartyManager partyManager;

    public TeleportCommand(RoomManager roomManager, PartyManager partyManager) {
        this.roomManager = roomManager;
        this.partyManager = partyManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        UUID playerUUID = player.getUniqueId();

        if (args.length < 1) {
            player.sendMessage("Usage: /tpt <lobby|zombie_shooter>");
            return true;
        }

        String destination = args[0].toLowerCase();

        if (destination.equals("lobby")) {
            Location lobbyLocation = new Location(Bukkit.getWorld("world"), 0.5, 16, 0.5);
            player.teleport(lobbyLocation);
            player.sendMessage("Teleported to the lobby.");
            roomManager.releaseRoomForMember(playerUUID); // تحرير الغرفة إذا عاد اللاعب إلى اللوبي
            return true;
        }

        if (destination.equals("zombie_shooter")) {
            if (partyManager.isLeader(playerUUID)) {
                UUID partyId = partyManager.getParty(playerUUID).getLeaderUUID();
                boolean reserved = roomManager.reserveRoom(partyId);
                if (!reserved) {
                    player.sendMessage("No available rooms at the moment.");
                    return true;
                }

                Room room = roomManager.getAvailableRoom();
                if (room == null) {
                    player.sendMessage("No available rooms at the moment.");
                    return true;
                }

                int[] coords = room.getCoordinates();
                Location roomLocation =
                        new Location(Bukkit.getWorld("world"), coords[0], coords[1], coords[2]);
                for (UUID memberUUID : partyManager.getPartyMembers(playerUUID)) {
                    Player member = Bukkit.getPlayer(memberUUID);
                    if (member != null) {
                        member.teleport(roomLocation);
                        member.sendMessage("Teleported to the Zombie Shooter waiting room.");
                    }
                }
            } else {
                player.sendMessage("Wait for your party leader to teleport.");
            }
            return true;
        }

        player.sendMessage("Unknown destination.");
        return true;
    }
}
