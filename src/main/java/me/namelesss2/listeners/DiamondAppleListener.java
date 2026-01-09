package me.namelesss2.listeners;

import me.namelesss2.NamelessS2;
import me.namelesss2.items.DiamondApple;
import me.namelesss2.managers.DiamondHeartsManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
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
        FileConfiguration config = NamelessS2.getInstance().getConfig();

        int diamondHearts = config.getInt("diamond-apple.diamond-hearts.amount", 3);
        DiamondHeartsManager.getInstance().addDiamondHearts(player, diamondHearts);

        player.sendMessage(Component.text("You gained " + diamondHearts + " Diamond Hearts!")
                .color(NamedTextColor.AQUA));
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
                
                int regenDuration = config.getInt("diamond-apple.regeneration-duration-seconds", 10) * 20;
                int resistDuration = config.getInt("diamond-apple.resistance-duration-seconds", 60) * 20;
                int fireResDuration = config.getInt("diamond-apple.fire-resistance-duration-seconds", 60) * 20;
                int strengthDuration = config.getInt("diamond-apple.strength-duration-seconds", 60) * 20;

                p.addPotionEffect(new PotionEffect(
                        PotionEffectType.RESISTANCE,
                        resistDuration,
                        1,
                        false,
                        true,
                        true
                ));

                p.addPotionEffect(new PotionEffect(
                        PotionEffectType.REGENERATION,
                        regenDuration,
                        2,
                        false,
                        true,
                        true
                ));

                p.addPotionEffect(new PotionEffect(
                        PotionEffectType.STRENGTH,
                        strengthDuration,
                        1,
                        false,
                        true,
                        true
                ));

                p.addPotionEffect(new PotionEffect(
                        PotionEffectType.FIRE_RESISTANCE,
                        fireResDuration,
                        0,
                        false,
                        true,
                        true
                ));
            }
        }.runTaskLater(NamelessS2.getInstance(), 1L);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        DiamondHeartsManager manager = DiamondHeartsManager.getInstance();
        if (!manager.hasDiamondHealth(player)) {
            return;
        }

        double damage = event.getDamage();
        double remainingDamage = manager.absorbDamage(player, damage);
        
        event.setDamage(remainingDamage);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        DiamondHeartsManager.getInstance().clearPlayer(event.getPlayer());
    }
}
