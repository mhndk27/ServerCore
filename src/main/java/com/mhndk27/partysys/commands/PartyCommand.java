package com.mhndk27.partysys.commands;

import com.mhndk27.partysys.Party;
import com.mhndk27.partysys.PartyManager;
import com.mhndk27.partysys.utils.MessageUtils;
import com.mhndk27.partysys.utils.TeleportUtils;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class PartyCommand implements CommandExecutor {

    private final PartyManager partyManager;

    public PartyCommand(PartyManager partyManager) {
        this.partyManager = partyManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can execute this command.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(MessageUtils.error("Usage: /party <create | invite | accept | deny | kick | leave | promote | disband | chat>"));
            return true;
        }

        String sub = args[0].toLowerCase();
        UUID playerUUID = player.getUniqueId();

        switch (sub) {
            case "create" -> {
                if (partyManager.isInParty(playerUUID)) {
                    player.sendMessage(MessageUtils.error("You are already in a party."));
                    return true;
                }
                Party party = partyManager.createParty(playerUUID);
                if (party != null) player.sendMessage(MessageUtils.success("Party created successfully."));
            }
            case "chat" -> {
                if (!partyManager.isInParty(playerUUID)) {
                    player.sendMessage(MessageUtils.error("You are not in a party."));
                    return true;
                }
                boolean enabled = partyManager.togglePartyChat(playerUUID);
                if (enabled)
                    player.sendMessage(MessageUtils.success("Party chat enabled."));
                else
                    player.sendMessage(MessageUtils.info("Party chat disabled."));
            }
            case "invite" -> {
                if (args.length < 2) {
                    player.sendMessage(MessageUtils.error("Usage: /party invite <player>"));
                    return true;
                }
                if (!partyManager.isInParty(playerUUID)) {
                    player.sendMessage(MessageUtils.error("You are not in a party."));
                    return true;
                }
                Party party = partyManager.getParty(playerUUID);
                if (!party.isLeader(playerUUID)) {
                    player.sendMessage(MessageUtils.error("Only the leader can invite."));
                    return true;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    player.sendMessage(MessageUtils.error("Player not found."));
                    return true;
                }
                UUID targetUUID = target.getUniqueId();
                if (partyManager.isInParty(targetUUID)) {
                    player.sendMessage(MessageUtils.error("That player is already in a party."));
                    return true;
                }
                if (party.isFull()) {
                    player.sendMessage(MessageUtils.error("Party is full."));
                    return true;
                }
                partyManager.addInvite(targetUUID, playerUUID);
                target.sendMessage(MessageUtils.success("Invited by " + player.getName() + ". Use /party accept or /party deny."));
                player.sendMessage(MessageUtils.success("Player invited successfully."));
            }
            case "accept" -> {
                if (!partyManager.hasInvite(playerUUID)) {
                    player.sendMessage(MessageUtils.error("No pending party invites."));
                    return true;
                }
                if (partyManager.isInParty(playerUUID)) {
                    player.sendMessage(MessageUtils.error("You are already in a party."));
                    return true;
                }
                PartyManager.InviteData invite = partyManager.getInviteData(playerUUID);
                UUID leaderUUID = invite.getLeaderUUID();

                Party party = partyManager.getParty(leaderUUID);
                if (party == null || !party.isLeader(leaderUUID)) {
                    player.sendMessage(MessageUtils.error("Party invite no longer valid."));
                    partyManager.removeInvite(playerUUID);
                    return true;
                }
                if (party.isFull()) {
                    player.sendMessage(MessageUtils.error("Party is full."));
                    partyManager.removeInvite(playerUUID);
                    return true;
                }
                partyManager.addMember(leaderUUID, playerUUID);
                partyManager.removeInvite(playerUUID);

                player.sendMessage(MessageUtils.success("You joined the party."));
                Player leader = Bukkit.getPlayer(leaderUUID);
                if (leader != null) {
                    leader.sendMessage(MessageUtils.success(player.getName() + " joined the party."));
                }
            }
            case "deny" -> {
                if (!partyManager.hasInvite(playerUUID)) {
                    player.sendMessage(MessageUtils.error("No pending party invites."));
                    return true;
                }
                partyManager.removeInvite(playerUUID);
                player.sendMessage(MessageUtils.success("Party invite declined."));
            }
            case "kick" -> {
                if (args.length < 2) {
                    player.sendMessage(MessageUtils.error("Usage: /party kick <player>"));
                    return true;
                }
                if (!partyManager.isInParty(playerUUID)) {
                    player.sendMessage(MessageUtils.error("You are not in a party."));
                    return true;
                }
                Party party = partyManager.getParty(playerUUID);
                if (!party.isLeader(playerUUID)) {
                    player.sendMessage(MessageUtils.error("Only the leader can kick."));
                    return true;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    player.sendMessage(MessageUtils.error("Player not found."));
                    return true;
                }
                UUID targetUUID = target.getUniqueId();
                if (!party.contains(targetUUID)) {
                    player.sendMessage(MessageUtils.error("That player is not in your party."));
                    return true;
                }
                if (party.isLeader(targetUUID)) {
                    player.sendMessage(MessageUtils.error("You cannot kick the leader."));
                    return true;
                }
                partyManager.removeMember(playerUUID, targetUUID);
                target.sendMessage(MessageUtils.error("You have been kicked from the party."));
                TeleportUtils.teleportToLobby(target);
                player.sendMessage(MessageUtils.success("Player kicked successfully."));
            }
            case "leave" -> {
                if (!partyManager.isInParty(playerUUID)) {
                    player.sendMessage(MessageUtils.error("You are not in a party."));
                    return true;
                }
                Party party = partyManager.getParty(playerUUID);
                boolean wasLeader = party.isLeader(playerUUID);

                partyManager.leaveParty(playerUUID);
                TeleportUtils.teleportToLobby(player);
                player.sendMessage(MessageUtils.success("You left the party."));

                if (wasLeader) {
                    List<UUID> members = party.getMembers().stream()
                            .filter(uuid -> !uuid.equals(playerUUID))
                            .collect(Collectors.toList());

                    if (!members.isEmpty()) {
                        UUID newLeaderUUID = members.get(0);
                        party.setLeader(newLeaderUUID);
                        Player newLeader = Bukkit.getPlayer(newLeaderUUID);
                        if (newLeader != null) {
                            newLeader.sendMessage(MessageUtils.success("You have been promoted to party leader."));
                        }
                    } else {
                        partyManager.removeParty(party);
                    }
                }
            }
            case "promote" -> {
                if (args.length < 2) {
                    player.sendMessage(MessageUtils.error("Usage: /party promote <player>"));
                    return true;
                }
                if (!partyManager.isInParty(playerUUID)) {
                    player.sendMessage(MessageUtils.error("You are not in a party."));
                    return true;
                }
                Party party = partyManager.getParty(playerUUID);
                if (!party.isLeader(playerUUID)) {
                    player.sendMessage(MessageUtils.error("Only the leader can promote."));
                    return true;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    player.sendMessage(MessageUtils.error("Player not found."));
                    return true;
                }
                UUID targetUUID = target.getUniqueId();
                if (!party.contains(targetUUID)) {
                    player.sendMessage(MessageUtils.error("That player is not in your party."));
                    return true;
                }
                partyManager.transferLeadership(playerUUID, targetUUID);
                player.sendMessage(MessageUtils.success("Leadership transferred successfully."));
                target.sendMessage(MessageUtils.success("You have been promoted to party leader."));
            }
            case "disband" -> {
                if (!partyManager.isInParty(playerUUID)) {
                    player.sendMessage(MessageUtils.error("You are not in a party."));
                    return true;
                }
                Party party = partyManager.getParty(playerUUID);
                if (!party.isLeader(playerUUID)) {
                    player.sendMessage(MessageUtils.error("Only the leader can disband the party."));
                    return true;
                }
                for (UUID memberUUID : party.getMembers()) {
                    Player member = Bukkit.getPlayer(memberUUID);
                    if (member != null) {
                        member.sendMessage(MessageUtils.error("The party has been disbanded."));
                        TeleportUtils.teleportToLobby(member);
                    }
                }
                partyManager.removeParty(party);
                player.sendMessage(MessageUtils.success("Party disbanded successfully."));
            }
            default -> player.sendMessage(MessageUtils.error("Unknown subcommand."));
        }

        return true;
    }
}
