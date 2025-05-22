package com.mhndk27.partysys.commands;

import com.mhndk27.partysys.Party;
import com.mhndk27.partysys.PartyManager;
import com.mhndk27.partysys.utils.MessageUtils;
import com.mhndk27.partysys.utils.TeleportUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PartyCommand implements CommandExecutor {

    private final PartyManager partyManager;

    public PartyCommand(PartyManager partyManager) {
        this.partyManager = partyManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(Component.text("Only players can use party commands."));
            return true;
        }

        Player player = (Player) sender;
        UUID playerUUID = player.getUniqueId();

        if (args.length == 0) {
            player.sendMessage(MessageUtils.info("Usage: /party <create|invite|kick|leave|transfer|disband|chat>"));
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "create":
                if (partyManager.isInAnyParty(playerUUID)) {
                    player.sendMessage(MessageUtils.error("You are already in a party."));
                    return true;
                }
                partyManager.createParty(playerUUID);
                player.sendMessage(MessageUtils.success("Party created successfully."));
                break;

            case "invite":
                if (args.length < 2) {
                    player.sendMessage(MessageUtils.error("Usage: /party invite <player>"));
                    return true;
                }
                if (!partyManager.isInAnyParty(playerUUID)) {
                    player.sendMessage(MessageUtils.error("You are not in a party."));
                    return true;
                }
                Party party = partyManager.getParty(playerUUID);
                if (!party.getLeader().equals(playerUUID)) {
                    player.sendMessage(MessageUtils.error("Only the party leader can invite."));
                    return true;
                }

                Player target = Bukkit.getPlayerExact(args[1]);
                if (target == null) {
                    player.sendMessage(MessageUtils.error("Player not found."));
                    return true;
                }
                UUID targetUUID = target.getUniqueId();

                if (partyManager.isInAnyParty(targetUUID)) {
                    player.sendMessage(MessageUtils.error("This player is already in another party."));
                    return true;
                }
                if (party.isFull()) {
                    player.sendMessage(MessageUtils.error("Party is full."));
                    return true;
                }

                boolean added = partyManager.addMemberToParty(playerUUID, targetUUID);
                if (added) {
                    player.sendMessage(MessageUtils.success("Player invited successfully."));
                    target.sendMessage(MessageUtils.info("You have been added to " + player.getName() + "'s party."));
                } else {
                    player.sendMessage(MessageUtils.error("Failed to invite player."));
                }
                break;

            case "kick":
                if (args.length < 2) {
                    player.sendMessage(MessageUtils.error("Usage: /party kick <player>"));
                    return true;
                }
                if (!partyManager.isInAnyParty(playerUUID)) {
                    player.sendMessage(MessageUtils.error("You are not in a party."));
                    return true;
                }
                party = partyManager.getParty(playerUUID);
                if (!party.getLeader().equals(playerUUID)) {
                    player.sendMessage(MessageUtils.error("Only the party leader can kick members."));
                    return true;
                }

                target = Bukkit.getPlayerExact(args[1]);
                if (target == null) {
                    player.sendMessage(MessageUtils.error("Player not found."));
                    return true;
                }
                targetUUID = target.getUniqueId();

                if (!party.contains(targetUUID)) {
                    player.sendMessage(MessageUtils.error("Player is not in your party."));
                    return true;
                }

                boolean removed = partyManager.removeMemberFromParty(playerUUID, targetUUID);
                if (removed) {
                    player.sendMessage(MessageUtils.success("Player kicked from the party."));
                    target.sendMessage(MessageUtils.info("You were kicked from the party."));
                    TeleportUtils.teleportToLobby(target);
                } else {
                    player.sendMessage(MessageUtils.error("Failed to kick player."));
                }
                break;

            case "leave":
                if (!partyManager.isInAnyParty(playerUUID)) {
                    player.sendMessage(MessageUtils.error("You are not in a party."));
                    return true;
                }
                boolean left = partyManager.leaveParty(playerUUID);
                if (left) {
                    player.sendMessage(MessageUtils.success("You left the party."));
                    TeleportUtils.teleportToLobby(player);
                } else {
                    player.sendMessage(MessageUtils.error("Failed to leave the party."));
                }
                break;

            case "transfer":
                if (args.length < 2) {
                    player.sendMessage(MessageUtils.error("Usage: /party transfer <player>"));
                    return true;
                }
                if (!partyManager.isInAnyParty(playerUUID)) {
                    player.sendMessage(MessageUtils.error("You are not in a party."));
                    return true;
                }
                party = partyManager.getParty(playerUUID);
                if (!party.getLeader().equals(playerUUID)) {
                    player.sendMessage(MessageUtils.error("Only the party leader can transfer leadership."));
                    return true;
                }

                target = Bukkit.getPlayerExact(args[1]);
                if (target == null) {
                    player.sendMessage(MessageUtils.error("Player not found."));
                    return true;
                }
                targetUUID = target.getUniqueId();

                if (!party.contains(targetUUID)) {
                    player.sendMessage(MessageUtils.error("Player is not in your party."));
                    return true;
                }

                boolean transferred = partyManager.transferLeadership(playerUUID, targetUUID);
                if (transferred) {
                    player.sendMessage(MessageUtils.success("Party leadership transferred."));
                    target.sendMessage(MessageUtils.info("You are now the party leader."));
                } else {
                    player.sendMessage(MessageUtils.error("Failed to transfer leadership."));
                }
                break;

            case "disband":
                if (!partyManager.isInAnyParty(playerUUID)) {
                    player.sendMessage(MessageUtils.error("You are not in a party."));
                    return true;
                }
                party = partyManager.getParty(playerUUID);
                if (!party.getLeader().equals(playerUUID)) {
                    player.sendMessage(MessageUtils.error("Only the party leader can disband the party."));
                    return true;
                }
                partyManager.disbandParty(party);
                player.sendMessage(MessageUtils.success("Party disbanded."));
                break;

            case "chat":
                if (!partyManager.isInAnyParty(playerUUID)) {
                    player.sendMessage(MessageUtils.error("You are not in a party."));
                    return true;
                }
                if (args.length < 2) {
                    player.sendMessage(MessageUtils.error("Usage: /party chat <message>"));
                    return true;
                }
                String message = String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length));
                partyManager.getParty(playerUUID).getMembers().forEach(uuid -> {
                    Player member = Bukkit.getPlayer(uuid);
                    if (member != null && member.isOnline()) {
                        member.sendMessage(Component.text("[Party] ").color(net.kyori.adventure.text.format.NamedTextColor.DARK_BLUE)
                                .append(Component.text(player.getName() + ": ", net.kyori.adventure.text.format.NamedTextColor.BLUE))
                                .append(Component.text(message)));
                    }
                });
                break;

            default:
                player.sendMessage(MessageUtils.error("Unknown subcommand."));
                break;
        }
        return true;
    }
}
