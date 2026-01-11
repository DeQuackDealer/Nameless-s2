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
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SwapRodListener implements Listener {

    private final Map<UUID, Long> cooldowns = new HashMap<>();

    private int countSwapRodsInInventory(Player player) {
        int count = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && SwapRod.isSwapRod(item)) {
                count += item.getAmount();
            }
        }
        return count;
    }

    private int countSwapRodsInItems(ItemStack... items) {
        int count = 0;
        for (ItemStack item : items) {
            if (item != null && SwapRod.isSwapRod(item)) {
                count += item.getAmount();
            }
        }
        return count;
    }

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
        double maxDistance = config.getDouble("swap-rod.radius", 16.0);
        long cooldownMs = config.getLong("swap-rod.cooldown-ms", 1000L);

        long currentTime = System.currentTimeMillis();
        Long lastUse = cooldowns.get(player.getUniqueId());

        if (lastUse != null && currentTime - lastUse < cooldownMs) {
            return;
        }

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

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 0.3f, 1.0f);
        target.getWorld().playSound(target.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 0.3f, 1.0f);

        cooldowns.put(player.getUniqueId(), currentTime);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        ItemStack cursor = event.getCursor();
        ItemStack current = event.getCurrentItem();
        
        boolean cursorIsSwapRod = cursor != null && SwapRod.isSwapRod(cursor);
        boolean currentIsSwapRod = current != null && SwapRod.isSwapRod(current);
        
        int currentCount = countSwapRodsInInventory(player);

        Inventory clickedInventory = event.getClickedInventory();
        boolean clickedPlayerInventory = clickedInventory != null && clickedInventory.equals(player.getInventory());

        if (event.isShiftClick() && currentIsSwapRod && !clickedPlayerInventory) {
            if (currentCount >= 1) {
                event.setCancelled(true);
                player.sendMessage(Component.text("You can only hold one Swap Rod!")
                        .color(NamedTextColor.RED));
                return;
            }
        }

        if (cursorIsSwapRod && clickedPlayerInventory) {
            if (currentCount >= 1 && !currentIsSwapRod) {
                event.setCancelled(true);
                player.sendMessage(Component.text("You can only hold one Swap Rod!")
                        .color(NamedTextColor.RED));
                return;
            }
        }

        if (event.getClick().isKeyboardClick() && currentIsSwapRod && !clickedPlayerInventory) {
            if (currentCount >= 1) {
                event.setCancelled(true);
                player.sendMessage(Component.text("You can only hold one Swap Rod!")
                        .color(NamedTextColor.RED));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        ItemStack draggedItem = event.getOldCursor();
        if (draggedItem == null || !SwapRod.isSwapRod(draggedItem)) {
            return;
        }

        boolean draggedToPlayerInventory = false;
        for (int slot : event.getRawSlots()) {
            if (slot >= event.getView().getTopInventory().getSize()) {
                draggedToPlayerInventory = true;
                break;
            }
        }

        if (draggedToPlayerInventory) {
            int currentCount = countSwapRodsInInventory(player);
            if (currentCount >= 1) {
                event.setCancelled(true);
                player.sendMessage(Component.text("You can only hold one Swap Rod!")
                        .color(NamedTextColor.RED));
            }
        }
    }

    @EventHandler
    public void onPickupItem(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        ItemStack item = event.getItem().getItemStack();
        if (SwapRod.isSwapRod(item)) {
            if (countSwapRodsInInventory(player) >= 1) {
                event.setCancelled(true);
                player.sendMessage(Component.text("You can only hold one Swap Rod!")
                        .color(NamedTextColor.RED));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onCraftSwapRod(CraftItemEvent event) {
        ItemStack result = event.getRecipe().getResult();

        if (!SwapRod.isSwapRod(result)) {
            return;
        }

        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        if (countSwapRodsInInventory(player) >= 1) {
            event.setCancelled(true);
            player.sendMessage(Component.text("You can only hold one Swap Rod!")
                    .color(NamedTextColor.RED));
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        cooldowns.remove(event.getPlayer().getUniqueId());
    }
}
