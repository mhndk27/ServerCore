package com.mhndk27.tpm.managers;

import com.mhndk27.tpm.utils.SchematicUtils;
import com.mhndk27.tpm.utils.TeleportUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class RoomManager {
    private final Map<UUID, Location> playerRoomMap = new HashMap<>();
    private final Map<Location, Set<UUID>> roomPlayersMap = new HashMap<>();
    private final NPCFileManager npcFileManager;

    private int nextRoomOffset = 0;
    private final int ROOM_SPACING = 100;

    private final Location lobbyLocation = new Location(Bukkit.getWorld("world"), 0, 7, 0);
    private final File schematicFile = new File("C:/Users/mohan/Downloads/THENETHER/server/plugins/TPM/schematics/zswaitroom.schem");
    private final File roomDataFolder = new File("plugins/TPM/rooms");

    private static final int OFFSET_X = 19;
    private static final int OFFSET_Y = -15;
    private static final int OFFSET_Z = -18;

    private static final int ROOM_WIDTH = 36;
    private static final int ROOM_HEIGHT = 18;
    private static final int ROOM_LENGTH = 37;

    public RoomManager(NPCFileManager npcFileManager) {
        this.npcFileManager = npcFileManager;
        if (!roomDataFolder.exists()) roomDataFolder.mkdirs();
    }

    public boolean hasRoom(Player player) {
        UUID uuid = player.getUniqueId();
        if (playerRoomMap.containsKey(uuid)) return true;

        File dataFile = new File(roomDataFolder, uuid + ".yml");
        if (dataFile.exists()) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(dataFile);
            String worldName = config.getString("world");
            double x = config.getDouble("x");
            double y = config.getDouble("y");
            double z = config.getDouble("z");

            Location loc = new Location(Bukkit.getWorld(worldName), x, y, z);
            playerRoomMap.put(uuid, loc);

            roomPlayersMap.putIfAbsent(loc, new HashSet<>());
            roomPlayersMap.get(loc).add(uuid);
            return true;
        }

        return false;
    }

    public Location getRoom(Player player) {
        hasRoom(player);
        return playerRoomMap.get(player.getUniqueId());
    }

    public void createRoomAndTeleport(Player initiator, List<Player> players) {
        int x = 100;
        int y = 100;
        int z = 500 + (nextRoomOffset * ROOM_SPACING);
        nextRoomOffset++;

        Location roomOrigin = new Location(Bukkit.getWorld("world"), x, y, z);
        SchematicUtils.pasteSchematic(schematicFile, roomOrigin);
        npcFileManager.createNPCFile(roomOrigin);

        roomPlayersMap.put(roomOrigin, new HashSet<>());

        for (Player p : players) {
            UUID uuid = p.getUniqueId();
            playerRoomMap.put(uuid, roomOrigin);
            roomPlayersMap.get(roomOrigin).add(uuid);
            saveRoomLocation(uuid, roomOrigin);

            Location teleportLocation = new Location(
                    roomOrigin.getWorld(),
                    roomOrigin.getX() + OFFSET_X,
                    roomOrigin.getY() + OFFSET_Y,
                    roomOrigin.getZ() + OFFSET_Z
            );
            TeleportUtils.teleport(p, teleportLocation);
        }
    }

    public void removePlayer(Player player) {
        UUID uuid = player.getUniqueId();
        Location room = playerRoomMap.remove(uuid);

        if (room != null) {
            Set<UUID> members = roomPlayersMap.get(room);
            if (members != null) {
                members.remove(uuid);
                if (members.isEmpty()) {
                    clearRoom(room); // ✅ استبدال الغرفة بالهواء وحذف ملفاتها
                }
            }
        }

        deleteRoomFile(uuid);
    }

    public void teleportToLobby(Player player) {
        TeleportUtils.teleport(player, lobbyLocation);
    }

    public void syncNewMember(Player player, UUID leaderUUID) {
        Location leaderRoom = playerRoomMap.get(leaderUUID);
        if (leaderRoom == null) return;

        UUID uuid = player.getUniqueId();
        playerRoomMap.put(uuid, leaderRoom);
        roomPlayersMap.get(leaderRoom).add(uuid);
        saveRoomLocation(uuid, leaderRoom);

        Location teleportLocation = new Location(
                leaderRoom.getWorld(),
                leaderRoom.getX() + OFFSET_X,
                leaderRoom.getY() + OFFSET_Y,
                leaderRoom.getZ() + OFFSET_Z
        );
        TeleportUtils.teleport(player, teleportLocation);
    }

    private void saveRoomLocation(UUID uuid, Location loc) {
        File file = new File(roomDataFolder, uuid + ".yml");
        YamlConfiguration config = new YamlConfiguration();
        config.set("world", loc.getWorld().getName());
        config.set("x", loc.getX());
        config.set("y", loc.getY());
        config.set("z", loc.getZ());

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteRoomFile(UUID uuid) {
        File file = new File(roomDataFolder, uuid + ".yml");
        if (file.exists()) file.delete();
    }

    private void deleteAllPlayerFilesInRoom(Location room) {
        File[] files = roomDataFolder.listFiles();
        if (files != null) {
            for (File f : files) {
                YamlConfiguration config = YamlConfiguration.loadConfiguration(f);
                String worldName = config.getString("world");
                double x = config.getDouble("x");
                double y = config.getDouble("y");
                double z = config.getDouble("z");

                if (worldName.equals(room.getWorld().getName()) &&
                    x == room.getX() && y == room.getY() && z == room.getZ()) {
                    f.delete();
                }
            }
        }
    }

    private void clearRoom(Location origin) {
        World world = origin.getWorld();
        int startX = origin.getBlockX();
        int startY = origin.getBlockY();
        int startZ = origin.getBlockZ();

        for (int x = 0; x < ROOM_WIDTH; x++) {
            for (int y = 0; y < ROOM_HEIGHT; y++) {
                for (int z = 0; z < ROOM_LENGTH; z++) {
                    world.getBlockAt(startX + x, startY + y, startZ + z).setType(Material.AIR, false);
                }
            }
        }

        npcFileManager.deleteNPCFile(origin);
        deleteAllPlayerFilesInRoom(origin);
        roomPlayersMap.remove(origin);
    }
}
