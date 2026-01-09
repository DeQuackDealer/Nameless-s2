package me.namelesss2.listeners;

import me.namelesss2.NamelessS2;
import me.namelesss2.items.LifestealSword;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class LifestealSwordListener implements Listener {

    private static final UUID LIFESTEAL_MODIFIER_UUID = UUID.fromString("a1b2c3d4-e5f6-4a5b-8c9d-0e1f2a3b4c5d");
    private static final String LIFESTEAL_MODIFIER_NAME = "lifesteal_bonus_health";

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerKill(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        if (killer == null) {
            return;
        }

        ItemStack weapon = killer.getInventory().getItemInMainHand();
        if (!LifestealSword.isLifestealSword(weapon)) {
            return;
        }

        FileConfiguration config = NamelessS2.getInstance().getConfig();
        double heartsPerKill = config.getDouble("lifesteal-sword.hearts-per-kill", 1.0);
        double maxBonusHearts = config.getDouble("lifesteal-sword.max-bonus-hearts", 10.0);
        double healthPerKill = heartsPerKill * 2.0;
        double maxBonusHealth = maxBonusHearts * 2.0;

        AttributeInstance maxHealthAttr = killer.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (maxHealthAttr == null) {
            return;
        }

        double currentBonus = 0.0;
        AttributeModifier existingModifier = null;
        for (AttributeModifier modifier : maxHealthAttr.getModifiers()) {
            if (LIFESTEAL_MODIFIER_NAME.equals(modifier.getName())) {
                currentBonus = modifier.getAmount();
                existingModifier = modifier;
                break;
            }
        }

        if (currentBonus >= maxBonusHealth) {
            killer.sendMessage(Component.text("You have reached the maximum bonus hearts!")
                    .color(NamedTextColor.YELLOW));
            return;
        }

        double newBonus = Math.min(currentBonus + healthPerKill, maxBonusHealth);

        if (existingModifier != null) {
            maxHealthAttr.removeModifier(existingModifier);
        }

        AttributeModifier newModifier = new AttributeModifier(
                LIFESTEAL_MODIFIER_UUID,
                LIFESTEAL_MODIFIER_NAME,
                newBonus,
                AttributeModifier.Operation.ADD_NUMBER
        );
        maxHealthAttr.addModifier(newModifier);

        double heartsGained = heartsPerKill;
        double totalBonusHearts = newBonus / 2.0;

        killer.sendMessage(Component.text("+" + String.format("%.1f", heartsGained) + " permanent heart! ")
                .color(NamedTextColor.RED)
                .append(Component.text("(Total bonus: " + String.format("%.1f", totalBonusHearts) + " hearts)")
                        .color(NamedTextColor.GRAY)));

        killer.playSound(killer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
        killer.getWorld().spawnParticle(Particle.HEART, 
                killer.getLocation().add(0, 2, 0), 10, 0.5, 0.3, 0.5, 0.1);

        double newMaxHealth = maxHealthAttr.getValue();
        if (killer.getHealth() < newMaxHealth) {
            killer.setHealth(Math.min(killer.getHealth() + healthPerKill, newMaxHealth));
        }
    }
}
