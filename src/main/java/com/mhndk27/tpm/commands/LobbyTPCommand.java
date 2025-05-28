package com.mhndk27.tpm.commands;

import com.mhndk27.partysys.Party;
import com.mhndk27.partysys.PartyManager;
import com.mhndk27.tpm.managers.RoomManager;
import com.mhndk27.tpm.utils.TeleportUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

public class LobbyTPCommand implements CommandExecutor {

    private final PartyManager partyManager = PartyManager.getInstance();
    private final RoomManager roomManager = RoomManager.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // فقط من الكونسل
        if (!(sender instanceof org.bukkit.command.ConsoleCommandSender)) {
            sender.sendMessage("This command can only be run from the console.");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage("Usage: /lobbytp <player>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null || !target.isOnline()) {
            sender.sendMessage("Player not found or offline.");
            return true;
        }

        UUID uuid = target.getUniqueId();

        Party party = partyManager.getParty(uuid);
        Integer roomId = roomManager.getPlayerRoomId(uuid);

        if (roomId == null) {
            sender.sendMessage("Player is not inside any room.");
            return true;
        }

        Location lobbyLocation = new Location(Bukkit.getWorld("world"), 0, 7, 0);

        if (party == null) {
            // لاعب منفرد: رجوع للوبي وحذف من الغرفة
            TeleportUtils.teleport(target, lobbyLocation);
            roomManager.removePlayer(uuid);
            sender.sendMessage("Player " + target.getName() + " teleported to lobby and removed from room.");
        } else {
            if (party.getLeaderUUID().equals(uuid)) {
                // قائد: ارجع الكل للوبي واحذف الغرفة
                Set<UUID> members = party.getMembers();
                for (UUID memberUuid : members) {
                    Player p = Bukkit.getPlayer(memberUuid);
                    if (p != null && p.isOnline()) {
                        TeleportUtils.teleport(p, lobbyLocation);
                        roomManager.removePlayer(memberUuid);
                    }
                }
                sender.sendMessage("Party leader " + target.getName() + " teleported party to lobby and deleted room.");
            } else {
                // عضو: ارجع هو فقط، طرده من البارتي، واحذفه من الغرفة
                partyManager.removeParty(party);;
                TeleportUtils.teleport(target, lobbyLocation);
                roomManager.removePlayer(uuid);
                sender.sendMessage("Party member " + target.getName() + " teleported to lobby and removed from party and room.");
            }
        }

        return true;
    }
}
