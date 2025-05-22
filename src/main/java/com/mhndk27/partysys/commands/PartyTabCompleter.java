package com.mhndk27.partysys.commands;

import com.mhndk27.partysys.Party;
import com.mhndk27.partysys.PartyManager;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class PartyTabCompleter implements TabCompleter {

    private final PartyManager partyManager;
    private final List<String> subcommands = Arrays.asList(
        "create", "invite", "kick", "leave", "promote", "disband", "chat", "accept", "deny"
    );

    public PartyTabCompleter(PartyManager partyManager) {
        this.partyManager = partyManager;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player player)) {
            return Collections.emptyList();
        }

        if (args.length == 1) {
            return subcommands.stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (args.length == 2) {
            String subcommand = args[0].toLowerCase();
            UUID playerUUID = player.getUniqueId();

            if (!partyManager.isInParty(playerUUID)) return Collections.emptyList();

            Party party = partyManager.getParty(playerUUID);
            if (party == null) return Collections.emptyList();

            switch (subcommand) {
                case "invite" -> {
                    // لاعبين أونلاين مش في بارتي
                    return Bukkit.getOnlinePlayers().stream()
                            .filter(p -> !partyManager.isInParty(p.getUniqueId()))
                            .map(Player::getName)
                            .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                            .toList();
                }
                case "kick", "promote" -> {
                    // أسماء أعضاء الفريق مع استثناء القائد من قائمة kick
                    return party.getMembers().stream()
                            .map(pUUID -> partyManager.getPlayerName(pUUID))
                            .filter(Objects::nonNull)
                            .filter(name -> {
                                if (subcommand.equals("kick")) {
                                    UUID uuid = partyManager.getUUIDFromName(name);
                                    return uuid != null && !party.isLeader(uuid);
                                }
                                return true; // للترقية نعرض الكل
                            })
                            .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                            .toList();
                }
                default -> {
                    return Collections.emptyList();
                }
            }
        }

        return Collections.emptyList();
    }
}
