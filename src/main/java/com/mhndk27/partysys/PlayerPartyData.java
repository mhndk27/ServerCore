package com.mhndk27.partysys;

import java.util.UUID;

public class PlayerPartyData {

    private final UUID playerUUID;
    private Party party;

    public PlayerPartyData(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public Party getParty() {
        return party;
    }

    public void setParty(Party party) {
        this.party = party;
    }

    public boolean isInParty() {
        return party != null;
    }
}
