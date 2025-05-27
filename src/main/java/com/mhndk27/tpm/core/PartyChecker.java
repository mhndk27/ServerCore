package com.mhndk27.tpm.core;

import com.mhndk27.tpm.TPMPlugin;
import com.mhndk27.partysys.PartyManager;
import com.mhndk27.partysys.Party;
import org.bukkit.entity.Player;

public class PartyChecker {

    private final TPMPlugin plugin;
    private final PartyManager partyManager;

    public PartyChecker(TPMPlugin plugin) {
        this.plugin = plugin;
        this.partyManager = PartyManager.getInstance(); // حسب مشروعك في PartySystem
    }

    public boolean isInParty(Player player) {
        return partyManager.isInParty(player.getUniqueId());
    }

    public boolean isLeader(Player player) {
        Party party = partyManager.getPartyOfPlayer(player.getUniqueId());
        return party != null && party.isLeader(player.getUniqueId());
    }

    public boolean isAlone(Player player) {
        return !isInParty(player);
    }

    public void kickFromParty(Player player) {
        Party party = partyManager.getPartyOfPlayer(player.getUniqueId());
        if (party != null) {
            party.removeMember(player.getUniqueId());
        }
    }
}
