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

    public Integer getPartyRoom(List<UUID> partyMembers) {
        for (UUID member : partyMembers) {
            Integer room = getPlayerRoom(member);
            if (room != null)
                return room;
        }
        return null;
    }

    /**
     * ينقل جميع أعضاء البارتي إلى مركز الغرفة المحددة مع ضبط الاتجاه.
     */
    public void teleportPartyToRoom(List<UUID> partyMembers, int roomId) {
        Location center = getRoomCenter(roomId);
        if (center == null)
            return;
        Location facingLocation = center.clone();
        facingLocation.setYaw(0); // جهة +Z
        for (UUID uuid : partyMembers) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {
                player.teleport(facingLocation);
            }
        }
    }

    /**
     * منطق نقل اللاعب أو البارتي إلى غرفة.
     */
    public boolean handleRoomJoinRequest(
            PartySystemAPI partySystem,
            UUID playerUUID,
            int roomId) {
        boolean inParty = partySystem.isPlayerInParty(playerUUID);
        if (!inParty) {
            teleportPartyToRoom(Collections.singletonList(playerUUID), roomId);
            addPlayersToRoom(roomId, Collections.singletonList(playerUUID));
            return true;
        }
        UUID leader = partySystem.getPartyLeader(playerUUID);
        List<UUID> members = partySystem.getPartyMembersOfPlayer(playerUUID);
        if (!playerUUID.equals(leader)) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player != null) {
                player.sendMessage("Wait for the party leader to join the room or leave the party.");
            }
            Player leaderPlayer = Bukkit.getPlayer(leader);
            if (leaderPlayer != null) {
                leaderPlayer.sendMessage("A party member tried to join a room. Only the leader can move the party.");
            }
            return false;
        }
        teleportPartyToRoom(members, roomId);
        addPlayersToRoom(roomId, members);
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
     */
    public void handleLobbyCommand(
            PartySystemAPI partySystem,
            UUID playerUUID,
            Location lobbyLocation) {
        boolean inParty = partySystem.isPlayerInParty(playerUUID);
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
        UUID leader = partySystem.getPartyLeader(playerUUID);
        List<UUID> members = partySystem.getPartyMembersOfPlayer(playerUUID);
        if (!playerUUID.equals(leader)) {
            partySystem.kickPlayerFromParty(playerUUID);
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
     * إذا كان القائد في غرفة، ينقل العضو الجديد لنفس غرفة القائد.
     * استدعِ هذه الدالة عند إضافة عضو جديد للبارتي.
     */
    public void syncPartyMemberWithLeaderRoom(PartySystemAPI partySystem, UUID newMemberUUID) {
        UUID leader = partySystem.getPartyLeader(newMemberUUID);
        if (leader == null)
            return;
        Integer leaderRoom = getPlayerRoom(leader);
        if (leaderRoom == null)
            return;
        Integer memberRoom = getPlayerRoom(newMemberUUID);
        if (leaderRoom.equals(memberRoom))
            return;
        Location center = getRoomCenter(leaderRoom);
        if (center != null) {
            Location facingLocation = center.clone();
            facingLocation.setYaw(0);
            Player player = Bukkit.getPlayer(newMemberUUID);
            if (player != null && player.isOnline()) {
                player.teleport(facingLocation);
                addPlayersToRoom(leaderRoom, Collections.singletonList(newMemberUUID));
                player.sendMessage("You have been teleported to your party leader's room.");
            }
        }
    }
}
