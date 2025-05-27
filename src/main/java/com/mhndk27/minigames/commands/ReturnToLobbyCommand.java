package com.mhndk27.minigames.commands;

import com.mhndk27.minigames.integration.PartyIntegration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ReturnToLobbyCommand implements CommandExecutor {

    private final PartyIntegration partyIntegration;
    private final Location lobbyLocation = new Location(Bukkit.getWorld("world"), 0, 7, 0);

    public ReturnToLobbyCommand(PartyIntegration partyIntegration) {
        this.partyIntegration = partyIntegration;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            sender.sendMessage("§cUsage: /returntolobby <player>");
            return false;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null || !target.isOnline()) {
            sender.sendMessage("§cPlayer not found or offline.");
            return false;
        }

        if (partyIntegration.isPartyLeader(target)) {
            List<Player> members = partyIntegration.getPartyMembers(target);
            if (members != null) {
                for (Player member : members) {
                    member.teleport(lobbyLocation);
                }
                sender.sendMessage("§aParty returned to lobby.");
                return true;
            }
        }

        target.teleport(lobbyLocation);
        sender.sendMessage("§ePlayer returned to lobby individually.");
        return true;
    }
}
