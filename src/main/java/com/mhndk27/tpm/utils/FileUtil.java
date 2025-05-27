package com.mhndk27.tpm.utils;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class FileUtil {

    private final JavaPlugin plugin;

    public FileUtil(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Gets an InputStream for a resource file inside the plugin jar/resources folder.
     * Useful for loading schematics or config files bundled with the plugin.
     *
     * @param resourcePath relative path inside the jar (e.g., "schematics/zswaitroom.schem")
     * @return InputStream of the resource or null if not found
     */
    public InputStream getResourceStream(String resourcePath) {
        return plugin.getResource(resourcePath);
    }

    /**
     * Copies a resource file from plugin jar to plugin data folder if it doesn't exist.
     * Useful to extract default schematic or config files.
     *
     * @param resourcePath relative path inside the jar
     * @throws IOException if copying fails
     */
    public void saveResourceToDataFolder(String resourcePath) throws IOException {
        File outFile = new File(plugin.getDataFolder(), resourcePath);
        if (!outFile.exists()) {
            // Create parent directories if not exist
            outFile.getParentFile().mkdirs();

            try (InputStream in = plugin.getResource(resourcePath)) {
                if (in == null) {
                    throw new IOException("Resource " + resourcePath + " not found in plugin jar.");
                }
                Files.copy(in, outFile.toPath());
            }
        }
    }

    /**
     * Get a file handle inside the plugin data folder.
     *
     * @param relativePath relative path inside plugin data folder
     * @return File object
     */
    public File getFile(String relativePath) {
        return new File(plugin.getDataFolder(), relativePath);
    }
}
