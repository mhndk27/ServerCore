package com.mhndk27.tpm.commands;

import com.mhndk27.tpm.TPMPlugin;
import com.mhndk27.tpm.core.RoomManager;
import com.mhndk27.tpm.core.PartyChecker;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReturnCommand implements CommandExecutor {

    private final TPMPlugin plugin;
    private static final Location LOBBY_LOCATION = new Location(Bukkit.getWorld("world"), 0, 7, 0);

    public ReturnCommand(TPMPlugin plugin) {
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

        if (!partyChecker.isInParty(target)) {
            // عضو بدون بارتي => يرجع للوبّي ويحذف الغرفة
            roomManager.deleteRoomByPlayer(target);
            target.teleport(LOBBY_LOCATION);
            target.sendMessage("§aYou have been returned to the lobby.");
        } else if (partyChecker.isLeader(target)) {
            // قائد => يرجع كل الفريق ويحذف الغرفة
            roomManager.deleteRoomByPartyLeader(target);
            target.getParty().getMembers().forEach(p -> p.teleport(LOBBY_LOCATION));
            target.sendMessage("§aParty returned to lobby and room deleted.");
        } else {
            // عضو في بارتي => يطرد من البارتي ويرجع للوبّي ويحذف الغرفة
            partyChecker.kickFromParty(target);
            roomManager.deleteRoomByPlayer(target);
            target.teleport(LOBBY_LOCATION);
            target.sendMessage("§cYou have been kicked from the party and returned to lobby.");
        }

        return true;
    }
}
