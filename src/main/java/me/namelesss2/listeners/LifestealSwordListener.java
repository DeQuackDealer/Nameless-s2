package me.namelesss2.listeners;

import me.namelesss2.NamelessS2;
import me.namelesss2.items.LifestealSword;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.NamespacedKey;
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

public class LifestealSwordListener implements Listener {

    private NamespacedKey getPlayerModifierKey(Player player) {
        String playerKeyPart = player.getUniqueId().toString().replace("-", "_");
        return new NamespacedKey("nameless_s2", "lifesteal_health_" + playerKeyPart);
    }

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

        AttributeInstance killerMaxHealth = killer.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        AttributeInstance victimMaxHealth = victim.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        
        if (killerMaxHealth == null || victimMaxHealth == null) {
            return;
        }

        NamespacedKey killerModKey = getPlayerModifierKey(killer);
        double killerCurrentBonus = 0.0;
        AttributeModifier killerExistingMod = null;
        
        for (AttributeModifier modifier : killerMaxHealth.getModifiers()) {
            if (modifier.getKey().equals(killerModKey)) {
                killerCurrentBonus = modifier.getAmount();
                killerExistingMod = modifier;
                break;
            }
        }

        if (killerCurrentBonus >= maxBonusHealth) {
            killer.sendMessage(Component.text("You have reached the maximum bonus hearts!")
                    .color(NamedTextColor.YELLOW));
            return;
        }

        NamespacedKey victimModKey = getPlayerModifierKey(victim);
        double victimCurrentBonus = 0.0;
        AttributeModifier victimExistingMod = null;
        
        for (AttributeModifier modifier : victimMaxHealth.getModifiers()) {
            if (modifier.getKey().equals(victimModKey)) {
                victimCurrentBonus = modifier.getAmount();
                victimExistingMod = modifier;
                break;
            }
        }

        double killerNewBonus = Math.min(killerCurrentBonus + healthPerKill, maxBonusHealth);

        if (killerExistingMod != null) {
            killerMaxHealth.removeModifier(killerExistingMod);
        }

        AttributeModifier killerNewMod = new AttributeModifier(
                killerModKey,
                killerNewBonus,
                AttributeModifier.Operation.ADD_NUMBER
        );
        killerMaxHealth.addModifier(killerNewMod);

        double victimNewBonus = victimCurrentBonus - healthPerKill;
        
        if (victimExistingMod != null) {
            victimMaxHealth.removeModifier(victimExistingMod);
        }

        if (victimNewBonus != 0) {
            AttributeModifier victimNewMod = new AttributeModifier(
                    victimModKey,
                    victimNewBonus,
                    AttributeModifier.Operation.ADD_NUMBER
            );
            victimMaxHealth.addModifier(victimNewMod);
        }

        double heartsGained = heartsPerKill;
        double totalBonusHearts = killerNewBonus / 2.0;

        killer.sendMessage(Component.text("+" + String.format("%.1f", heartsGained) + " heart stolen! ")
                .color(NamedTextColor.RED)
                .append(Component.text("(Total bonus: " + String.format("%.1f", totalBonusHearts) + " hearts)")
                        .color(NamedTextColor.GRAY)));

        victim.sendMessage(Component.text("-" + String.format("%.1f", heartsGained) + " heart lost!")
                .color(NamedTextColor.DARK_RED));

        killer.playSound(killer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
        killer.getWorld().spawnParticle(Particle.HEART, 
                killer.getLocation().add(0, 2, 0), 10, 0.5, 0.3, 0.5, 0.1);

        double newMaxHealth = killerMaxHealth.getValue();
        if (killer.getHealth() < newMaxHealth) {
            killer.setHealth(Math.min(killer.getHealth() + healthPerKill, newMaxHealth));
        }
    }
}
