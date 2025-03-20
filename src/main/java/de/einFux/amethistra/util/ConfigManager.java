package de.einFux.amethistra.util;

import de.einFux.amethistra.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigManager {
    private static File configFile;
    private static FileConfiguration config;

    public static void setup() {
        configFile = new File(Main.getInstance().getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            Main.getInstance().saveResource("config.yml", false);
        }
        reloadConfig();
    }

    public static FileConfiguration getConfig() {
        if (config == null) {
            reloadConfig();
        }
        return config;
    }

    public static void saveConfig() {
        if (config == null || configFile == null) {
            return;
        }
        try {
            config.save(configFile);
        } catch (IOException e) {
            System.err.println("[Amethistra] Fehler beim Speichern der Konfiguration!");
            e.printStackTrace();
        }
    }

    public static void reloadConfig() {
        if (configFile == null) {
            configFile = new File(Main.getInstance().getDataFolder(), "config.yml");
        }
        config = YamlConfiguration.loadConfiguration(configFile);
    }
}