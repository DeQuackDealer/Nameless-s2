package me.namelesss2.listeners;

import me.namelesss2.items.Spear;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.Sound;
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

        Spear.Tier currentTier = Spear.getTier(spear);
        int currentKills = Spear.getKills(spear) + 1;

        if (currentTier.getNextTier() != null && currentKills >= currentTier.getKillsToUpgrade()) {
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
        } else {
            Spear.updateSpear(spear, currentTier, currentKills);

            if (currentTier.getNextTier() != null) {
                killer.sendMessage(Component.text("Kill progress: ")
                        .color(NamedTextColor.GRAY)
                        .append(Component.text(currentKills + "/" + currentTier.getKillsToUpgrade())
                                .color(NamedTextColor.YELLOW)));
            }
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
            victim.sendMessage(Component.text("Your kill progress has been reset!")
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
