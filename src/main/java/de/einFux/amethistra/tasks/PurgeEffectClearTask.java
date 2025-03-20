package de.einFux.amethistra.tasks;

import de.einFux.amethistra.PurgeManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class PurgeEffectClearTask extends BukkitRunnable {

    @Override
    public void run() {
        // Überprüfe, ob die Purge Night aktiv ist
        if (!PurgeManager.isPurgeActive()) {
            // Wenn keine Purge Night aktiv ist, entferne den Glowing-Effekt für alle Spieler
            for (Player player : Bukkit.getOnlinePlayers()) {
                PurgeManager.removeGlowing(player);
            }
        }
    }
}