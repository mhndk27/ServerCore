package com.mhndk27.minigames.utils;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.world.World;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;

public class SchematicUtils {

    public static Clipboard loadSchematic(JavaPlugin plugin, String filePath) {
        File file = new File(plugin.getDataFolder(), filePath);
        if (!file.exists()) {
            plugin.getLogger().log(Level.SEVERE, "Schematic file not found: " + file.getAbsolutePath());
            return null;
        }

        ClipboardFormat format = ClipboardFormats.findByFile(file);
        if (format == null) {
            plugin.getLogger().log(Level.SEVERE, "Unsupported schematic format: " + file.getName());
            return null;
        }

        try (FileInputStream fis = new FileInputStream(file);
             ClipboardReader reader = format.getReader(fis)) {

            return reader.read();
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to load schematic: " + e.getMessage(), e);
            return null;
        }
    }

    public static boolean pasteSchematic(JavaPlugin plugin, Clipboard clipboard, Location location, boolean ignoreAir) {
        if (clipboard == null) {
            plugin.getLogger().log(Level.WARNING, "Clipboard is null, cannot paste schematic");
            return false;
        }

        World weWorld = BukkitAdapter.adapt(location.getWorld());
        BlockVector3 pasteLocation = BlockVector3.at(location.getX(), location.getY(), location.getZ());

        try (EditSession editSession = WorldEdit.getInstance().newEditSession(weWorld)) {
            ClipboardHolder holder = new ClipboardHolder(clipboard);
            Operation operation = holder
                    .createPaste(editSession)
                    .to(pasteLocation)
                    .ignoreAirBlocks(ignoreAir)
                    .build();

            Operations.complete(operation);
            return true;
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to paste schematic: " + e.getMessage(), e);
            return false;
        }
    }
}
