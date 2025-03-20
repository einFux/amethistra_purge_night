package de.einFux.amethistra.listeners;

import de.einFux.amethistra.Main;
import de.einFux.amethistra.commands.RocketToggleCommand;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class RocketListener implements Listener {

    @EventHandler
    public void onRocketUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item != null && item.getType() == Material.FIREWORK_ROCKET) {
            if (!RocketToggleCommand.areRocketsEnabled()) {
                event.setCancelled(true);
                String rocketDisabledMessage = Main.getInstance().getConfig().getString("messages.rocket_disabled_action_bar", "§cRaketen sind während der Purge Night ausgeschaltet.");
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(rocketDisabledMessage));

            }
        }
    }
}
