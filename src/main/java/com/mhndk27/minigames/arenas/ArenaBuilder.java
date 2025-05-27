package com.mhndk27.minigames.arenas;

import com.mhndk27.minigames.MiniGamesPlugin;
import com.mhndk27.minigames.utils.SchematicUtils;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import org.bukkit.Location;

public class ArenaBuilder {

    private final MiniGamesPlugin plugin;

    public ArenaBuilder(MiniGamesPlugin plugin) {
        this.plugin = plugin;
    }

    public void buildWaitingRoom(Location loc) {
        Clipboard clipboard = SchematicUtils.loadSchematic(plugin, "schematics/zswaitroom.schem");
        if (clipboard == null) {
            System.out.println("❌ Failed to load schematic: zswaitroom.schem");
            return;
        }

        boolean result = SchematicUtils.pasteSchematic(plugin, clipboard, loc, true);
        if (!result) {
            System.out.println("❌ Failed to paste schematic at location: " + loc);
        }
    }
}
