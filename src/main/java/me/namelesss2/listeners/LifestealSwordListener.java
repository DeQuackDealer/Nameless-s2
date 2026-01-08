package me.namelesss2.listeners;

import me.namelesss2.NamelessS2;
import me.namelesss2.items.LifestealSword;
import me.namelesss2.utils.ItemUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class LifestealSwordListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) {
            return;
        }

        if (!(event.getEntity() instanceof LivingEntity)) {
            return;
        }

        ItemStack weapon = player.getInventory().getItemInMainHand();
        if (!LifestealSword.isLifestealSword(weapon)) {
            return;
        }

        long currentTime = System.currentTimeMillis();
        long cooldownEnd = ItemUtils.getLongData(weapon, NamelessS2.LIFESTEAL_COOLDOWN_KEY, 0L);

        if (currentTime < cooldownEnd) {
            return;
        }

        if (cooldownEnd > 0 && currentTime >= cooldownEnd) {
            ItemUtils.setIntData(weapon, NamelessS2.LIFESTEAL_HITS_KEY, 0);
            ItemUtils.setLongData(weapon, NamelessS2.LIFESTEAL_COOLDOWN_KEY, 0L);
        }

        double damage = event.getFinalDamage();
        double healAmount = damage * LifestealSword.LIFESTEAL_PERCENT;

        double maxHealth = player.getAttribute(Attribute.MAX_HEALTH).getValue();
        double newHealth = Math.min(player.getHealth() + healAmount, maxHealth);
        player.setHealth(newHealth);

        int currentHits = ItemUtils.getIntData(weapon, NamelessS2.LIFESTEAL_HITS_KEY, 0) + 1;
        ItemUtils.setIntData(weapon, NamelessS2.LIFESTEAL_HITS_KEY, currentHits);

        if (currentHits >= LifestealSword.HITS_BEFORE_COOLDOWN) {
            long newCooldownEnd = currentTime + LifestealSword.COOLDOWN_DURATION_MS;
            ItemUtils.setLongData(weapon, NamelessS2.LIFESTEAL_COOLDOWN_KEY, newCooldownEnd);
            ItemUtils.setIntData(weapon, NamelessS2.LIFESTEAL_HITS_KEY, 0);

            player.sendMessage(Component.text("Lifesteal is now on cooldown for 25 seconds!")
                    .color(NamedTextColor.YELLOW));
        }
    }
}
