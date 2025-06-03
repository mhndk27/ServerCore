package com.mhndk27.core.rooms.commands;

import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.mhndk27.core.partysys.Party;
import com.mhndk27.core.partysys.PartyManager;
import com.mhndk27.core.rooms.Room;
import com.mhndk27.core.rooms.RoomManager;
import com.mhndk27.core.utils.MessageUtils; // Import MessageUtils
import com.mhndk27.core.utils.TeleportUtils; // Use general TeleportUtils

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
            if (partyManager.isInParty(playerUUID)) {
                Party party = partyManager.getParty(playerUUID);

                if (party.isLeader(playerUUID)) {
                    // Leader: teleport all party members to the lobby and release their rooms
                    for (UUID memberUUID : party.getMembers()) {
                        Player member = Bukkit.getPlayer(memberUUID);
                        if (member != null) {
                            TeleportUtils.teleportToLobby(member);
                        }
                        roomManager.releaseRoomForMember(memberUUID); // Release room for each member
                    }
                } else {
                    // Member: teleport to the lobby, remove from party, and release their room
                    partyManager.removeMember(party.getLeaderUUID(), playerUUID);
                    TeleportUtils.teleportToLobby(player);
                    player.sendMessage(MessageUtils.error(
                            "You have been removed from the party and teleported to the lobby."));
                    roomManager.releaseRoomForMember(playerUUID); // Release room for the member
                }
            } else {
                // Not in a party: teleport to the lobby and release their room
                TeleportUtils.teleportToLobby(player);
                roomManager.releaseRoomForMember(playerUUID); // Release room for the player
            }
            return true;
        }

        if (destination.equals("zombie_shooter")) {
            Room currentRoom = roomManager.getRoomByPlayer(playerUUID);
            if (currentRoom != null) { // Check if the player is already occupying a room
                player.sendMessage(MessageUtils.error("You are already in a waiting room."));
                return true; // Prevent redundant teleportation
            }

            Room room = null;

            if (partyManager.isInParty(playerUUID)) {
                Party party = partyManager.getParty(playerUUID);
                UUID leaderUUID = party.getLeaderUUID();

                if (party.isLeader(playerUUID)) {
                    boolean reserved =
                            roomManager.reserveRoomForParty(playerUUID, party.getMembers());
                    if (!reserved) {
                        player.sendMessage(MessageUtils.error("No available rooms at the moment."));
                        return true;
                    }
                    room = roomManager.getRoomByPlayer(playerUUID);
                } else {
                    room = roomManager.getRoomByPlayer(leaderUUID);
                    if (room == null || !room.isOccupied()
                            || !room.getOccupants().contains(playerUUID)) {
                        player.sendMessage(
                                MessageUtils.info("Wait for your party leader to teleport."));
                        return true;
                    }
                }
            } else {
                boolean reserved = roomManager.reserveRoom(playerUUID);
                if (!reserved) {
                    player.sendMessage(MessageUtils.error("No available rooms at the moment."));
                    return true;
                }
                room = roomManager.getRoomByPlayer(playerUUID);
            }

            if (room == null) {
                player.sendMessage(MessageUtils.error("No available rooms at the moment."));
                return true;
            }

            int[] coords = room.getCoordinates();
            Location roomLocation =
                    new Location(Bukkit.getWorld("world"), coords[0], coords[1], coords[2]);

            if (partyManager.isInParty(playerUUID)) {
                Party party = partyManager.getParty(playerUUID);
                for (UUID memberUUID : party.getMembers()) {
                    Player member = Bukkit.getPlayer(memberUUID);
                    if (member != null) {
                        member.teleport(roomLocation);
                        member.sendMessage(MessageUtils
                                .success("Teleported to the Zombie Shooter waiting room."));
                    }
                }
            } else {
                player.teleport(roomLocation);
                player.sendMessage(
                        MessageUtils.success("Teleported to the Zombie Shooter waiting room."));
            }
            return true;
        }

        player.sendMessage("Unknown destination.");
        return true;
    }
}
