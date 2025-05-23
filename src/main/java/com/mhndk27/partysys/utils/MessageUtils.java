package com.mhndk27.partysys.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class MessageUtils {

    public static Component prefix() {
        return Component.text("[Party] ", NamedTextColor.DARK_AQUA);
    }

    public static Component success(String msg) {
        return prefix().append(Component.text(msg, NamedTextColor.GREEN));
    }

    public static Component error(String msg) {
        return prefix().append(Component.text(msg, NamedTextColor.RED));
    }

    public static Component info(String msg) {
        return prefix().append(Component.text(msg, NamedTextColor.YELLOW));
    }

    // رسالة دردشة خاصة بالبارتي بألوان واضحة
    public static Component partyChat(String playerName, String message) {
        return prefix()
                .append(Component.text(playerName, NamedTextColor.BLUE))
                .append(Component.text(": ", NamedTextColor.WHITE))
                .append(Component.text(message, NamedTextColor.WHITE));
    }
}
