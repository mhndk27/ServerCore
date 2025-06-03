package com.mhndk27.core.partysys.managers;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import com.mhndk27.core.partysys.Party;
import com.mhndk27.core.partysys.PartyManager;
import com.mhndk27.core.utils.MessageUtils; // Update import to general utils

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
            Player player = Bukkit.getPlayer(playerUUID);
            if (player != null) {
                player.sendMessage(MessageUtils
                        .info("Party chat disabled. All messages will now be visible."));
            }
            return false;
        } else {
            partyChatEnabled.add(playerUUID);
            Player player = Bukkit.getPlayer(playerUUID);
            if (player != null) {
                player.sendMessage(MessageUtils
                        .success("Party chat enabled. You will only see party messages."));
            }
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

        var formattedMessage = MessageUtils.chatMessage(sender.getName(), message);

        for (UUID memberUUID : party.getMembers()) {
            Player member = Bukkit.getPlayer(memberUUID);
            if (member != null) {
                member.sendMessage(formattedMessage);
            }
        }
    }
}
