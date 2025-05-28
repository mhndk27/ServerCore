package com.mhndk27.tpm.managers;

import org.bukkit.Location;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class NPCFileManager {

    private static final String DATA_FOLDER = "plugins/ZNPCsPlus/data/";

    public static void createReturnNPCFile(String playerName, int roomId, Location location) {
        String fileName = DATA_FOLDER + roomId + ".yml";
        File file = new File(fileName);

        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            FileWriter writer = new FileWriter(file);

            String content = "Name: Return to Lobby\n" +
                    "Location:\n" +
                    "  World: " + location.getWorld().getName() + "\n" +
                    "  X: " + location.getX() + "\n" +
                    "  Y: " + location.getY() + "\n" +
                    "  Z: " + location.getZ() + "\n" +
                    "Commands:\n" +
                    "  - 'lobbytp " + playerName + "'\n";

            writer.write(content);
            writer.flush();
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteNPCFile(int roomId) {
        File file = new File(DATA_FOLDER + roomId + ".yml");
        if (file.exists()) {
            file.delete();
        }
    }
}
