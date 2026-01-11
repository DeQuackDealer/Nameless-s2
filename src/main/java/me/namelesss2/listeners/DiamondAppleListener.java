package me.namelesss2.listeners;

import me.namelesss2.NamelessS2;
import me.namelesss2.items.DiamondApple;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
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

                FileConfiguration config = NamelessS2.getInstance().getConfig();

                p.removePotionEffect(PotionEffectType.REGENERATION);
                p.removePotionEffect(PotionEffectType.ABSORPTION);

                double absorptionHearts = config.getDouble("diamond-apple.absorption-hearts", 8.0);
                double currentAbsorption = p.getAbsorptionAmount();
                p.setAbsorptionAmount(currentAbsorption + (absorptionHearts * 2.0));

                if (config.getBoolean("diamond-apple.effects.regeneration.enabled", true)) {
                    int duration = config.getInt("diamond-apple.effects.regeneration.duration-seconds", 60) * 20;
                    int amplifier = config.getInt("diamond-apple.effects.regeneration.amplifier", 2);
                    p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, duration, amplifier, false, true, true));
                }

                if (config.getBoolean("diamond-apple.effects.resistance.enabled", true)) {
                    int duration = config.getInt("diamond-apple.effects.resistance.duration-seconds", 90) * 20;
                    int amplifier = config.getInt("diamond-apple.effects.resistance.amplifier", 1);
                    p.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, duration, amplifier, false, true, true));
                }

                if (config.getBoolean("diamond-apple.effects.strength.enabled", true)) {
                    int duration = config.getInt("diamond-apple.effects.strength.duration-seconds", 90) * 20;
                    int amplifier = config.getInt("diamond-apple.effects.strength.amplifier", 2);
                    p.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, duration, amplifier, false, true, true));
                }

                if (config.getBoolean("diamond-apple.effects.speed.enabled", true)) {
                    int duration = config.getInt("diamond-apple.effects.speed.duration-seconds", 480) * 20;
                    int amplifier = config.getInt("diamond-apple.effects.speed.amplifier", 1);
                    p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration, amplifier, false, true, true));
                }

                if (config.getBoolean("diamond-apple.effects.fire-resistance.enabled", true)) {
                    int duration = config.getInt("diamond-apple.effects.fire-resistance.duration-seconds", 480) * 20;
                    int amplifier = config.getInt("diamond-apple.effects.fire-resistance.amplifier", 0);
                    p.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, duration, amplifier, false, true, true));
                }
            }
        }.runTaskLater(NamelessS2.getInstance(), 1L);
    }
}
