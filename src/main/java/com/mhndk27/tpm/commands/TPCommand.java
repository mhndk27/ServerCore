package com.mhndk27.tpm.commands;

import com.mhndk27.tpm.TPMPlugin;
import com.mhndk27.tpm.core.PartyChecker;
import com.mhndk27.tpm.core.RoomManager;
import com.mhndk27.tpm.core.NPCManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TPCommand implements CommandExecutor {

    private final TPMPlugin plugin;

    public TPCommand(TPMPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§cPlease specify a player name!");
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            sender.sendMessage("§cPlayer not found or offline.");
            return true;
        }

        PartyChecker partyChecker = plugin.getPartyChecker();
        RoomManager roomManager = plugin.getRoomManager();
        NPCManager npcManager = plugin.getNpcManager();

        if (!partyChecker.isAlone(target)) {
            if (!partyChecker.isInParty(target)) {
                // عضو في ميني قيم وليس قائد، رفض النقل
                target.sendMessage("§eYou are in a party but not the leader, please wait for your leader.");
                return true;
            }
            // قائد البارتي ينقل كل الفريق
            roomManager.createRoomForParty(target);
            npcManager.createNPCForRoom(target);
            target.sendMessage("§aRoom created and party teleported.");
        } else {
            // لاعب لوحده ينشئ غرفة وينقله
            roomManager.createRoomForPlayer(target);
            npcManager.createNPCForRoom(target);
            target.sendMessage("§aRoom created and teleported.");
        }

        return true;
    }
}
