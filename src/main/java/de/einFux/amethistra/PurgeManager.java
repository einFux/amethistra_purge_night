package de.einFux.amethistra;

import de.einFux.amethistra.commands.RocketToggleCommand;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PurgeManager {
    private static boolean purgeActive = false;
    private static BukkitRunnable purgeTask;
    private static BukkitRunnable glowingRenewTask;
    private static BukkitRunnable glowingStartTask;
    private static long purgeEndTime;
    private static final Set<UUID> glowingPlayers = new HashSet<>();
    private static boolean infinitePurge = false;

    public static boolean isPurgeActive() {
        return purgeActive;
    }

    public static void startPurge(int durationMinutes) {
        if (purgeActive) {
            return;
        }

        purgeActive = true;
        if (durationMinutes > 0) {
            purgeEndTime = System.currentTimeMillis() + (durationMinutes * 60 * 1000);
            infinitePurge = false;
        } else {
            infinitePurge = true;
        }

        World world = Bukkit.getWorlds().get(0);
        world.setTime(18000);
        world.setGameRuleValue("doDaylightCycle", "false");
        world.setGameRuleValue("doWeatherCycle", "false");
        world.setGameRuleValue("doMobSpawning", "false");
        world.setStorm(true);

        RocketToggleCommand.setRocketsEnabled(false);

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendTitle("§4Purge Night beginnt", "§4Du kannst jetzt von jedem getötet werden", 10, 100, 10);
            player.playSound(player.getLocation(), Sound.ENTITY_WITHER_DEATH, 1.0f, 1.0f);
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 200, 1));
            player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 200, 4));
        }

        // Verzögerung für das Glowing, startet nach 10 Sekunden (200 Ticks)
        glowingStartTask = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    applyGlowing(player);
                }
            }
        };
        glowingStartTask.runTaskLater(Main.getInstance(), 200L);

        glowingRenewTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (purgeActive) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        applyGlowing(player);
                    }
                } else {
                    cancel();
                }
            }
        };
        glowingRenewTask.runTaskTimer(Main.getInstance(), 295L, 95L);

        purgeTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (infinitePurge) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.sendActionBar("§4Purge Night aktiv!");
                    }
                } else {
                    long remainingTime = purgeEndTime - System.currentTimeMillis();
                    if (remainingTime <= 0) {
                        stopPurge();
                        cancel();
                        return;
                    }
                    String timeLeft = formatTime(remainingTime);
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.sendActionBar("§4Purge Time Left: " + timeLeft);
                    }
                }
            }
        };
        purgeTask.runTaskTimer(Main.getInstance(), 0L, 20L);
    }

    public static void stopPurge() {
        if (!purgeActive) {
            return;
        }
        purgeActive = false;
        infinitePurge = false;

        World world = Bukkit.getWorlds().get(0);
        world.setStorm(false);
        world.setThundering(false);
        world.setTime(1000);
        world.setGameRuleValue("doDaylightCycle", "true");
        world.setGameRuleValue("doWeatherCycle", "true");
        world.setGameRuleValue("doMobSpawning", "true");

        RocketToggleCommand.setRocketsEnabled(true);

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendTitle("§4Purge Night ist vorbei", "§4Der Frieden kehrt in die Welt zurück.", 10, 100, 10);
            player.playSound(player.getLocation(), Sound.ENTITY_WITHER_DEATH, 1.0f, 1.0f);
            removeGlowing(player);
            player.removePotionEffect(PotionEffectType.BLINDNESS);
            player.removePotionEffect(PotionEffectType.RESISTANCE);
        }

        glowingPlayers.clear();

        if (purgeTask != null) {
            purgeTask.cancel();
        }
        if (glowingRenewTask != null) {
            glowingRenewTask.cancel();
        }
        if (glowingStartTask != null) {
            glowingStartTask.cancel();
        }
    }

    public static void applyGlowing(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 200, 0, false, false));
        glowingPlayers.add(player.getUniqueId());
    }

    public static void removeGlowing(Player player) {
        if (glowingPlayers.contains(player.getUniqueId())) {
            player.removePotionEffect(PotionEffectType.GLOWING);
            glowingPlayers.remove(player.getUniqueId());
        }
    }

    private static String formatTime(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        return String.format("%02d:%02d:%02d", hours, minutes % 60, seconds % 60);
    }
}
