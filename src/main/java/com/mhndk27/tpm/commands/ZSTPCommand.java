package com.mhndk27.tpm.commands;

import com.mhndk27.partysys.Party;
import com.mhndk27.partysys.PartyManager;
import com.mhndk27.tpm.managers.RoomManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class ZSTPCommand implements CommandExecutor {

    private final RoomManager roomManager;

    public ZSTPCommand(RoomManager roomManager) {
        this.roomManager = roomManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) return false;

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) return false;

        Party party = PartyManager.getInstance().getParty(target.getUniqueId()); // تصحيح هنا

        if (party == null) {
            roomManager.createRoomAndTeleport(target, Collections.singletonList(target));
        } else if (party.getLeaderUUID().equals(target.getUniqueId())) {
            // تحويل Set<UUID> إلى List<Player>
            List<Player> members = new ArrayList<>();
            for (UUID memberUUID : party.getMembers()) {
                Player member = Bukkit.getPlayer(memberUUID);
                if (member != null && member.isOnline()) {
                    members.add(member);
                }
            }
            roomManager.createRoomAndTeleport(target, members);
        } else {
            target.sendMessage("§cانتظر القائد يختار الميني قيم.");
        }

        return true;
    }
}
