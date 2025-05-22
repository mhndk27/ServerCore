package com.mhndk27.teamsys;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeamCommand implements CommandExecutor {

    private final TeamManager teamManager;

    public TeamCommand(TeamManager teamManager) {
        this.teamManager = teamManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cالأوامر مخصصة للاعبين فقط.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("§eأوامر التيم: /team invite <player>, /team leave, /team info");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "invite" -> {
                if (args.length < 2) {
                    player.sendMessage("§cاكتب اسم اللاعب للدعوة.");
                    return true;
                }
                Player target = Bukkit.getPlayerExact(args[1]);
                if (target == null) {
                    player.sendMessage("§cاللاعب غير متصل.");
                    return true;
                }
                if (target.equals(player)) {
                    player.sendMessage("§cلا يمكنك دعوة نفسك.");
                    return true;
                }
                boolean success = teamManager.invite(player, target);
                if (success) {
                    player.sendMessage("§aتم إرسال الدعوة إلى " + target.getName());
                }
            }
            case "leave" -> {
                teamManager.leaveTeam(player);
            }
            case "info" -> {
                var team = teamManager.getTeam(player);
                if (team == null) {
                    player.sendMessage("§cأنت لست في أي فريق.");
                    return true;
                }
                player.sendMessage("§aقائد الفريق: " + Bukkit.getOfflinePlayer(team.getLeader()).getName());
                player.sendMessage("§aأعضاء الفريق:");
                for (var memberUUID : team.getMembers()) {
                    player.sendMessage(" - " + Bukkit.getOfflinePlayer(memberUUID).getName());
                }
            }
            default -> player.sendMessage("§cالأمر غير معروف.");
        }

        return true;
    }
}
