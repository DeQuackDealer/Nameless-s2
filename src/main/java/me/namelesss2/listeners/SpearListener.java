package me.namelesss2.listeners;

import me.namelesss2.NamelessS2;
import me.namelesss2.items.Spear;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.SmithingInventory;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class SpearListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (!(event.getEntity() instanceof Trident trident)) {
            return;
        }

        if (!(trident.getShooter() instanceof Player player)) {
            return;
        }

        ItemStack mainHand = player.getInventory().getItemInMainHand();
        ItemStack offHand = player.getInventory().getItemInOffHand();

        if (Spear.isSpear(mainHand) || Spear.isSpear(offHand)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onCraftSpear(CraftItemEvent event) {
        ItemStack result = event.getRecipe().getResult();
        
        if (!Spear.isSpear(result)) {
            return;
        }

        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        if (hasSpear(player)) {
            event.setCancelled(true);
            player.sendMessage(Component.text("You can only have one spear at a time!")
                    .color(NamedTextColor.RED));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerKill(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        if (killer == null) {
            return;
        }

        ItemStack spear = findSpearInInventory(killer);
        if (spear == null) {
            return;
        }

        Spear.Tier currentTier = Spear.getTier(spear);
        
        if (currentTier.getNextTier() == null) {
            return;
        }

        int currentKills = Spear.getKills(spear) + 1;
        int killsNeeded = currentTier.getKillsToUpgrade();

        if (currentKills >= killsNeeded) {
            Spear.Tier newTier = currentTier.getNextTier();
            Spear.updateSpear(spear, newTier, 0);

            killer.sendMessage(Component.text("Your spear has been upgraded to ")
                    .color(NamedTextColor.GREEN)
                    .append(Component.text(newTier.getDisplayName())
                            .color(newTier.getColor())
                            .decoration(TextDecoration.BOLD, true))
                    .append(Component.text("!")
                            .color(NamedTextColor.GREEN)));

            killer.playSound(killer.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
            killer.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, 
                    killer.getLocation().add(0, 1, 0), 50, 0.5, 0.5, 0.5, 0.1);
        } else {
            Spear.updateSpear(spear, currentTier, currentKills);

            killer.sendMessage(Component.text("Spear Progress: ")
                    .color(NamedTextColor.GRAY)
                    .append(Component.text(currentKills + "/" + killsNeeded)
                            .color(NamedTextColor.YELLOW)
                            .decoration(TextDecoration.BOLD, true))
                    .append(Component.text(" kills")
                            .color(NamedTextColor.GRAY)));
        }

        handleVictimSpearDowngrade(victim);
    }

    private void handleVictimSpearDowngrade(Player victim) {
        ItemStack spear = findSpearInInventory(victim);
        if (spear == null) {
            return;
        }

        Spear.Tier currentTier = Spear.getTier(spear);
        Spear.Tier previousTier = currentTier.getPreviousTier();

        if (previousTier != null) {
            Spear.updateSpear(spear, previousTier, 0);
            victim.sendMessage(Component.text("Your spear has been downgraded to ")
                    .color(NamedTextColor.RED)
                    .append(Component.text(previousTier.getDisplayName())
                            .color(previousTier.getColor())
                            .decoration(TextDecoration.BOLD, true))
                    .append(Component.text("!")
                            .color(NamedTextColor.RED)));
        } else {
            Spear.updateSpear(spear, currentTier, 0);
            victim.sendMessage(Component.text("Your spear kill progress has been reset!")
                    .color(NamedTextColor.RED));
        }
    }

    private ItemStack findSpearInInventory(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && Spear.isSpear(item)) {
                return item;
            }
        }
        return null;
    }

    private boolean hasSpear(Player player) {
        return findSpearInInventory(player) != null;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        AnvilInventory inventory = event.getInventory();
        ItemStack firstItem = inventory.getItem(0);
        ItemStack secondItem = inventory.getItem(1);

        if (firstItem == null || secondItem == null) {
            return;
        }

        if (!Spear.isSpear(firstItem)) {
            return;
        }

        Spear.Tier tier = Spear.getTier(firstItem);
        Material repairMaterial = tier.getRepairMaterial();

        if (secondItem.getType() != repairMaterial) {
            return;
        }

        ItemStack result = firstItem.clone();
        ItemMeta meta = result.getItemMeta();

        if (meta instanceof Damageable damageable) {
            damageable.setDamage(0);
            result.setItemMeta(meta);
        }

        event.setResult(result);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPrepareSmithing(PrepareSmithingEvent event) {
        SmithingInventory inventory = event.getInventory();
        ItemStack inputItem = inventory.getInputEquipment();
        ItemStack materialItem = inventory.getInputMineral();

        if (inputItem == null || materialItem == null) {
            return;
        }

        if (!Spear.isSpear(inputItem)) {
            return;
        }

        Spear.Tier currentTier = Spear.getTier(inputItem);

        if (currentTier != Spear.Tier.DIAMOND) {
            return;
        }

        if (materialItem.getType() != Material.NETHERITE_INGOT) {
            return;
        }

        ItemStack result = Spear.create(Spear.Tier.NETHERITE);
        event.setResult(result);
    }
}
