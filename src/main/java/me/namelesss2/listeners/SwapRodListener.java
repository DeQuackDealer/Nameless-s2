package me.namelesss2.listeners;

import me.namelesss2.NamelessS2;
import me.namelesss2.items.SwapRod;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.RayTraceResult;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SwapRodListener implements Listener {

    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private static final double MAX_DISTANCE = 256.0;

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null || !SwapRod.isSwapRod(item)) {
            return;
        }

        event.setCancelled(true);

        FileConfiguration config = NamelessS2.getInstance().getConfig();
        int cooldownSeconds = config.getInt("swap-rod.cooldown-seconds", 5);
        long cooldownMs = cooldownSeconds * 1000L;

        long currentTime = System.currentTimeMillis();
        Long lastUse = cooldowns.get(player.getUniqueId());

        if (lastUse != null && currentTime - lastUse < cooldownMs) {
            long remaining = (cooldownMs - (currentTime - lastUse)) / 1000 + 1;
            player.sendActionBar(Component.text("Swap Rod on cooldown: " + remaining + "s")
                    .color(NamedTextColor.RED));
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.3f, 0.5f);
            return;
        }

        double maxDistance = config.getDouble("swap-rod.max-distance", MAX_DISTANCE);
        
        RayTraceResult entityResult = player.getWorld().rayTraceEntities(
                player.getEyeLocation(),
                player.getEyeLocation().getDirection(),
                maxDistance,
                0.5,
                entity -> entity instanceof LivingEntity && !entity.equals(player)
        );

        if (entityResult == null || entityResult.getHitEntity() == null) {
            player.sendActionBar(Component.text("No target found!")
                    .color(NamedTextColor.RED));
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.3f, 0.5f);
            return;
        }

        Entity target = entityResult.getHitEntity();
        double distanceToTarget = player.getEyeLocation().distance(target.getLocation().add(0, 1, 0));

        RayTraceResult blockResult = player.getWorld().rayTraceBlocks(
                player.getEyeLocation(),
                player.getEyeLocation().getDirection(),
                distanceToTarget,
                FluidCollisionMode.NEVER,
                true
        );

        if (blockResult != null && blockResult.getHitBlock() != null) {
            double blockDistance = player.getEyeLocation().distance(blockResult.getHitPosition().toLocation(player.getWorld()));
            if (blockDistance < distanceToTarget) {
                player.sendActionBar(Component.text("Target blocked by obstacle!")
                        .color(NamedTextColor.RED));
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.3f, 0.5f);
                return;
            }
        }

        Location playerLoc = player.getLocation().clone();
        Location targetLoc = target.getLocation().clone();

        player.getWorld().spawnParticle(Particle.PORTAL, playerLoc.clone().add(0, 1, 0), 30, 0.5, 0.5, 0.5, 0.1);
        target.getWorld().spawnParticle(Particle.PORTAL, targetLoc.clone().add(0, 1, 0), 30, 0.5, 0.5, 0.5, 0.1);

        player.teleport(targetLoc);
        target.teleport(playerLoc);

        float soundVolume = (float) config.getDouble("swap-rod.sound-volume", 0.3);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, soundVolume, 1.0f);
        target.getWorld().playSound(target.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, soundVolume, 1.0f);

        String swapMessage = config.getString("messages.swap-success", "Swapped positions!");
        player.sendMessage(Component.text(swapMessage)
                .color(NamedTextColor.GOLD));

        cooldowns.put(player.getUniqueId(), currentTime);

        ItemMeta meta = item.getItemMeta();
        if (meta instanceof Damageable damageable) {
            int currentDamage = damageable.getDamage();
            int maxDamage = SwapRod.MAX_DURABILITY;

            if (currentDamage + 1 >= maxDamage) {
                player.getInventory().setItemInMainHand(null);
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 0.5f, 1.0f);
            } else {
                damageable.setDamage(currentDamage + 1);
                item.setItemMeta(meta);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        cooldowns.remove(event.getPlayer().getUniqueId());
    }
}
