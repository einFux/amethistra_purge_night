package de.einFux.amethistra.tasks;

import de.einFux.amethistra.Main;
import de.einFux.amethistra.PurgeManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class PurgeTimerTask extends BukkitRunnable {
    private int remainingSeconds;
    private final World world;
    private int weatherCycleCounter = 0;
    private boolean isStormActive = true;
    private static PurgeTimerTask activeTask;
    private final boolean infinitePurge;

    public PurgeTimerTask(int timeSeconds) {
        this.remainingSeconds = timeSeconds;
        this.world = Bukkit.getWorlds().get(0);
        this.infinitePurge = (timeSeconds == -1);

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 0, false, false));
        }

        // Setze direkt Sturm, wenn Purge startet
        world.setStorm(true);
        world.setGameRuleValue("doWeatherCycle", "false"); // Wetterzyklus deaktivieren
        isStormActive = true;

        activeTask = this;
    }

    @Override
    public void run() {
        if (!infinitePurge && remainingSeconds <= 0) {
            stopPurge();
            cancel();
            return;
        }

        String infinitePurgeMessage = Main.getInstance().getConfig().getString("messages.purge_infinite_active", "§4Purge ist aktiv!");
        String message = infinitePurge ? infinitePurgeMessage : formatTime(remainingSeconds);
        for (Player player : Bukkit.getOnlinePlayers()) {
            String actionBarMessage = Main.getInstance().getConfig().getString("messages.purge_time_left", "§4Purge Night endet in: ");
            player.sendActionBar(actionBarMessage + formatTime(remainingSeconds));
        }

        // Stelle sicher, dass die Zeit auf Nacht bleibt
        world.setTime(18000);

        // Wetter-Zyklus: Alle 24.000 Ticks (20 Min) zwischen Sturm und Klar wechseln
        if (weatherCycleCounter >= 24000) {
            isStormActive = !isStormActive;
            world.setStorm(isStormActive);
            weatherCycleCounter = 0;
        }
        weatherCycleCounter += 20;

        if (!infinitePurge) {
            remainingSeconds--;
        }
    }

    private String formatTime(int seconds) {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public static void stopActivePurge() {
        if (activeTask != null) {
            activeTask.stopPurge();
            activeTask.cancel();
            activeTask = null;
        }
    }

    public void stopPurge() {
        PurgeManager.stopPurge();

        // Stelle sicher, dass das Wetter nach der Purge auf klar ist
        world.setStorm(false);
        world.setThundering(false);

        // Aktiviere den normalen Wetterzyklus wieder
        world.setGameRuleValue("doWeatherCycle", "true");
    }
}
