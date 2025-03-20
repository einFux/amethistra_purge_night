package de.einFux.amethistra.listeners;

import de.einFux.amethistra.Main;
import de.einFux.amethistra.PurgeManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PurgeListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!PurgeManager.isPurgeActive()) {
            // Wenn keine Purge aktiv ist, entferne den Glowing-Effekt
            PurgeManager.removeGlowing(player);
        } else {
            // Wenn die Purge aktiv ist, wende den Glowing-Effekt an
            player.sendTitle("§4Es ist Purge Night!", "§4 Achtung! Du kannst von jedem getötet werden", 10, 70, 20);
            player.sendMessage("§4Weitere Informationen zu diesem Event findest du auf discord.gg/amethystra");
            applyGlowing(player);
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (PurgeManager.isPurgeActive()) {
            Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> applyGlowing(player), 2L);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (PurgeManager.isPurgeActive()) {
            // Handle player quit during purge if needed
        }
    }

    @EventHandler
    public void onPotionEffectExpire(EntityPotionEffectEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();

        if (PurgeManager.isPurgeActive() && event.getOldEffect() != null &&
                event.getOldEffect().getType().equals(PotionEffectType.GLOWING)) {
            Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                if (!player.hasPotionEffect(PotionEffectType.GLOWING)) {
                    applyGlowing(player);
                }
            }, 2L);
        }
    }

    @EventHandler
    public void onPlayerConsumeMilk(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        if (PurgeManager.isPurgeActive() && event.getItem().getType() == Material.MILK_BUCKET) {
            Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> applyGlowing(player), 2L);
        }
    }

    @EventHandler
    public void onTotemUse(EntityResurrectEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();

        if (PurgeManager.isPurgeActive()) {
            Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> applyGlowing(player), 2L);
        }
    }

    @EventHandler
    public void onPlayerSleep(PlayerBedEnterEvent event) {
        if (PurgeManager.isPurgeActive()) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("§cDu kannst während der Purge nicht schlafen!");
        }
    }

    private void applyGlowing(Player player) {
        if (!player.hasPotionEffect(PotionEffectType.GLOWING)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 200, 0, false, false)); // 10 Sekunden (200 Ticks)
        }
    }
}