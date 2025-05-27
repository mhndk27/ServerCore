package com.mhndk27.minigames.api;

import org.bukkit.entity.Player;

public interface MiniGameAPI {

    /**
     * Start the mini-game for a player or party leader
     * @param player The player who initiates the game
     * @return true if game started successfully
     */
    boolean startMiniGame(Player player);

    /**
     * End the mini-game and return players to lobby
     * @param player Player who requests to end the mini-game
     */
    void endMiniGame(Player player);

    /**
     * Check if a player is currently in a mini-game
     * @param player Player to check
     * @return true if player is in game
     */
    boolean isInGame(Player player);
}
