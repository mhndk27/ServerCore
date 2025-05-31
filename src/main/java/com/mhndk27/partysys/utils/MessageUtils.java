package com.mhndk27.partysys.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class MessageUtils {

    // Party prefix with a vibrant color
    public static Component prefix() {
        return Component.text("⭑ Party ⭑ ", NamedTextColor.AQUA, TextDecoration.BOLD);
    }

    public static Component success(String msg) {
        return prefix().append(Component.text(msg, NamedTextColor.GREEN));
    }

    public static Component error(String msg) {
        return prefix().append(Component.text(msg, NamedTextColor.RED));
    }

    public static Component info(String msg) {
        return prefix().append(Component.text(msg, NamedTextColor.WHITE));
    }

    // Usage/help message with colored command
    public static Component usage(String command) {
        return prefix()
                .append(Component.text("Type ", NamedTextColor.WHITE))
                .append(Component.text(command, NamedTextColor.GREEN, TextDecoration.BOLD))
                .append(Component.text(" to use the command.", NamedTextColor.WHITE));
    }

    // Invite message with interactive [accept] [deny] buttons
    public static Component inviteMessage(String inviterName) {
        TextComponent acceptBtn = Component.text("[Accept]", NamedTextColor.GREEN, TextDecoration.BOLD)
                .clickEvent(ClickEvent.runCommand("/party accept"))
                .hoverEvent(HoverEvent.showText(Component.text("Click to accept the invite", NamedTextColor.GREEN)));

        TextComponent denyBtn = Component.text("[Deny]", NamedTextColor.RED, TextDecoration.BOLD)
                .clickEvent(ClickEvent.runCommand("/party deny"))
                .hoverEvent(HoverEvent.showText(Component.text("Click to deny the invite", NamedTextColor.RED)));

        return prefix()
                .append(Component.text(inviterName, NamedTextColor.GOLD, TextDecoration.BOLD))
                .append(Component.text(" invited you to join their party! ", NamedTextColor.YELLOW))
                .append(acceptBtn)
                .append(Component.text(" "))
                .append(denyBtn);
    }

    // Party chat message with clear colors
    public static Component partyChat(String playerName, String message) {
        return prefix()
                .append(Component.text(playerName, NamedTextColor.LIGHT_PURPLE, TextDecoration.BOLD))
                .append(Component.text(": ", NamedTextColor.WHITE))
                .append(Component.text(message, NamedTextColor.YELLOW));
    }
}
