package com.mhndk27.partysys.managers;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.mhndk27.partysys.Party;
import com.mhndk27.partysys.PartyManager;
import com.mhndk27.partysys.utils.MessageUtils;

public class PartyInviteManager {
    private static PartyInviteManager instance;
    private final PartyManager partyManager;
    private final Map<UUID, InviteData> pendingInvites = new ConcurrentHashMap<>();

    public PartyInviteManager(PartyManager partyManager) {
        this.partyManager = partyManager;
        instance = this;
    }

    public static PartyInviteManager getInstance() {
        return instance;
    }

    public static class InviteData {
        private final UUID leaderUUID;
        private final long expireTime;

        public InviteData(UUID leaderUUID, long expireTime) {
            this.leaderUUID = leaderUUID;
            this.expireTime = expireTime;
        }

        public UUID getLeaderUUID() {
            return leaderUUID;
        }

        public long getExpireTime() {
            return expireTime;
        }
    }

    public void addInvite(UUID targetUUID, UUID leaderUUID) {
        long expireTime = System.currentTimeMillis() + 60_000;
        pendingInvites.put(targetUUID, new InviteData(leaderUUID, expireTime));
    }

    public boolean hasInvite(UUID targetUUID) {
        if (!pendingInvites.containsKey(targetUUID))
            return false;
        InviteData invite = pendingInvites.get(targetUUID);
        if (System.currentTimeMillis() > invite.getExpireTime()) {
            pendingInvites.remove(targetUUID);
            return false;
        }
        return true;
    }

    public InviteData getInviteData(UUID targetUUID) {
        return pendingInvites.get(targetUUID);
    }

    public void removeInvite(UUID targetUUID) {
        pendingInvites.remove(targetUUID);
    }

    public boolean acceptInvite(UUID targetUUID) {
        if (!hasInvite(targetUUID))
            return false;

        InviteData invite = getInviteData(targetUUID);
        Party leaderParty = partyManager.getParty(invite.getLeaderUUID());

        if (leaderParty == null || leaderParty.isFull()) {
            removeInvite(targetUUID);
            Player player = Bukkit.getPlayer(targetUUID);
            if (player != null) {
                player.sendMessage(MessageUtils.error("The party is no longer available or full."));
            }
            return false;
        }

        boolean added = leaderParty.addMember(targetUUID);
        if (added) {
            partyManager.addParty(leaderParty);
            removeInvite(targetUUID);

            Player player = Bukkit.getPlayer(targetUUID);
            if (player != null) {
                player.sendMessage(MessageUtils.success("You joined the party!"));
            }

            Player leader = Bukkit.getPlayer(invite.getLeaderUUID());
            if (leader != null) {
                leader.sendMessage(MessageUtils.info(partyManager.getPlayerName(targetUUID) + " joined the party."));
            }

            return true;
        }
        return false;
    }

    public boolean denyInvite(UUID targetUUID) {
        if (!hasInvite(targetUUID))
            return false;

        InviteData invite = getInviteData(targetUUID);
        removeInvite(targetUUID);

        Player player = Bukkit.getPlayer(targetUUID);
        if (player != null) {
            player.sendMessage(MessageUtils.info("You denied the party invite."));
        }

        Player leader = Bukkit.getPlayer(invite.getLeaderUUID());
        if (leader != null) {
            leader.sendMessage(MessageUtils.info(partyManager.getPlayerName(targetUUID) + " denied the party invite."));
        }
        return true;
    }
}
