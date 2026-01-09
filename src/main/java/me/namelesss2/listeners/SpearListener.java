package me.namelesss2.listeners;

import me.namelesss2.NamelessS2;
import me.namelesss2.items.Spear;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class SpearListener implements Listener {

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

        FileConfiguration config = NamelessS2.getInstance().getConfig();
        Spear.Tier currentTier = Spear.getTier(spear);
        int currentKills = Spear.getKills(spear) + 1;

        int killsNeeded = getKillsToUpgrade(config, currentTier);

        if (currentTier.getNextTier() != null && currentKills >= killsNeeded) {
            Spear.Tier newTier = currentTier.getNextTier();
            Spear.updateSpear(spear, newTier, 0);

            String upgradeMessage = config.getString("messages.spear-upgrade", 
                    "Your spear has been upgraded to %tier%!");
            upgradeMessage = upgradeMessage.replace("%tier%", newTier.getDisplayName());

            killer.sendMessage(Component.text(upgradeMessage)
                    .color(NamedTextColor.GREEN)
                    .decoration(TextDecoration.BOLD, true));

            killer.playSound(killer.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
            killer.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, 
                    killer.getLocation().add(0, 1, 0), 50, 0.5, 0.5, 0.5, 0.1);
        } else {
            Spear.updateSpear(spear, currentTier, currentKills);

            if (currentTier.getNextTier() != null) {
                killer.sendMessage(Component.text("Spear Progress: ")
                        .color(NamedTextColor.GRAY)
                        .append(Component.text(currentKills + "/" + killsNeeded)
                                .color(NamedTextColor.YELLOW)
                                .decoration(TextDecoration.BOLD, true))
                        .append(Component.text(" kills")
                                .color(NamedTextColor.GRAY)));
            }
        }

        handleVictimSpearDowngrade(victim);
    }

    private int getKillsToUpgrade(FileConfiguration config, Spear.Tier tier) {
        return switch (tier) {
            case WOOD -> config.getInt("spear.wood.kills-to-upgrade", 1);
            case COPPER -> config.getInt("spear.copper.kills-to-upgrade", 2);
            case IRON -> config.getInt("spear.iron.kills-to-upgrade", 3);
            case DIAMOND -> config.getInt("spear.diamond.kills-to-upgrade", 4);
            case NETHERITE -> 999;
        };
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
}
