package com.mhndk27.teamsys;

import org.bukkit.entity.Player;

import java.util.*;

public class TeamManager {

    private final TeamsSystem plugin;
    private final InviteManager inviteManager;

    private final Map<UUID, Team> playerTeams = new HashMap<>();

    public TeamManager(TeamsSystem plugin, InviteManager inviteManager) {
        this.plugin = plugin;
        this.inviteManager = inviteManager;
    }

    public Team getTeam(Player player) {
        return playerTeams.get(player.getUniqueId());
    }

    public boolean isInTeam(Player player) {
        return playerTeams.containsKey(player.getUniqueId());
    }

    public Team createTeam(Player leader) {
        Team team = new Team(leader.getUniqueId());
        playerTeams.put(leader.getUniqueId(), team);
        return team;
    }

    public void addPlayerToTeam(Player player, Team team) {
        team.addMember(player.getUniqueId());
        playerTeams.put(player.getUniqueId(), team);
    }

    public void removePlayerFromTeam(Player player) {
        Team team = playerTeams.get(player.getUniqueId());
        if (team != null) {
            team.removeMember(player.getUniqueId());
            playerTeams.remove(player.getUniqueId());

            if (team.isLeader(player.getUniqueId())) {
                if (!team.getMembers().isEmpty()) {
                    UUID newLeader = team.getMembers().iterator().next();
                    team.setLeader(newLeader);
                    // You can notify the new leader here if you want
                } else {
                    // Team empty, remove all references (optional)
                }
            }
        }
    }

    public boolean invite(Player inviter, Player invitee) {
        Team inviterTeam = playerTeams.get(inviter.getUniqueId());
        if (inviterTeam == null) {
            inviter.sendMessage("§cأنت لست في فريق لتدعوه.");
            return false;
        }
        if (playerTeams.containsKey(invitee.getUniqueId())) {
            inviter.sendMessage("§cاللاعب في فريق بالفعل.");
            return false;
        }
        if (inviterTeam.getMembers().size() >= 4) {
            inviter.sendMessage("§cفريقك ممتلئ.");
            return false;
        }

        inviteManager.addInvite(invitee, inviter);
        inviter.sendMessage("§aتم إرسال الدعوة.");
        invitee.sendMessage("§aلديك دعوة من " + inviter.getName() + " اكتب /team accept للانضمام.");
        return true;
    }

    public void acceptInvite(Player invitee) {
        if (!inviteManager.hasInvite(invitee)) {
            invitee.sendMessage("§cليس لديك دعوة.");
            return;
        }
        UUID inviterUUID = inviteManager.getInviterUUID(invitee);
        Team inviterTeam = playerTeams.get(inviterUUID);
        if (inviterTeam == null) {
            invitee.sendMessage("§cفريق الداعي غير موجود.");
            inviteManager.removeInvite(invitee);
            return;
        }
        if (inviterTeam.getMembers().size() >= 4) {
            invitee.sendMessage("§cفريق الداعي ممتلئ.");
            inviteManager.removeInvite(invitee);
            return;
        }

        addPlayerToTeam(invitee, inviterTeam);
        inviteManager.removeInvite(invitee);
        invitee.sendMessage("§aانضممت إلى الفريق.");
    }

    public void leaveTeam(Player player) {
        removePlayerFromTeam(player);
        player.sendMessage("§aخرجت من الفريق.");
        createTeam(player);
        player.sendMessage("§aتم إنشاء فريق خاص بك.");
    }

    public void promote(Player leader, Player target) {
        Team team = playerTeams.get(leader.getUniqueId());
        if (team == null || !team.isLeader(leader.getUniqueId())) {
            leader.sendMessage("§cأنت لست قائد الفريق.");
            return;
        }
        if (!team.getMembers().contains(target.getUniqueId())) {
            leader.sendMessage("§cاللاعب ليس في فريقك.");
            return;
        }
        team.setLeader(target.getUniqueId());
        leader.sendMessage("§aتم ترقية " + target.getName() + " إلى قائد الفريق.");
        target.sendMessage("§aتمت ترقيتك إلى قائد الفريق.");
    }

    public void kick(Player leader, Player target) {
        Team team = playerTeams.get(leader.getUniqueId());
        if (team == null || !team.isLeader(leader.getUniqueId())) {
            leader.sendMessage("§cأنت لست قائد الفريق.");
            return;
        }
        if (!team.getMembers().contains(target.getUniqueId())) {
            leader.sendMessage("§cاللاعب ليس في فريقك.");
            return;
        }
        team.removeMember(target.getUniqueId());
        playerTeams.remove(target.getUniqueId());

        leader.sendMessage("§aتم طرد " + target.getName() + " من الفريق.");
        target.sendMessage("§cتم طردك من الفريق.");
    }

    public void disband(Player leader) {
        Team team = playerTeams.get(leader.getUniqueId());
        if (team == null || !team.isLeader(leader.getUniqueId())) {
            leader.sendMessage("§cأنت لست قائد الفريق.");
            return;
        }
        for (UUID memberUUID : new HashSet<>(team.getMembers())) {
            Player member = plugin.getServer().getPlayer(memberUUID);
            if (member != null && member.isOnline()) {
                member.sendMessage("§cتم حل الفريق من قبل القائد.");
            }
            playerTeams.remove(memberUUID);
        }
        playerTeams.remove(leader.getUniqueId());
        leader.sendMessage("§aتم حل الفريق.");
    }
}
