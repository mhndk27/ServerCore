package com.mhndk27.core.partysys.managers;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import com.mhndk27.core.partysys.Party;
import com.mhndk27.core.partysys.PartyManager;
import com.mhndk27.core.rooms.Room;
import com.mhndk27.core.rooms.RoomManager;
import com.mhndk27.core.utils.MessageUtils; // Update import to general utils
import com.mhndk27.core.utils.TeleportUtils;

public class PartyInviteManager {
    private static PartyInviteManager instance;
    private final PartyManager partyManager;
    private final RoomManager roomManager; // Add RoomManager as a dependency
    private final Map<UUID, InviteData> pendingInvites = new ConcurrentHashMap<>();

    public PartyInviteManager(PartyManager partyManager, RoomManager roomManager) {
        this.partyManager = partyManager;
        this.roomManager = roomManager; // Initialize RoomManager
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

                // نقل العضو الجديد تلقائيًا إلى الغرفة المحجوزة للقائد إذا كان داخل غرفة انتظار
                Room leaderRoom = roomManager.getRoomByPlayer(invite.getLeaderUUID());
                if (leaderRoom != null) {
                    roomManager.transferToNewRoom(targetUUID, leaderRoom); // Release current room
                                                                           // and transfer
                    int[] coords = leaderRoom.getCoordinates();
                    Location roomLocation =
                            new Location(Bukkit.getWorld("world"), coords[0], coords[1], coords[2]);
                    TeleportUtils.teleportToLocation(player, roomLocation);
                    player.sendMessage(MessageUtils
                            .info("You have been teleported to the leader's waiting room."));
                }
            }

            Player leader = Bukkit.getPlayer(invite.getLeaderUUID());
            if (leader != null) {
                leader.sendMessage(MessageUtils
                        .info(partyManager.getPlayerName(targetUUID) + " joined the party."));
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
            leader.sendMessage(MessageUtils
                    .info(partyManager.getPlayerName(targetUUID) + " denied the party invite."));
        }
        return true;
    }
}
