package com.mhndk27.minigames.arenas;

import com.mhndk27.minigames.MiniGamesPlugin;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WaitingRoomManager {

    private final MiniGamesPlugin plugin;
    private final Map<UUID, WaitingRoom> activeRooms = new HashMap<>();
    private final Map<UUID, UUID> playerRoomMap = new HashMap<>(); // ربط اللاعب بالغرفة
    private int roomCount = 0;

    // مسافة بين كل غرفة وأخرى على محور X (مثلاً 500 بلوك)
    private final int ROOM_DISTANCE = 500;

    public WaitingRoomManager(MiniGamesPlugin plugin) {
        this.plugin = plugin;
    }

    // بناء غرفة انتظار جديدة وتخزينها
    public WaitingRoom createWaitingRoom() {
        Location baseLoc = plugin.getServer().getWorlds().get(0).getSpawnLocation(); // لوبي كنقطة بداية
        Location roomLoc = baseLoc.clone().add(roomCount * ROOM_DISTANCE, 0, 0);
        roomCount++;

        ArenaBuilder builder = new ArenaBuilder(plugin);
        builder.buildWaitingRoom(roomLoc);

        WaitingRoom room = new WaitingRoom(UUID.randomUUID(), roomLoc);
        activeRooms.put(room.getRoomId(), room);
        return room;
    }

    public WaitingRoom getRoom(UUID roomId) {
        return activeRooms.get(roomId);
    }

    // حذف غرفة انتظار
    public void removeRoom(UUID roomId) {
        activeRooms.remove(roomId);
    }

    // حذف كل الغرف عند تعطيل البلوقن
    public void clearAllRooms() {
        activeRooms.clear();
        playerRoomMap.clear();
    }

    // حساب موقع جديد لغرفة الانتظار التالية
    public Location getNextAvailableLocation() {
        Location baseLoc = plugin.getServer().getWorlds().get(0).getSpawnLocation(); // نقطة البداية
        return baseLoc.clone().add(roomCount * ROOM_DISTANCE, 0, 0);
    }

    // ===== الدالة المطلوبة: تعيين غرفة انتظار للاعب =====
    public WaitingRoom assignRoom(Player player) {
        // إذا اللاعب موجود مسبقًا في غرفة، رجعها
        if (playerRoomMap.containsKey(player.getUniqueId())) {
            UUID roomId = playerRoomMap.get(player.getUniqueId());
            WaitingRoom room = activeRooms.get(roomId);
            if (room != null) {
                return room;
            }
        }

        // لو ما عنده غرفة، أنشئ غرفة جديدة
        WaitingRoom newRoom = createWaitingRoom();

        // سجل الربط بين اللاعب والغرفة
        playerRoomMap.put(player.getUniqueId(), newRoom.getRoomId());

        return newRoom;
    }
}
