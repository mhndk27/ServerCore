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

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can execute this command.");
            return true;
        }
        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage("§aاستخدام: /team invite <player>, accept, leave, promote <player>, kick <player>, disband");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "invite":
                if (args.length < 2) {
                    player.sendMessage("§cالرجاء كتابة اسم اللاعب للدعوة.");
                    return true;
                }
                Player invitee = Bukkit.getPlayer(args[1]);
                if (invitee == null || !invitee.isOnline()) {
                    player.sendMessage("§cاللاعب غير موجود أو غير متصل.");
                    return true;
                }
                teamManager.invite(player, invitee);
                break;

            case "accept":
                teamManager.acceptInvite(player);
                break;

            case "leave":
                if (!teamManager.isInTeam(player)) {
                    player.sendMessage("§cأنت لست في فريق.");
                    return true;
                }
                teamManager.leaveTeam(player);
                break;

            case "promote":
                if (args.length < 2) {
                    player.sendMessage("§cالرجاء كتابة اسم اللاعب للترقية.");
                    return true;
                }
                Player promoteTarget = Bukkit.getPlayer(args[1]);
                if (promoteTarget == null || !promoteTarget.isOnline()) {
                    player.sendMessage("§cاللاعب غير موجود أو غير متصل.");
                    return true;
                }
                teamManager.promote(player, promoteTarget);
                break;

            case "kick":
                if (args.length < 2) {
                    player.sendMessage("§cالرجاء كتابة اسم اللاعب للطرد.");
                    return true;
                }
                Player kickTarget = Bukkit.getPlayer(args[1]);
                if (kickTarget == null || !kickTarget.isOnline()) {
                    player.sendMessage("§cاللاعب غير موجود أو غير متصل.");
                    return true;
                }
                teamManager.kick(player, kickTarget);
                break;

            case "disband":
                teamManager.disband(player);
                break;

            default:
                player.sendMessage("§cالأمر غير معروف.");
                break;
        }

        return true;
    }
}
