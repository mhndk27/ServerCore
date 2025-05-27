package com.mhndk27.minigames.game;

import com.mhndk27.minigames.arenas.WaitingRoom;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class GameSession {

    private final UUID sessionId;
    private final WaitingRoom waitingRoom;
    private final List<Player> players;

    public GameSession(UUID sessionId, WaitingRoom waitingRoom, List<Player> players) {
        this.sessionId = sessionId;
        this.waitingRoom = waitingRoom;
        this.players = players;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public WaitingRoom getWaitingRoom() {
        return waitingRoom;
    }

    public List<Player> getPlayers() {
        return players;
    }
}
