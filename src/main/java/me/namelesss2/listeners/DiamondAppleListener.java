package me.namelesss2.listeners;

import me.namelesss2.items.DiamondApple;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class DiamondAppleListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerConsume(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();

        if (!DiamondApple.isDiamondApple(item)) {
            return;
        }

        Player player = event.getPlayer();

        player.addPotionEffect(new PotionEffect(
                PotionEffectType.ABSORPTION,
                20 * 60 * 5,
                3,
                false,
                true,
                true
        ));

        player.addPotionEffect(new PotionEffect(
                PotionEffectType.RESISTANCE,
                20 * 60 * 3,
                1,
                false,
                true,
                true
        ));

        player.addPotionEffect(new PotionEffect(
                PotionEffectType.REGENERATION,
                20 * 45,
                2,
                false,
                true,
                true
        ));

        player.addPotionEffect(new PotionEffect(
                PotionEffectType.STRENGTH,
                20 * 60 * 3,
                1,
                false,
                true,
                true
        ));

        player.addPotionEffect(new PotionEffect(
                PotionEffectType.FIRE_RESISTANCE,
                20 * 60 * 10,
                0,
                false,
                true,
                true
        ));

        player.addPotionEffect(new PotionEffect(
                PotionEffectType.SPEED,
                20 * 60 * 2,
                1,
                false,
                true,
                true
        ));
    }
}
