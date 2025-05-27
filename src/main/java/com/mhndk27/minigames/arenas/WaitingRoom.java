package com.mhndk27.minigames.arenas;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class WaitingRoom {

    private final UUID roomId;
    private final Location location;
    private final Set<UUID> players;

    public WaitingRoom(UUID roomId, Location location) {
        this.roomId = roomId;
        this.location = location;
        this.players = new HashSet<>();
    }

    public UUID getRoomId() {
        return roomId;
    }

    public Location getLocation() {
        return location;
    }

    public Set<UUID> getPlayers() {
        return players;
    }

    // إضافة لاعب للغرفة
    public void addPlayer(Player player) {
        players.add(player.getUniqueId());
    }

    // إزالة لاعب من الغرفة
    public void removePlayer(Player player) {
        players.remove(player.getUniqueId());
    }

    // نقل لاعب لموقع الغرفة
    public void teleportPlayer(Player player) {
        player.teleport(location);
    }

    // التحقق إذا الغرفة فاضية
    public boolean isEmpty() {
        return players.isEmpty();
    }

    // نقل كل اللاعبين في الغرفة
    public void teleportAllPlayers() {
        for (UUID uuid : players) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {
                teleportPlayer(player);
            }
        }
    }
}
