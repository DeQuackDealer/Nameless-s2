package me.namelesss2.listeners;

import me.namelesss2.items.SwapRod;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class SwapRodListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerFish(PlayerFishEvent event) {
        Player player = event.getPlayer();
        ItemStack rod = player.getInventory().getItemInMainHand();

        if (!SwapRod.isSwapRod(rod)) {
            rod = player.getInventory().getItemInOffHand();
            if (!SwapRod.isSwapRod(rod)) {
                return;
            }
        }

        if (event.getState() == PlayerFishEvent.State.CAUGHT_ENTITY) {
            Entity caught = event.getCaught();

            if (!(caught instanceof LivingEntity)) {
                return;
            }

            Location playerLoc = player.getLocation().clone();
            Location entityLoc = caught.getLocation().clone();

            player.teleport(entityLoc);
            caught.teleport(playerLoc);

            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
            caught.getWorld().playSound(caught.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);

            ItemMeta meta = rod.getItemMeta();
            if (meta instanceof Damageable damageable) {
                int currentDamage = damageable.getDamage();
                int maxDamage = SwapRod.MAX_DURABILITY;

                if (currentDamage + 1 >= maxDamage) {
                    player.getInventory().remove(rod);
                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
                } else {
                    damageable.setDamage(currentDamage + 1);
                    rod.setItemMeta(meta);
                }
            }

            event.setCancelled(true);
        }

        if (event.getState() == PlayerFishEvent.State.REEL_IN ||
            event.getState() == PlayerFishEvent.State.IN_GROUND ||
            event.getState() == PlayerFishEvent.State.FAILED_ATTEMPT) {
            event.setExpToDrop(0);
        }
    }
}
