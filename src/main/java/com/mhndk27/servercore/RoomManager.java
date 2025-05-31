package com.mhndk27.servercore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class RoomManager {
    private final int ROOM_COUNT = 20;
    private final Map<Integer, List<UUID>> rooms = new HashMap<>();
    private final Map<Integer, Location> roomCenters = new HashMap<>();

    public RoomManager() {
        for (int i = 1; i <= ROOM_COUNT; i++) {
            rooms.put(i, new ArrayList<>());
            // Room 1: 118 85 490, Room 2: 118 85 590, ..., Room 20: 118 85 2390
            roomCenters.put(i, new Location(
                    Bukkit.getWorld("world"), // تأكد أن اسم العالم صحيح
                    118, 85, 490 + (i - 1) * 100));
        }
    }

    // Returns the first available (empty) room id, or null if all are occupied
    public Integer findEmptyRoom() {
        for (int i = 1; i <= ROOM_COUNT; i++) {
            if (rooms.get(i).isEmpty())
                return i;
        }
        return null;
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

    // Returns the roomId if any party member is in a room, otherwise null
    public Integer getPartyRoom(List<UUID> partyMembers) {
        for (UUID member : partyMembers) {
            Integer room = getPlayerRoom(member);
            if (room != null)
                return room;
        }
        return null;
    }

    /**
     * ينقل جميع أعضاء البارتي إلى مركز الغرفة المحددة.
     * 
     * @param partyMembers قائمة UUID لأعضاء البارتي
     * @param roomId       رقم الغرفة
     */
    public void teleportPartyToRoom(List<UUID> partyMembers, int roomId) {
        Location center = getRoomCenter(roomId);
        if (center == null)
            return;
        for (UUID uuid : partyMembers) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {
                player.teleport(center);
            }
        }
    }

    /**
     * منطق نقل اللاعب أو البارتي إلى غرفة.
     * partySystem: كائن نظام البارتي الذي يوفر الدوال المذكورة.
     * playerUUID: اللاعب الذي يريد الدخول.
     * roomId: رقم الغرفة.
     * lobbyLocation: موقع اللوبي (للاستخدام عند الحاجة).
     * 
     * ترجع true إذا تم النقل، false إذا لم يتم (مثلاً اللاعب ليس القائد).
     */
    public boolean handleRoomJoinRequest(
            Object partySystem, // استبدل Object بكلاس البارتي سستم الحقيقي
            UUID playerUUID,
            int roomId) {
        boolean inParty = ((PartySystemInterface) partySystem).isPlayerInParty(playerUUID);
        if (!inParty) {
            teleportPartyToRoom(List.of(playerUUID), roomId);
            addPlayersToRoom(roomId, List.of(playerUUID));
            return true;
        }
        UUID leader = ((PartySystemInterface) partySystem).getPartyLeader(playerUUID);
        List<UUID> members = ((PartySystemInterface) partySystem).getPartyMembersOfPlayer(playerUUID);
        if (!playerUUID.equals(leader)) {
            // أرسل رسالة للاعب نفسه
            Player player = Bukkit.getPlayer(playerUUID);
            if (player != null) {
                player.sendMessage("Wait for the party leader to join the room or leave the party.");
            }
            // أرسل رسالة للقائد (اختياري)
            Player leaderPlayer = Bukkit.getPlayer(leader);
            if (leaderPlayer != null) {
                leaderPlayer.sendMessage("A party member tried to join a room. Only the leader can move the party.");
            }
            return false;
        }
        // هو القائد: انقل كل الأعضاء
        teleportPartyToRoom(members, roomId);
        addPlayersToRoom(roomId, members);
        // أرسل رسالة لكل عضو (عدا القائد)
        for (UUID uuid : members) {
            if (!uuid.equals(leader)) {
                Player member = Bukkit.getPlayer(uuid);
                if (member != null) {
                    member.sendMessage("The party leader moved you to a room.");
                }
            }
        }
        return true;
    }

    /**
     * منطق أمر العودة للوبي.
     * partySystem: كائن نظام البارتي الذي يوفر الدوال المذكورة.
     * playerUUID: اللاعب الذي يريد العودة للوبي.
     * lobbyLocation: موقع اللوبي.
     */
    public void handleLobbyCommand(
            Object partySystem, // استبدل Object بكلاس البارتي سستم الحقيقي
            UUID playerUUID,
            Location lobbyLocation) {
        boolean inParty = ((PartySystemInterface) partySystem).isPlayerInParty(playerUUID);
        if (!inParty) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player != null)
                player.teleport(lobbyLocation);
            Integer roomId = getPlayerRoom(playerUUID);
            if (roomId != null)
                clearRoom(roomId);
            removePlayer(playerUUID);
            return;
        }
        UUID leader = ((PartySystemInterface) partySystem).getPartyLeader(playerUUID);
        List<UUID> members = ((PartySystemInterface) partySystem).getPartyMembersOfPlayer(playerUUID);
        if (!playerUUID.equals(leader)) {
            // اطرده من البارتي
            ((PartySystemInterface) partySystem).kickPlayerFromParty(playerUUID);
            Player player = Bukkit.getPlayer(playerUUID);
            if (player != null) {
                player.teleport(lobbyLocation);
                player.sendMessage("You have been removed from the party and sent to the lobby.");
            }
            Integer roomId = getPlayerRoom(playerUUID);
            if (roomId != null)
                removePlayer(playerUUID);
            return;
        }
        // القائد: رجع كل الأعضاء للوبي بدون تفكيك التيم
        for (UUID uuid : members) {
            Player member = Bukkit.getPlayer(uuid);
            if (member != null) {
                member.teleport(lobbyLocation);
                member.sendMessage("The party leader sent you to the lobby.");
            }
        }
        Integer roomId = getPlayerRoom(playerUUID);
        if (roomId != null)
            clearRoom(roomId);
    }

    /**
     * واجهة توضيحية لدوال البارتي سستم المطلوبة.
     * استبدلها بكلاس البارتي سستم الحقيقي في مشروعك.
     */
    public interface PartySystemInterface {
        boolean isPlayerInParty(UUID playerUUID);

        UUID getPartyLeader(UUID playerUUID);

        List<UUID> getPartyMembersOfPlayer(UUID playerUUID);

        void kickPlayerFromParty(UUID targetUUID);
        // ...أي دوال أخرى تحتاجها...
    }
}
