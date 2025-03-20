package de.einFux.amethistra;

import de.einFux.amethistra.commands.PurgeCommand;
import de.einFux.amethistra.commands.PurgeTabCompleter;
import de.einFux.amethistra.commands.RocketToggleCommand;
import de.einFux.amethistra.listeners.PurgeListener;
import de.einFux.amethistra.listeners.RocketListener;
import de.einFux.amethistra.tasks.PurgeEffectClearTask;
import org.bukkit.plugin.java.JavaPlugin;
import de.einFux.amethistra.util.ConfigManager;

public class Main extends JavaPlugin {
    private static Main instance;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        // Lade die Config korrekt
        ConfigManager.setup();

        // Lade Commands
        getCommand("rockettoggle").setExecutor(new RocketToggleCommand());
        getCommand("purge").setExecutor(new PurgeCommand());
        getCommand("purge").setTabCompleter(new PurgeTabCompleter());

        // Lade Event Listener
        getServer().getPluginManager().registerEvents(new RocketListener(), this);
        getServer().getPluginManager().registerEvents(new PurgeListener(), this);

        getLogger().info("Amethistra Plugin erfolgreich aktiviert!");
    }

    @Override
    public void onDisable() {
        if (PurgeManager.isPurgeActive()) {
            PurgeManager.stopPurge();
        }
        getLogger().info("Amethistra Plugin deaktiviert!");
    }

    public static Main getInstance() {
        return instance;
    }
}