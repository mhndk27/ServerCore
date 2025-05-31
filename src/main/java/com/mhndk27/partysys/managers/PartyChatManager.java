package com.mhndk27.partysys.managers;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.mhndk27.partysys.Party;
import com.mhndk27.partysys.PartyManager;
import com.mhndk27.partysys.utils.MessageUtils;

public class PartyChatManager {
    private static PartyChatManager instance;
    private final PartyManager partyManager;
    private final Set<UUID> partyChatEnabled = new HashSet<>();

    public PartyChatManager(PartyManager partyManager) {
        this.partyManager = partyManager;
        instance = this;
    }

    public static PartyChatManager getInstance() {
        return instance;
    }

    public boolean togglePartyChat(UUID playerUUID) {
        if (!partyManager.isInParty(playerUUID)) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player != null) {
                player.sendMessage(MessageUtils.error("You must be in a party to use party chat."));
            }
            return false;
        }

        if (partyChatEnabled.contains(playerUUID)) {
            partyChatEnabled.remove(playerUUID);
            return false;
        } else {
            partyChatEnabled.add(playerUUID);
            return true;
        }
    }

    public boolean isPartyChatEnabled(UUID playerUUID) {
        return partyChatEnabled.contains(playerUUID);
    }

    public void disablePartyChat(UUID playerUUID) {
        partyChatEnabled.remove(playerUUID);
    }

    public void sendPartyMessage(Player sender, String message) {
        UUID senderUUID = sender.getUniqueId();
        Party party = partyManager.getParty(senderUUID);

        if (party == null) {
            sender.sendMessage(MessageUtils.error("You are not in a party."));
            return;
        }

        var formattedMessage = MessageUtils.partyChat(sender.getName(), message);

        for (UUID memberUUID : party.getMembers()) {
            Player member = Bukkit.getPlayer(memberUUID);
            if (member != null) {
                member.sendMessage(formattedMessage);
            }
        }
    }
}
