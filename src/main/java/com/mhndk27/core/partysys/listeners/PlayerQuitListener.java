package com.mhndk27.core.partysys.listeners;

import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import com.mhndk27.core.partysys.PartyManager;
import com.mhndk27.core.rooms.RoomManager; // Import RoomManager


public class PlayerQuitListener implements Listener {

    private final PartyManager partyManager;
    private final RoomManager roomManager; // Add RoomManager as a dependency

    public PlayerQuitListener(PartyManager partyManager, RoomManager roomManager) {
        this.partyManager = partyManager;
        this.roomManager = roomManager; // Initialize RoomManager
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        if (partyManager.isInParty(playerUUID)) {
            partyManager.leaveParty(playerUUID);
        }
        roomManager.releaseRoomForMember(playerUUID); // Release room occupancy
    }
}
