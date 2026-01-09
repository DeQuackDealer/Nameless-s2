package me.namelesss2.listeners;

import me.namelesss2.NamelessS2;
import me.namelesss2.items.SwapRod;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SwapRodListener implements Listener {

    private final Map<UUID, Long> cooldowns = new HashMap<>();

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null || !SwapRod.isSwapRod(item)) {
            return;
        }

        FileConfiguration config = NamelessS2.getInstance().getConfig();
        int cooldownSeconds = config.getInt("swap-rod.cooldown-seconds", 5);
        long cooldownMs = cooldownSeconds * 1000L;

        long currentTime = System.currentTimeMillis();
        Long lastUse = cooldowns.get(player.getUniqueId());

        if (lastUse != null && currentTime - lastUse < cooldownMs) {
            long remaining = (cooldownMs - (currentTime - lastUse)) / 1000 + 1;
            event.setCancelled(true);
            player.sendActionBar(Component.text("Swap Rod on cooldown: " + remaining + "s")
                    .color(NamedTextColor.RED));
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.5f, 0.5f);
            return;
        }
    }

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

        FileConfiguration config = NamelessS2.getInstance().getConfig();
        int cooldownSeconds = config.getInt("swap-rod.cooldown-seconds", 5);
        long cooldownMs = cooldownSeconds * 1000L;
        long currentTime = System.currentTimeMillis();
        Long lastUse = cooldowns.get(player.getUniqueId());

        if (event.getState() == PlayerFishEvent.State.FISHING) {
            if (lastUse != null && currentTime - lastUse < cooldownMs) {
                long remaining = (cooldownMs - (currentTime - lastUse)) / 1000 + 1;
                event.setCancelled(true);
                player.sendActionBar(Component.text("Swap Rod on cooldown: " + remaining + "s")
                        .color(NamedTextColor.RED));
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.5f, 0.5f);
                
                FishHook hook = event.getHook();
                if (hook != null) {
                    hook.remove();
                }
                return;
            }
        }

        if (event.getState() == PlayerFishEvent.State.CAUGHT_ENTITY) {
            Entity caught = event.getCaught();

            if (!(caught instanceof LivingEntity)) {
                return;
            }

            if (lastUse != null && currentTime - lastUse < cooldownMs) {
                long remaining = (cooldownMs - (currentTime - lastUse)) / 1000 + 1;
                player.sendActionBar(Component.text("Swap Rod on cooldown: " + remaining + "s")
                        .color(NamedTextColor.RED));
                event.setCancelled(true);
                return;
            }

            Location playerLoc = player.getLocation().clone();
            Location entityLoc = caught.getLocation().clone();

            player.getWorld().spawnParticle(Particle.PORTAL, playerLoc.clone().add(0, 1, 0), 30, 0.5, 0.5, 0.5, 0.1);
            caught.getWorld().spawnParticle(Particle.PORTAL, entityLoc.clone().add(0, 1, 0), 30, 0.5, 0.5, 0.5, 0.1);

            player.teleport(entityLoc);
            caught.teleport(playerLoc);

            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
            caught.getWorld().playSound(caught.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);

            String swapMessage = config.getString("messages.swap-success", "Swapped positions!");
            player.sendMessage(Component.text(swapMessage)
                    .color(NamedTextColor.GOLD));

            cooldowns.put(player.getUniqueId(), currentTime);

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

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        cooldowns.remove(event.getPlayer().getUniqueId());
    }
}
