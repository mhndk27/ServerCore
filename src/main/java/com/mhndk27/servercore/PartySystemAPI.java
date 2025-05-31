package com.mhndk27.servercore;

import java.util.List;
import java.util.UUID;

/**
 * PartySystemAPI: واجهة يجب أن تطبقها أي إضافة بارتي تريد ربطها مع RoomManager.
 */
public interface PartySystemAPI {
    boolean isPlayerInParty(UUID playerUUID);

    UUID getPartyLeader(UUID playerUUID);

    List<UUID> getPartyMembersOfPlayer(UUID playerUUID);

    Object getPlayerParty(UUID playerUUID); // Replace Object with actual Party class if available

    int getPartySize(UUID playerUUID);

    void kickPlayerFromParty(UUID targetUUID);
}
