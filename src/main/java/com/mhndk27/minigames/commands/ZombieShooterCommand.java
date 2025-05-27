package com.mhndk27.minigames.commands;

import com.mhndk27.minigames.integration.PartyIntegration;
import com.mhndk27.minigames.MiniGamesPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ZombieShooterCommand implements CommandExecutor {

    private final PartyIntegration partyIntegration;

    public ZombieShooterCommand(PartyIntegration partyIntegration) {
        this.partyIntegration = partyIntegration;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length != 1) {
            sender.sendMessage("§cUsage: /startzombieshooter <player>");
            return false;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null || !target.isOnline()) {
            sender.sendMessage("§cPlayer not found or offline.");
            return false;
        }

        boolean success = partyIntegration.startMiniGameForPartyLeader(target);
        if (success) {
            sender.sendMessage("§aZombie Shooter started for player or party.");
        } else {
            MiniGamesPlugin.getInstance()
                    .getWaitingRoomManager()
                    .assignRoom(target);

            sender.sendMessage("§eNo party detected. Teleported player individually.");
        }

        return true;
    }
}
