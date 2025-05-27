package com.mhndk27.tpm.core;

import com.mhndk27.tpm.TPMPlugin;
import com.mhndk27.tpm.utils.WorldEditUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.*;

public class RoomManager {

    private final TPMPlugin plugin;
    private final Map<UUID, Integer> playerRooms = new HashMap<>(); // يخزن غرفة اللاعب أو بارتيه
    private final Map<Integer, Location> roomCenters = new HashMap<>();
    private int nextRoomId = 2; // نبدأ من 2 لأن 1.yml موجود مسبق

    private static final int DISTANCE_BETWEEN_ROOMS = 1000;
    private static final int ROOM_HEIGHT = 100;
    private static final String SCHEMATIC_PATH = "schematics/zswaitroom.schem";

    private final World world;

    public RoomManager(TPMPlugin plugin) {
        this.plugin = plugin;
        this.world = Bukkit.getWorld("world");
        loadExistingRooms();
    }

    private void loadExistingRooms() {
        // لو حاب تخزن غرف مسبقاً، الآن ندخل هنا
    }

    private Location getRoomCenter(int roomId) {
        return new Location(world, roomId * DISTANCE_BETWEEN_ROOMS, 60, 0);
    }

    public void createRoomForPlayer(Player player) {
        if (playerRooms.containsKey(player.getUniqueId())) {
            player.sendMessage("§cYou already have a room!");
            return;
        }
        int roomId = nextRoomId++;
        Location center = getRoomCenter(roomId);
        roomCenters.put(roomId, center);
        playerRooms.put(player.getUniqueId(), roomId);

        try {
            WorldEditUtil.pasteSchematic(world, center, plugin.getResource(SCHEMATIC_PATH));
        } catch (IOException e) {
            player.sendMessage("§cError loading room schematic.");
            e.printStackTrace();
        }

        player.teleport(center.clone().add(0, 5, 0));
    }

    public void createRoomForParty(Player leader) {
        UUID leaderUUID = leader.getUniqueId();
        if (playerRooms.containsKey(leaderUUID)) {
            leader.sendMessage("§cYou already have a room!");
            return;
        }
        int roomId = nextRoomId++;
        Location center = getRoomCenter(roomId);
        roomCenters.put(roomId, center);
        playerRooms.put(leaderUUID, roomId);

        // توجيه جميع أعضاء البارتي لنفس الغرفة
        List<Player> partyMembers = plugin.getPartyChecker()
                .getPartyMembers(leader);
        partyMembers.forEach(p -> playerRooms.put(p.getUniqueId(), roomId));

        try {
            WorldEditUtil.pasteSchematic(world, center, plugin.getResource(SCHEMATIC_PATH));
        } catch (IOException e) {
            leader.sendMessage("§cError loading room schematic.");
            e.printStackTrace();
        }

        partyMembers.forEach(p -> p.teleport(center.clone().add(0, 5, 0)));
    }

    public void deleteRoomByPlayer(Player player) {
        Integer roomId = playerRooms.get(player.getUniqueId());
        if (roomId == null) {
            player.sendMessage("§cYou do not have a room!");
            return;
        }
        // ممكن تضيف إزالة المبنى هنا باستخدام WorldEdit
        playerRooms.remove(player.getUniqueId());
        player.sendMessage("§aRoom deleted.");
    }

    public void deleteRoomByPartyLeader(Player leader) {
        Integer roomId = playerRooms.get(leader.getUniqueId());
        if (roomId == null) {
            leader.sendMessage("§cYou do not have a room!");
            return;
        }
        // إزالة كل اللاعبين من الغرفة
        playerRooms.entrySet().removeIf(entry -> entry.getValue().equals(roomId));
        leader.sendMessage("§aRoom deleted.");
    }
}
