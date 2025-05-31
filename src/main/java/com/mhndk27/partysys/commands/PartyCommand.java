package com.mhndk27.partysys.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.mhndk27.partysys.Party;
import com.mhndk27.partysys.PartyManager;
import com.mhndk27.partysys.managers.PartyChatManager;
import com.mhndk27.partysys.managers.PartyInviteManager;
import com.mhndk27.partysys.utils.MessageUtils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class PartyCommand implements CommandExecutor, TabCompleter {

    private final PartyManager partyManager;

    public PartyCommand(PartyManager partyManager) {
        this.partyManager = partyManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can run this command.");
            return true;
        }

        Player player = (Player) sender;
        UUID playerUUID = player.getUniqueId();

        if (args.length == 0) {
            player.sendMessage(
                    MessageUtils.usage("/party <create|invite|accept|deny|leave|kick|promote|disband|chat>"));
            return true;
        }

        String sub = args[0].toLowerCase();

        switch (sub) {
            case "create":
                if (partyManager.isInParty(playerUUID)) {
                    player.sendMessage(MessageUtils.error("You are already in a party."));
                    return true;
                }
                Party newParty = partyManager.createParty(playerUUID);
                if (newParty != null) {
                    player.sendMessage(MessageUtils.success("Party created!"));
                } else {
                    player.sendMessage(MessageUtils.error("Failed to create party."));
                }
                break;

            case "invite":
                if (args.length < 2) {
                    player.sendMessage(MessageUtils.info("Usage: /party invite <player>"));
                    return true;
                }
                if (!partyManager.isInParty(playerUUID)) {
                    player.sendMessage(MessageUtils.error("You are not in a party."));
                    return true;
                }
                Party party = partyManager.getParty(playerUUID);
                if (!party.isLeader(playerUUID)) {
                    player.sendMessage(MessageUtils.error("Only the party leader can invite."));
                    return true;
                }
                if (party.isFull()) {
                    player.sendMessage(MessageUtils.error("Party is full."));
                    return true;
                }

                String targetName = args[1];
                Player target = Bukkit.getPlayerExact(targetName);
                if (target == null) {
                    player.sendMessage(MessageUtils.error("Player not found or offline."));
                    return true;
                }
                if (partyManager.isInParty(target.getUniqueId())) {
                    player.sendMessage(MessageUtils.error("This player is already in a party."));
                    return true;
                }

                PartyInviteManager inviteManager = PartyInviteManager.getInstance();
                inviteManager.addInvite(target.getUniqueId(), playerUUID);
                player.sendMessage(MessageUtils.success("Invite sent to " + targetName + " 🎉"));
                target.sendMessage(MessageUtils.inviteMessage(player.getName()));
                break;

            case "accept":
                PartyInviteManager inviteManagerAccept = PartyInviteManager.getInstance();
                if (!inviteManagerAccept.hasInvite(playerUUID)) {
                    player.sendMessage(MessageUtils.error("You have no pending party invites."));
                    return true;
                }
                boolean accepted = inviteManagerAccept.acceptInvite(playerUUID);
                if (!accepted) {
                    player.sendMessage(MessageUtils.error("Failed to join the party."));
                }
                break;

            case "deny":
                PartyInviteManager inviteManagerDeny = PartyInviteManager.getInstance();
                if (!inviteManagerDeny.hasInvite(playerUUID)) {
                    player.sendMessage(MessageUtils.error("You have no pending party invites."));
                    return true;
                }
                inviteManagerDeny.denyInvite(playerUUID);
                break;

            case "leave":
                if (!partyManager.isInParty(playerUUID)) {
                    player.sendMessage(MessageUtils.error("You are not in a party."));
                    return true;
                }
                partyManager.leaveParty(playerUUID);
                break;

            case "kick":
                if (args.length < 2) {
                    player.sendMessage(MessageUtils.info("Usage: /party kick <player>"));
                    return true;
                }
                if (!partyManager.isInParty(playerUUID)) {
                    player.sendMessage(MessageUtils.error("You are not in a party."));
                    return true;
                }
                Party partyKick = partyManager.getParty(playerUUID);
                if (!partyKick.isLeader(playerUUID)) {
                    player.sendMessage(MessageUtils.error("Only the party leader can kick members."));
                    return true;
                }
                String kickName = args[1];
                UUID kickUUID = partyManager.getUUIDFromName(kickName);
                if (kickUUID == null) {
                    player.sendMessage(MessageUtils.error("Player not found."));
                    return true;
                }
                if (!partyKick.contains(kickUUID)) {
                    player.sendMessage(MessageUtils.error("This player is not in your party."));
                    return true;
                }
                if (kickUUID.equals(playerUUID)) {
                    player.sendMessage(MessageUtils.error("You cannot kick yourself."));
                    return true;
                }
                boolean kicked = partyManager.removeMember(playerUUID, kickUUID);
                if (kicked) {
                    player.sendMessage(MessageUtils.success(kickName + " has been kicked from the party."));
                } else {
                    player.sendMessage(MessageUtils.error("Failed to kick player."));
                }
                break;

            case "promote":
                if (args.length < 2) {
                    player.sendMessage(MessageUtils.info("Usage: /party promote <player>"));
                    return true;
                }
                if (!partyManager.isInParty(playerUUID)) {
                    player.sendMessage(MessageUtils.error("You are not in a party."));
                    return true;
                }
                Party partyPromote = partyManager.getParty(playerUUID);
                if (!partyPromote.isLeader(playerUUID)) {
                    player.sendMessage(MessageUtils.error("Only the party leader can promote members."));
                    return true;
                }
                String promoteName = args[1];
                UUID promoteUUID = partyManager.getUUIDFromName(promoteName);
                if (promoteUUID == null) {
                    player.sendMessage(MessageUtils.error("Player not found."));
                    return true;
                }
                if (!partyPromote.contains(promoteUUID)) {
                    player.sendMessage(MessageUtils.error("This player is not in your party."));
                    return true;
                }
                if (promoteUUID.equals(playerUUID)) {
                    player.sendMessage(MessageUtils.error("You are already the leader."));
                    return true;
                }
                boolean promoted = partyManager.transferLeadership(playerUUID, promoteUUID);
                if (promoted) {
                    player.sendMessage(MessageUtils.success(promoteName + " is now the party leader."));
                } else {
                    player.sendMessage(MessageUtils.error("Failed to promote player."));
                }
                break;

            case "disband":
                if (!partyManager.isInParty(playerUUID)) {
                    player.sendMessage(MessageUtils.error("You are not in a party."));
                    return true;
                }
                Party disbandParty = partyManager.getParty(playerUUID);
                if (!disbandParty.isLeader(playerUUID)) {
                    player.sendMessage(MessageUtils.error("Only the party leader can disband the party."));
                    return true;
                }
                partyManager.removeParty(disbandParty);
                player.sendMessage(MessageUtils.success("Party disbanded and all members teleported to the lobby."));
                break;

            case "chat":
                boolean enabled = PartyChatManager.getInstance().togglePartyChat(playerUUID);
                player.sendMessage(enabled ? MessageUtils.success("Party chat enabled.")
                        : MessageUtils.info("Party chat disabled."));
                break;

            default:
                player.sendMessage(MessageUtils.info("Unknown subcommand. Use ")
                        .append(Component.text("/party help", NamedTextColor.GREEN, TextDecoration.BOLD))
                        .append(Component.text(" to see available commands.", NamedTextColor.WHITE)));
                break;
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player))
            return Collections.emptyList();

        Player player = (Player) sender;
        UUID playerUUID = player.getUniqueId();

        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            List<String> subs = List.of("create", "invite", "accept", "deny", "leave", "kick", "promote", "disband",
                    "chat");
            for (String sub : subs) {
                if (sub.startsWith(args[0].toLowerCase())) {
                    completions.add(sub);
                }
            }
            return completions;
        }

        String sub = args[0].toLowerCase();

        if (args.length == 2) {
            switch (sub) {
                case "invite":
                    // إكمال تلقائي بلايرز أونلاين غير في بارتي
                    for (Player online : Bukkit.getOnlinePlayers()) {
                        if (!partyManager.isInParty(online.getUniqueId())
                                && online.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
                            completions.add(online.getName());
                        }
                    }
                    break;

                case "kick":
                case "promote":
                    // إكمال تلقائي بلايرز في نفس البارتي (باستثناء اللاعب نفسه)
                    Party party = partyManager.getParty(playerUUID);
                    if (party != null) {
                        for (UUID memberUUID : party.getMembers()) {
                            String name = partyManager.getPlayerName(memberUUID);
                            if (name != null && !memberUUID.equals(playerUUID)
                                    && name.toLowerCase().startsWith(args[1].toLowerCase())) {
                                completions.add(name);
                            }
                        }
                    }
                    break;

                default:
                    break;
            }
        }

        return completions;
    }
}
