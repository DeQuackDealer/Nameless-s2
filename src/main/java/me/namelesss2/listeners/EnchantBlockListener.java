package me.namelesss2.listeners;

import me.namelesss2.commands.EnchantBlockCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.inventory.ItemStack;

public class EnchantBlockListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPrepareEnchant(PrepareItemEnchantEvent event) {
        ItemStack item = event.getItem();
        if (EnchantBlockCommand.isEnchantBlocked(item)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEnchant(EnchantItemEvent event) {
        ItemStack item = event.getItem();
        if (EnchantBlockCommand.isEnchantBlocked(item)) {
            event.setCancelled(true);
            Player player = event.getEnchanter();
            player.sendMessage(Component.text("This item cannot be enchanted in an enchanting table!")
                    .color(NamedTextColor.RED));
        }
    }
}
