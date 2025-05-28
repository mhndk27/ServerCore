package com.mhndk27.tpm.managers;

import com.mhndk27.tpm.utils.SchematicUtils;
import com.mhndk27.tpm.utils.TeleportUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;

public class RoomManager {
    private final Map<UUID, Location> playerRoomMap = new HashMap<>();
    private final Map<Location, Set<UUID>> roomPlayersMap = new HashMap<>();
    private final NPCFileManager npcFileManager;

    private int nextRoomOffset = 0;
    private final int ROOM_SPACING = 100;

    private final Location lobbyLocation = new Location(Bukkit.getWorld("world"), 0, 7, 0);
    private final File schematicFile = new File("C:/Users/mohan/Downloads/THENETHER/server/plugins/TPM/schematics/zswaitroom.schem");

    public RoomManager(NPCFileManager npcFileManager) {
        this.npcFileManager = npcFileManager;
    }

    public boolean hasRoom(Player player) {
        return playerRoomMap.containsKey(player.getUniqueId());
    }

    public Location getRoom(Player player) {
        return playerRoomMap.get(player.getUniqueId());
    }

    public void createRoomAndTeleport(Player initiator, List<Player> players) {
        int x = 100;
        int y = 100;
        int z = 500 + (nextRoomOffset * ROOM_SPACING);
        nextRoomOffset++;

        Location roomOrigin = new Location(Bukkit.getWorld("world"), x, y, z);
        SchematicUtils.pasteSchematic(schematicFile, roomOrigin);
        npcFileManager.createNPCFile(roomOrigin); // NPC داخل الغرفة

        for (Player p : players) {
            playerRoomMap.put(p.getUniqueId(), roomOrigin);
        }

        roomPlayersMap.put(roomOrigin, new HashSet<>());
        for (Player p : players) {
            roomPlayersMap.get(roomOrigin).add(p.getUniqueId());
            TeleportUtils.teleport(p, roomOrigin);
        }
    }

    public void removePlayer(Player player) {
        UUID uuid = player.getUniqueId();
        Location room = playerRoomMap.get(uuid);

        if (room != null) {
            playerRoomMap.remove(uuid);
            Set<UUID> members = roomPlayersMap.get(room);
            if (members != null) {
                members.remove(uuid);
                if (members.isEmpty()) {
                    roomPlayersMap.remove(room);
                    npcFileManager.deleteNPCFile(room);
                }
            }
        }
    }

    public void disbandParty(List<Player> members) {
        if (members.isEmpty()) return;

        Location room = playerRoomMap.get(members.get(0).getUniqueId());
        for (Player p : members) {
            playerRoomMap.remove(p.getUniqueId());
            TeleportUtils.teleport(p, lobbyLocation);
        }

        if (room != null) {
            roomPlayersMap.remove(room);
            npcFileManager.deleteNPCFile(room);
        }
    }

    public void teleportToLobby(Player player) {
        TeleportUtils.teleport(player, lobbyLocation);
    }
}
