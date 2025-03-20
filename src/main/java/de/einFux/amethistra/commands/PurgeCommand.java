package de.einFux.amethistra.commands;

import de.einFux.amethistra.Main;
import de.einFux.amethistra.PurgeManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class PurgeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("stop")) {
            if (!PurgeManager.isPurgeActive()) {
                sender.sendMessage("§4Es läuft aktuell keine Purge!");
                return false;
            }
            PurgeManager.stopPurge();
            sender.sendMessage("§4 Die Purge Night ist beendet.Ab sofort gelten wieder die normalen Regeln.Schau auf discord.gg/amethystra nach,wann die nächste Purge Night stattfindet.");
            return true;
        }

        if (args.length < 1 || !args[0].equalsIgnoreCase("start")) {
            sender.sendMessage("§4Verwendung: /purge start [time] oder /purge stop");
            return false;
        }

        int timeMinutes = -1;
        if (args.length > 1) {
            try {
                timeMinutes = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                sender.sendMessage("§4Ungültige Zeit! Bitte gib eine Zahl in Minuten an.");
                return false;
            }
        }

        if (PurgeManager.isPurgeActive()) {
            sender.sendMessage("§4Die Purge läuft bereits!");
            return false;
        }

        PurgeManager.startPurge(timeMinutes);
        Bukkit.broadcastMessage("§4 Die Purge Night ist ein PvP-Event,bei dem jeder Spieler ab sofort jeden anderen ohne besonderen Grund töten darf.Alle Regeln dazu findest du auf discord.gg/amethystra.");
        return true;
    }
}