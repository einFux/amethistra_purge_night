package de.einFux.amethistra.commands;

import de.einFux.amethistra.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RocketToggleCommand implements CommandExecutor {
    private static boolean rocketsEnabled = Main.getInstance().getConfig().getBoolean("defaults.rockets-enabled");

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) return false;

        rocketsEnabled = !rocketsEnabled;
        Main.getInstance().getConfig().set("defaults.rockets-enabled", rocketsEnabled);
        Main.getInstance().saveConfig();

        String rocketMessage = Main.getInstance().getConfig().getString(rocketsEnabled ? "messages.rocket_enabled" : "messages.rocket_disabled", rocketsEnabled ? "§aRocket boosting is enabled!" : "§cRocket boosting is disabled!");
        sender.sendMessage(rocketMessage);
        return true;
    }

    public static boolean areRocketsEnabled() {
        return rocketsEnabled;
    }
    public static void setRocketsEnabled(boolean enabled) {
        rocketsEnabled = enabled;
    }

}
