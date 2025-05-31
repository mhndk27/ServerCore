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
            if (rooms.get(i).isEmpty()) // تحقق إذا الغرفة فاضية
                return i;
        }
        return null; // إذا لم توجد أي غرفة فاضية
    }

    public Integer findSuitableRoom(PartySystemAPI partySystem, UUID playerUUID) {
        for (int i = 1; i <= ROOM_COUNT; i++) {
            List<UUID> roomMembers = rooms.get(i);
            if (roomMembers.isEmpty()) {
                return i; // الغرفة فاضية
            }
            // تحقق إذا اللاعب في نفس البارتي مع الموجودين في الغرفة
            UUID leader = partySystem.getPartyLeader(playerUUID);
            if (leader != null && roomMembers.contains(leader)) {
                return i; // الغرفة تحتوي على أعضاء نفس البارتي
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
        boolean inParty = partySystem.isPlayerInParty(playerUUID);
        Integer currentRoom = getPlayerRoom(playerUUID);

        // تحقق إذا اللاعب موجود بالفعل في غرفة
        if (currentRoom != null) {
            Player player = getPlayer(playerUUID);
            if (player != null) {
                player.sendMessage("§c§l[Error] §r§cYou are already in a room (Room #" + currentRoom + ").");
            }
            return false; // لا يتم النقل إذا كان اللاعب بالفعل في غرفة
        }

        // البحث عن أول غرفة مناسبة (فاضية أو تحتوي على أعضاء نفس البارتي)
        Integer roomId = findSuitableRoom(partySystem, playerUUID);
        if (roomId == null) {
            Player player = getPlayer(playerUUID);
            if (player != null) {
                player.sendMessage("§c§l[Error] §r§cNo available rooms at the moment.");
            }
            return false; // لا يتم النقل إذا لم توجد أي غرفة مناسبة
        }

        if (!inParty) {
            teleportPartyToRoom(Collections.singletonList(playerUUID), roomId);
            addPlayersToRoom(roomId, Collections.singletonList(playerUUID));
            Player player = getPlayer(playerUUID);
            if (player != null) {
                player.sendMessage("§a§l[Success] §r§aYou have been teleported to Room #" + roomId + ".");
            }
            return true;
        }

        UUID leader = partySystem.getPartyLeader(playerUUID);
        List<UUID> members = partySystem.getPartyMembersOfPlayer(playerUUID);
        Integer partyRoom = getPartyRoom(members);

        if (!playerUUID.equals(leader)) {
            if (partyRoom != null) {
                Location center = getRoomCenter(partyRoom);
                if (center != null) {
                    Location facingLocation = center.clone();
                    facingLocation.setYaw(180);
                    teleportPlayerToLocation(playerUUID, facingLocation);
                    addPlayersToRoom(partyRoom, Collections.singletonList(playerUUID));
                    Player player = getPlayer(playerUUID);
                    if (player != null) {
                        player.sendMessage("§b§l[Info] §r§bYou have joined your party in Room #" + partyRoom + ".");
                    }
                }
            } else {
                Player player = getPlayer(playerUUID);
                if (player != null) {
                    player.sendMessage(
                            "§c§l[Error] §r§cWait for the party leader to join the room or leave the party.");
                }
            }
            return false;
        }

        int targetRoom = (partyRoom != null) ? partyRoom : roomId;
        Location center = getRoomCenter(targetRoom);
        if (center != null) {
            Location facingLocation = center.clone();
            facingLocation.setYaw(180);
            teleportPlayersToLocation(members, facingLocation);
            addPlayersToRoom(targetRoom, members);
            for (UUID uuid : members) {
                Player member = getPlayer(uuid);
                if (member != null && !uuid.equals(leader)) {
                    member.sendMessage("§a§l[Success] §r§aThe party leader moved you to Room #" + targetRoom + ".");
                }
            }
        }
        return true;
    }

    public void handleLobbyCommand(
            PartySystemAPI partySystem,
            UUID playerUUID,
            Location lobbyLocation) {
        boolean inParty = partySystem.isPlayerInParty(playerUUID);
        if (!inParty) {
            teleportPlayerToLocation(playerUUID, lobbyLocation);
            Integer roomId = getPlayerRoom(playerUUID);
            if (roomId != null) {
                clearRoom(roomId);
                removePlayer(playerUUID); // إزالة اللاعب من الغرفة بشكل صحيح
            }
            Player player = getPlayer(playerUUID);
            if (player != null) {
                player.sendMessage("§e§l[Notice] §r§eYou have been teleported to the lobby.");
            }
            return;
        }
        UUID leader = partySystem.getPartyLeader(playerUUID);
        List<UUID> members = partySystem.getPartyMembersOfPlayer(playerUUID);

        if (!playerUUID.equals(leader)) {
            partySystem.kickPlayerFromParty(playerUUID);
            Player player = getPlayer(playerUUID);
            if (player != null) {
                player.sendMessage("§c§l[Warning] §r§cYou have been removed from the party.");
            }
            return;
        }

        teleportPlayersToLocation(members, lobbyLocation);
        for (UUID uuid : members) {
            Player member = getPlayer(uuid);
            if (member != null) {
                member.sendMessage("§e§l[Notice] §r§eThe party leader sent you to the lobby.");
            }
        }
        Integer roomId = getPlayerRoom(playerUUID);
        if (roomId != null) {
            clearRoom(roomId);
            removePlayer(playerUUID); // إزالة القائد وأعضاء البارتي من الغرفة بشكل صحيح
        }
    }

    public void syncPartyMemberWithLeaderRoom(PartySystemAPI partySystem, UUID newMemberUUID) {
        if (partySystem == null)
            return;
        UUID leader = partySystem.getPartyLeader(newMemberUUID);
        if (leader == null)
            return;
        Integer leaderRoom = getPlayerRoom(leader);
        if (leaderRoom == null)
            return;
        Player leaderPlayer = getPlayer(leader);
        if (leaderPlayer == null)
            return;
        Integer memberRoom = getPlayerRoom(newMemberUUID);
        if (leaderRoom.equals(memberRoom))
            return;
        Location center = getRoomCenter(leaderRoom);
        if (center != null) {
            Location facingLocation = center.clone();
            facingLocation.setYaw(180);
            teleportPlayerToLocation(newMemberUUID, facingLocation);
            addPlayersToRoom(leaderRoom, Collections.singletonList(newMemberUUID));
        }
    }
}
