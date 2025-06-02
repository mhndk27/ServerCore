package com.mhndk27.core.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class MessageUtils {

    // شعار عام: أزرق غامق + أزرق فاتح + عريض
    public static Component prefix(String label) {
        return Component.text("⭑ ", NamedTextColor.DARK_AQUA, TextDecoration.BOLD)
                .append(Component.text(label, NamedTextColor.AQUA, TextDecoration.BOLD))
                .append(Component.text(" ⭑ ", NamedTextColor.DARK_AQUA, TextDecoration.BOLD));
    }

    // رسائل النجاح/الملاحظات: أخضر مزرق + أبيض
    public static Component success(String msg) {
        return prefix("SUCCESS").append(Component.text(msg, NamedTextColor.GREEN))
                .append(Component.text("", NamedTextColor.WHITE));
    }

    // رسائل الخطأ/المشاكل: أحمر عريض + أبيض
    public static Component error(String msg) {
        return prefix("ERROR").append(Component.text(msg, NamedTextColor.RED, TextDecoration.BOLD));
    }

    // رسائل المعلومات: أبيض عادي
    public static Component info(String msg) {
        return prefix("INFO").append(Component.text(msg, NamedTextColor.WHITE));
    }

    // رسائل تنبيه أو تعليمات: أصفر + أخضر عريض للأوامر
    public static Component usage(String command) {
        return prefix("USAGE").append(Component.text("Type ", NamedTextColor.YELLOW))
                .append(Component.text(command, NamedTextColor.GREEN, TextDecoration.BOLD))
                .append(Component.text(" to use the command.", NamedTextColor.YELLOW));
    }

    // رسالة دعوة: اسم اللاعب برتقالي عريض، نص أصفر، زر قبول أخضر عريض، زر رفض أحمر عريض
    public static Component inviteMessage(String inviterName) {
        TextComponent acceptBtn = Component
                .text("[Accept]", NamedTextColor.GREEN, TextDecoration.BOLD)
                .clickEvent(ClickEvent.runCommand("/party accept")).hoverEvent(HoverEvent.showText(
                        Component.text("Click to accept the invite", NamedTextColor.GREEN)));

        TextComponent denyBtn = Component.text("[Deny]", NamedTextColor.RED, TextDecoration.BOLD)
                .clickEvent(ClickEvent.runCommand("/party deny")).hoverEvent(HoverEvent
                        .showText(Component.text("Click to deny the invite", NamedTextColor.RED)));

        return prefix("INVITE")
                .append(Component.text(inviterName, NamedTextColor.GOLD, TextDecoration.BOLD))
                .append(Component.text(" invited you to join their party! ", NamedTextColor.YELLOW))
                .append(acceptBtn).append(Component.text(" ")).append(denyBtn);
    }

    // دردشة عامة: اسم اللاعب بنفسجي عريض، النقطتان أزرق غامق، الرسالة أصفر وأبيض متداخل
    public static Component chatMessage(String playerName, String message) {
        int firstSpace = message.indexOf(' ');
        String first = firstSpace > 0 ? message.substring(0, firstSpace) : message;
        String rest = firstSpace > 0 ? message.substring(firstSpace) : "";

        return prefix("CHAT")
                .append(Component.text(playerName, NamedTextColor.LIGHT_PURPLE,
                        TextDecoration.BOLD))
                .append(Component.text(": ", NamedTextColor.DARK_AQUA))
                .append(Component.text(first, NamedTextColor.YELLOW))
                .append(Component.text(rest, NamedTextColor.WHITE));
    }

    // زر تفاعلي عام (للاستخدام في بلوقنات أخرى)
    public static TextComponent actionButton(String label, NamedTextColor color, String command,
            String hover) {
        return Component.text(label, color, TextDecoration.BOLD)
                .clickEvent(ClickEvent.runCommand(command))
                .hoverEvent(HoverEvent.showText(Component.text(hover, color)));
    }
}
