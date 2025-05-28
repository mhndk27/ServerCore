package com.mhndk27.tpm.managers;

import com.mhndk27.partysys.Party;
import com.mhndk27.tpm.utils.SchematicUtils;
import com.mhndk27.tpm.utils.TeleportUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

public class RoomManager {

    private static final RoomManager instance = new RoomManager();
    public static RoomManager getInstance() {
        return instance;
    }

    // ربط UUID اللاعب بالرقم الغرفة
    private final Map<UUID, Integer> playerRoomMap = new HashMap<>();

    // تتبع رقم ملف NPC (يبدأ من 2)
    private int currentNpcId = 2;

    // ربط رقم الغرفة مع اللاعبين المرتبطين (UUIDs)
    private final Map<Integer, Set<UUID>> roomPlayerMap = new HashMap<>();

    public void createRoomForSolo(Player player) {
        int roomId = currentNpcId++;

        Location pasteLocation = SchematicUtils.pasteSchematic("zswaitroom.schem", roomId);

        TeleportUtils.teleport(player, pasteLocation);

        playerRoomMap.put(player.getUniqueId(), roomId);
        roomPlayerMap.put(roomId, new HashSet<>(Collections.singletonList(player.getUniqueId())));

        NPCFileManager.createReturnNPCFile(player.getName(), roomId, pasteLocation);
    }

    public void createRoomForParty(Party party) {
        int roomId = currentNpcId++;

        Location pasteLocation = SchematicUtils.pasteSchematic("zswaitroom.schem", roomId);

        Set<UUID> members = party.getMembers();
        roomPlayerMap.put(roomId, new HashSet<>(members));

        for (UUID uuid : members) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null && p.isOnline()) {
                playerRoomMap.put(uuid, roomId);
                TeleportUtils.teleport(p, pasteLocation);
            }
        }

        Player leader = Bukkit.getPlayer(party.getLeaderUUID());
        if (leader != null) {
            NPCFileManager.createReturnNPCFile(leader.getName(), roomId, pasteLocation);
        }
    }

    public void removePlayer(UUID uuid) {
        Integer roomId = playerRoomMap.remove(uuid);
        if (roomId == null) return;

        Set<UUID> set = roomPlayerMap.get(roomId);
        if (set != null) {
            set.remove(uuid);
            if (set.isEmpty()) {
                roomPlayerMap.remove(roomId);
                NPCFileManager.deleteNPCFile(roomId);
            }
        }
    }

    public boolean isPlayerInRoom(UUID uuid) {
        return playerRoomMap.containsKey(uuid);
    }

    public Integer getPlayerRoomId(UUID uuid) {
        return playerRoomMap.get(uuid);
    }
}
