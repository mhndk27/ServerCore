package com.mhndk27.servercore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * RoomManager: إدارة الغرف وتكاملها مع نظام البارتي.
 */
public class RoomManager {
    private final int ROOM_COUNT = 20;
    private final Map<Integer, List<UUID>> rooms = new HashMap<>();
    private final Map<Integer, Location> roomCenters = new HashMap<>();

    public RoomManager() {
        for (int i = 1; i <= ROOM_COUNT; i++) {
            rooms.put(i, new ArrayList<>());
            roomCenters.put(i, new Location(
                    Bukkit.getWorld("world"),
                    118, 85, 490 + (i - 1) * 100));
        }
    }

    public Integer findEmptyRoom() {
        for (int i = 1; i <= ROOM_COUNT; i++) {
            if (rooms.get(i).isEmpty()) {
                return i; // الغرفة فاضية
            }
        }
        return null; // إذا لم توجد أي غرفة فاضية
    }

    public Integer findSuitableRoom(PartySystemAPI partySystem, UUID playerUUID) {
        for (int i = 1; i <= ROOM_COUNT; i++) {
            List<UUID> roomMembers = rooms.get(i);
            if (roomMembers.isEmpty()) {
                return i; // الغرفة فاضية
            }
            // تحقق إذا الغرفة تحتوي فقط على أعضاء نفس البارتي
            UUID leader = partySystem.getPartyLeader(playerUUID);
            if (leader != null && roomMembers.stream().allMatch(member -> partySystem.isPlayerInParty(member)
                    && partySystem.getPartyLeader(member).equals(leader))) {
                return i; // الغرفة تحتوي على أعضاء نفس البارتي فقط
            }
        }
        return null; // إذا لم توجد أي غرفة مناسبة
    }

    public void addPlayersToRoom(int roomId, List<UUID> players) {
        rooms.get(roomId).addAll(players);
    }

    public void removePlayer(UUID playerUUID) {
        for (List<UUID> members : rooms.values()) {
            members.remove(playerUUID);
        }
    }

    public void clearRoom(int roomId) {
        rooms.get(roomId).clear();
    }

    public Integer getPlayerRoom(UUID playerUUID) {
        for (Map.Entry<Integer, List<UUID>> entry : rooms.entrySet()) {
            if (entry.getValue().contains(playerUUID))
                return entry.getKey();
        }
        return null;
    }

    public Location getRoomCenter(int roomId) {
        return roomCenters.get(roomId);
    }

    public Integer getPartyRoom(List<UUID> partyMembers) {
        for (UUID member : partyMembers) {
            Integer room = getPlayerRoom(member);
            if (room != null)
                return room;
        }
        return null;
    }

    private Player getPlayer(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        return (player != null && player.isOnline()) ? player : null;
    }

    private void teleportPlayerToLocation(UUID uuid, Location loc) {
        Player player = getPlayer(uuid);
        if (player != null) {
            player.teleport(loc);
        }
    }

    private void teleportPlayersToLocation(List<UUID> uuids, Location loc) {
        for (UUID uuid : uuids) {
            teleportPlayerToLocation(uuid, loc);
        }
    }

    public void teleportPartyToRoom(List<UUID> partyMembers, int roomId) {
        Location center = getRoomCenter(roomId);
        if (center == null)
            return;
        Location facingLocation = center.clone();
        facingLocation.setYaw(180);
        teleportPlayersToLocation(partyMembers, facingLocation);
    }

    public boolean handleRoomJoinRequest(
            PartySystemAPI partySystem,
            UUID playerUUID) {
        Integer currentRoom = getPlayerRoom(playerUUID);

        // تحقق إذا اللاعب موجود بالفعل في غرفة
        if (currentRoom != null) {
            Player player = getPlayer(playerUUID);
            if (player != null) {
                player.sendMessage("§c§l[Error] §r§cYou are already in a room (Room #" + currentRoom + ").");
            }
            return false; // لا يتم النقل إذا كان اللاعب بالفعل في غرفة
        }

        // البحث عن أول غرفة فاضية
        Integer roomId = findEmptyRoom();
        if (roomId == null) {
            Player player = getPlayer(playerUUID);
            if (player != null) {
                player.sendMessage("§c§l[Error] §r§cNo available rooms at the moment.");
            }
            return false; // لا يتم النقل إذا لم توجد أي غرفة فاضية
        }

        // نقل اللاعب أو البارتي إلى الغرفة الفاضية
        if (!partySystem.isPlayerInParty(playerUUID)) {
            teleportPartyToRoom(Collections.singletonList(playerUUID), roomId);
            addPlayersToRoom(roomId, Collections.singletonList(playerUUID)); // إنشاء علاقة بين اللاعب والغرفة
        } else {
            UUID leader = partySystem.getPartyLeader(playerUUID);
            if (leader.equals(playerUUID)) { // إذا كان القائد هو من كتب الأمر
                List<UUID> members = partySystem.getPartyMembersOfPlayer(playerUUID);
                teleportPartyToRoom(members, roomId);
                addPlayersToRoom(roomId, members); // إنشاء علاقة بين البارتي والغرفة
            }
        }
        return true;
    }

    public void handleLobbyCommand(
            PartySystemAPI partySystem,
            UUID playerUUID,
            Location lobbyLocation) {
        teleportPlayerToLocation(playerUUID, lobbyLocation);
        cutRoomRelation(playerUUID); // قطع علاقة اللاعب بالغرفة
    }

    public void handlePartyDisband(UUID leaderUUID) {
        cutRoomRelation(leaderUUID); // قطع علاقة القائد بالغرفة
    }

    public void handlePlayerLeaveParty(UUID playerUUID) {
        cutRoomRelation(playerUUID); // قطع علاقة اللاعب بالغرفة
    }

    public void handlePlayerKick(UUID playerUUID) {
        cutRoomRelation(playerUUID); // قطع علاقة اللاعب بالغرفة
    }

    public void handlePlayerDisconnect(UUID playerUUID) {
        cutRoomRelation(playerUUID); // قطع علاقة اللاعب بالغرفة عند مغادرة السيرفر
    }

    private void cutRoomRelation(UUID playerUUID) {
        Integer roomId = getPlayerRoom(playerUUID);
        if (roomId != null) {
            removePlayer(playerUUID); // إزالة اللاعب من الغرفة
            if (rooms.get(roomId).isEmpty()) {
                clearRoom(roomId); // تنظيف الغرفة إذا أصبحت فارغة
            }
        }
    }
}
