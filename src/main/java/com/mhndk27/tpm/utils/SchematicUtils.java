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
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;

import org.bukkit.Location;

import java.io.File;
import java.io.FileInputStream;

public class SchematicUtils {

    public static void pasteSchematic(File schematicFile, Location loc) {
        try {
            var format = ClipboardFormats.findByFile(schematicFile);
            if (format == null) {
                System.err.println("Unknown schematic format for file: " + schematicFile.getName());
                return;
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

    public static void clearSchematicArea(Location origin, int width, int height, int length) {
        try {
            World weWorld = BukkitAdapter.adapt(origin.getWorld());

            BlockVector3 min = BlockVector3.at(origin.getBlockX(), origin.getBlockY(), origin.getBlockZ());
            BlockVector3 max = BlockVector3.at(
                origin.getBlockX() + width - 1,
                origin.getBlockY() + height - 1,
                origin.getBlockZ() + length - 1
            );

            CuboidRegion region = new CuboidRegion(weWorld, min, max);

            try (EditSession editSession = WorldEdit.getInstance().newEditSession(weWorld)) {
                BlockType airType = BlockTypes.AIR;
                if (airType != null) {
                    BlockState air = airType.getDefaultState();
                    editSession.setBlocks(region, air);
                } else {
                    System.err.println("BlockTypes.AIR is null!");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void clearSchematicArea(Location origin) {
        int width = 38;
        int height = 19;
        int length = 37;
        clearSchematicArea(origin, width, height, length);
    }
}
