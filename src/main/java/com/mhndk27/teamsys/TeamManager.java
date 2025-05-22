package com.mhndk27.teamsys;

import org.bukkit.entity.Player;

import java.util.*;

public class TeamManager {

    private final TeamsSystem plugin;

    // map player uuid -> team
    private final Map<UUID, Team> playerTeams = new HashMap<>();

    public TeamManager(TeamsSystem plugin) {
        this.plugin = plugin;
    }

    // احصل على تيم اللاعب، لو ما موجود اعمل له تيم جديد وهو القائد
    public Team getOrCreateTeam(Player player) {
        return playerTeams.computeIfAbsent(player.getUniqueId(), uuid -> {
            Team team = new Team(player.getUniqueId());
            player.sendMessage("§aتم إنشاء فريق خاص بك وأنت القائد!");
            return team;
        });
    }

    // دعوة لاعب للتيم، الدعوة تلقائياً تجعل الداعي هو القائد
    public boolean invite(Player inviter, Player invitee) {
        Team inviterTeam = getOrCreateTeam(inviter);
        Team inviteeTeam = getOrCreateTeam(invitee);

        if (inviterTeam.equals(inviteeTeam)) {
            inviter.sendMessage("§cأنت واللاعب الآخر في نفس الفريق بالفعل.");
            return false;
        }

        // شروط الحجم
        if (inviterTeam.getMembers().size() >= 4) {
            inviter.sendMessage("§cفريقك مكتمل 4 لاعبين.");
            return false;
        }

        // دمج أعضاء فريق invitee في فريق inviter
        for (UUID memberUUID : inviteeTeam.getMembers()) {
            playerTeams.put(memberUUID, inviterTeam);
            inviterTeam.addMember(memberUUID);
        }

        // تحديث قائد الفريق ليكون الداعي
        inviterTeam.setLeader(inviter.getUniqueId());

        inviter.sendMessage("§aتم قبول الدعوة، أنت الآن قائد الفريق.");
        invitee.sendMessage("§aتم ضمك لفريق " + inviter.getName() + ".");

        return true;
    }

    // خروج لاعب من التيم: يرجع لتيمه الخاص و قائد نفسه
    public void leaveTeam(Player player) {
        Team oldTeam = playerTeams.get(player.getUniqueId());
        if (oldTeam == null) return;

        // إزالة اللاعب من الفريق القديم
        oldTeam.removeMember(player.getUniqueId());
        playerTeams.remove(player.getUniqueId());

        // إذا كان اللاعب هو القائد و الفريق صار فارغ، نحذف الفريق من الماب (كل اللاعبين فيه)
        if (oldTeam.isLeader(player.getUniqueId()) && oldTeam.getMembers().isEmpty()) {
            // حذف كل أعضاء الفريق من playerTeams
            for (UUID memberUUID : new HashSet<>(oldTeam.getMembers())) {
                playerTeams.remove(memberUUID);
            }
            return;
        }

        // إنشاء تيم خاص جديد لللاعب وهو القائد
        Team newTeam = new Team(player.getUniqueId());
        playerTeams.put(player.getUniqueId(), newTeam);
        player.sendMessage("§aخرجت من الفريق وتم إنشاء فريق خاص بك.");
    }

    public Team getTeam(Player player) {
        return playerTeams.get(player.getUniqueId());
    }

    public void removePlayer(Player player) {
        playerTeams.remove(player.getUniqueId());
    }
}
