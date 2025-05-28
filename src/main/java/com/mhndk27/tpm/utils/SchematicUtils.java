package com.mhndk27.tpm.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class SchematicUtils {

    public static Location pasteSchematic(String schematicName, int roomId) {
        World world = Bukkit.getWorld("world");
        double baseX = 500 * roomId;
        double baseY = 100;
        double baseZ = 100;
        return new Location(world, baseX, baseY, baseZ);
    }
}
