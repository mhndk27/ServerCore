package com.mhndk27.partysys.listeners;

import com.mhndk27.partysys.PartyManager;
import com.mhndk27.partysys.chat.PartyChatManager;
import com.mhndk27.partysys.Party;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.UUID;

public class PartyChatListener implements Listener {

    private final PartyManager partyManager;
    private final PartyChatManager chatManager;

    public PartyChatListener(PartyManager partyManager, PartyChatManager chatManager) {
        this.partyManager = partyManager;
        this.chatManager = chatManager;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (!chatManager.isChatEnabled(uuid)) return; // اللاعب ما فعّل شات البارتي

        event.setCancelled(true); // نلغي الرسالة العامة

        Party party = partyManager.getParty(uuid);
        if (party == null) {
            chatManager.disableChat(uuid); // خرج من البارتي، نطفي وضع الشات
            player.sendMessage(ChatColor.RED + "You are no longer in a party. Party chat disabled.");
            return;
        }

        String formatted = ChatColor.DARK_BLUE + "[Party] " + ChatColor.RESET + player.getName() + ": " + event.getMessage();
        for (UUID memberUUID : party.getMembers()) {
            Player member = Bukkit.getPlayer(memberUUID);
            if (member != null) {
                member.sendMessage(formatted);
            }
        }
    }
}
