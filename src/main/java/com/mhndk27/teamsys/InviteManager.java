package com.mhndk27.teamsys;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InviteManager {

    private final Map<UUID, UUID> invites = new HashMap<>();

    public void addInvite(Player invitee, Player inviter) {
        invites.put(invitee.getUniqueId(), inviter.getUniqueId());
    }

    public boolean hasInvite(Player invitee) {
        return invites.containsKey(invitee.getUniqueId());
    }

    public UUID getInviterUUID(Player invitee) {
        return invites.get(invitee.getUniqueId());
    }

    public void removeInvite(Player invitee) {
        invites.remove(invitee.getUniqueId());
    }
}
