package me.namelesss2.listeners;

import me.namelesss2.NamelessS2;
import me.namelesss2.items.DiamondApple;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class DiamondAppleListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerConsume(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();

        if (!DiamondApple.isDiamondApple(item)) {
            return;
        }

        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1.0f, 1.5f);

        new BukkitRunnable() {
            @Override
            public void run() {
                Player p = NamelessS2.getInstance().getServer().getPlayer(playerUUID);
                if (p == null || !p.isOnline()) {
                    return;
                }
                
                p.removePotionEffect(PotionEffectType.ABSORPTION);
                p.removePotionEffect(PotionEffectType.REGENERATION);

                p.addPotionEffect(new PotionEffect(
                        PotionEffectType.ABSORPTION,
                        9600,
                        3,
                        false,
                        true,
                        true
                ));

                p.addPotionEffect(new PotionEffect(
                        PotionEffectType.REGENERATION,
                        1200,
                        2,
                        false,
                        true,
                        true
                ));

                p.addPotionEffect(new PotionEffect(
                        PotionEffectType.RESISTANCE,
                        1800,
                        1,
                        false,
                        true,
                        true
                ));

                p.addPotionEffect(new PotionEffect(
                        PotionEffectType.STRENGTH,
                        1800,
                        2,
                        false,
                        true,
                        true
                ));

                p.addPotionEffect(new PotionEffect(
                        PotionEffectType.SPEED,
                        9600,
                        1,
                        false,
                        true,
                        true
                ));

                p.addPotionEffect(new PotionEffect(
                        PotionEffectType.FIRE_RESISTANCE,
                        9600,
                        0,
                        false,
                        true,
                        true
                ));
            }
        }.runTaskLater(NamelessS2.getInstance(), 1L);
    }
}
