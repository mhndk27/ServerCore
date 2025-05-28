package com.mhndk27.tpm.commands;

import com.mhndk27.partysys.Party;
import com.mhndk27.partysys.PartyManager;
import com.mhndk27.tpm.managers.RoomManager;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.UUID;

public class LobbyTPCommand implements CommandExecutor {

    private final RoomManager roomManager;

    public LobbyTPCommand(RoomManager roomManager) {
        this.roomManager = roomManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length != 1) return false;

        Player player = Bukkit.getPlayer(args[0]);
        if (player == null) return false;

        UUID playerUUID = player.getUniqueId();
        PartyManager partyManager = PartyManager.getInstance();
        Party party = partyManager.getParty(playerUUID);

        if (party == null) {
            // مو في بارتي
            roomManager.removePlayer(player);
            roomManager.teleportToLobby(player);
        } else if (party.getLeaderUUID().equals(playerUUID)) {
            // القائد → يرجّع كل أعضاء البارتي للوبي فقط
            for (UUID uuid : party.getMembers()) {
                Player member = Bukkit.getPlayer(uuid);
                if (member != null && member.isOnline()) {
                    roomManager.removePlayer(member);
                    roomManager.teleportToLobby(member);
                }
            }
        } else {
            // عضو → يرجع لحاله + ينطرد من البارتي
            roomManager.removePlayer(player);
            roomManager.teleportToLobby(player);
            partyManager.removeMember(party.getLeaderUUID(), playerUUID); // ✅ تم التعديل هنا
        }

        return true;
    }
}
