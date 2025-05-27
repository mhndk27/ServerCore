package com.mhndk27.tpm.utils;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BlockVector3;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.EditSessionFactory;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.IOException;
import java.io.InputStream;

public class WorldEditUtil {

    public static void pasteSchematic(org.bukkit.World bukkitWorld, Location loc, InputStream schematicStream) throws IOException {
        World world = BukkitAdapter.adapt(bukkitWorld);

        ClipboardReader reader = ClipboardFormats.findByFileName("zswaitroom.schem").getReader(schematicStream);
        try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(world, -1)) {
            ClipboardHolder clipboard = new ClipboardHolder(reader.read());
            Operation operation = clipboard.createPaste(editSession)
                    .to(BlockVector3.at(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()))
                    .ignoreAirBlocks(false)
                    .build();
            Operations.complete(operation);
            editSession.flushQueue();
        }
    }
}
