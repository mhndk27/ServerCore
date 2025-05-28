package com.mhndk27.tpm.utils;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.EditSession;

import org.bukkit.Location;

import java.io.File;
import java.io.FileInputStream;

public class SchematicUtils {
    public static void pasteSchematic(File schematicFile, Location loc) {
        try {
            var format = ClipboardFormats.findByFile(schematicFile);
            if (format == null) {
                System.err.println("Unknown schematic format for file: " + schematicFile.getName());
                return; // توقف التنفيذ هنا
            }
            
            try (ClipboardReader reader = format.getReader(new FileInputStream(schematicFile))) {
                Clipboard clipboard = reader.read();
                World weWorld = BukkitAdapter.adapt(loc.getWorld());

                try (EditSession editSession = WorldEdit.getInstance().newEditSession(weWorld)) {
                    BlockVector3 pasteLocation = BlockVector3.at(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
                    ClipboardHolder holder = new ClipboardHolder(clipboard);

                    Operation operation = holder.createPaste(editSession)
                            .to(pasteLocation)
                            .ignoreAirBlocks(false)
                            .build();

                    Operations.complete(operation);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
