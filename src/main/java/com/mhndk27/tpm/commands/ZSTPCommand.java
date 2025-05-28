package com.mhndk27.tpm.commands;

import com.mhndk27.partysys.Party;
import com.mhndk27.partysys.PartyManager;
import com.mhndk27.tpm.managers.RoomManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ZSTPCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // فقط من الكونسل
        if (!(sender instanceof org.bukkit.command.ConsoleCommandSender)) {
            sender.sendMessage("This command can only be run from the console.");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage("Usage: /zstp <player>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null || !target.isOnline()) {
            sender.sendMessage("Player not found or offline.");
            return true;
        }

        PartyManager partyManager = PartyManager.getInstance();
        Party party = partyManager.getParty(target.getUniqueId());

        RoomManager roomManager = RoomManager.getInstance();

        if (party == null) {
            // لاعب منفرد، أنشئ غرفة لوحده
            roomManager.createRoomForSolo(target);
            sender.sendMessage("Created solo room for " + target.getName());
        } else {
            // لاعب في بارتي
            if (party.getLeaderUUID().equals(target.getUniqueId())) {
                // القائد: انشئ غرفة للبارتي كاملة
                roomManager.createRoomForParty(party);
                sender.sendMessage("Created party room for leader " + target.getName());
            } else {
                // عضو فقط: رسالة انتظر القائد
                target.sendMessage("Please wait for your party leader to choose the mini game.");
                sender.sendMessage("Player " + target.getName() + " is not the leader.");
            }
        }

        return true;
    }
}
