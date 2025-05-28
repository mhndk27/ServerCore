package com.mhndk27.tpm.managers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import org.bukkit.Location;

public class NPCFileManager {
    private final File dataFolder = new File("plugins/ZNPCsPlus/data");
    private final AtomicInteger npcCounter = new AtomicInteger(2); // يبدأ من 2.yml

    public void createNPCFile(Location location) {
        int npcId = npcCounter.getAndIncrement();
        File npcFile = new File(dataFolder, npcId + ".yml");

        try (FileWriter writer = new FileWriter(npcFile)) {
            writer.write("name: Lobby\n");
            writer.write("location:\n");
            writer.write("  world: " + location.getWorld().getName() + "\n");
            writer.write("  x: " + (location.getBlockX() + 2) + "\n");
            writer.write("  y: " + location.getBlockY() + "\n");
            writer.write("  z: " + (location.getBlockZ() + 2) + "\n");
            writer.write("commands:\n");
            writer.write("  - 'console: lobbytp %player%'\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteNPCFile(Location roomLocation) {
        // ابحث عن NPC داخل الغرفة واحذفه يدوياً (تبسيط مؤقت)
    }
}
