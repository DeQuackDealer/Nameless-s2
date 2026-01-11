package me.namelesss2.commands;

import me.namelesss2.NamelessS2;
import me.namelesss2.utils.ItemUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WithdrawCommand implements CommandExecutor, TabCompleter {

    public static final String LIFE_STAR_ID = "life_star";
    public static final NamespacedKey LIFE_STAR_KEY = new NamespacedKey("nameless_s2", "life_star");

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can use this command!").color(NamedTextColor.RED));
            return true;
        }

        AttributeInstance maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (maxHealth == null) {
            player.sendMessage(Component.text("Could not access health attribute!").color(NamedTextColor.RED));
            return true;
        }

        NamespacedKey modKey = getPlayerModifierKey(player);
        double currentBonus = 0.0;
        AttributeModifier existingMod = null;

        for (AttributeModifier modifier : maxHealth.getModifiers()) {
            if (modifier.getKey().equals(modKey)) {
                currentBonus = modifier.getAmount();
                existingMod = modifier;
                break;
            }
        }

        if (currentBonus < 2.0) {
            player.sendMessage(Component.text("You don't have any bonus hearts to withdraw!").color(NamedTextColor.RED));
            return true;
        }

        if (existingMod != null) {
            maxHealth.removeModifier(existingMod);
        }

        double newBonus = currentBonus - 2.0;
        if (newBonus > 0) {
            AttributeModifier newMod = new AttributeModifier(
                    modKey,
                    newBonus,
                    AttributeModifier.Operation.ADD_NUMBER
            );
            maxHealth.addModifier(newMod);
        }

        if (player.getHealth() > maxHealth.getValue()) {
            player.setHealth(maxHealth.getValue());
        }

        ItemStack lifeStar = createLifeStar();
        player.getInventory().addItem(lifeStar);

        player.sendMessage(Component.text("Withdrew 1 heart into a Life Star!")
                .color(NamedTextColor.GREEN));
        player.sendMessage(Component.text("Remaining bonus hearts: " + String.format("%.1f", newBonus / 2.0))
                .color(NamedTextColor.GRAY));
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);

        return true;
    }

    private NamespacedKey getPlayerModifierKey(Player player) {
        String playerKeyPart = player.getUniqueId().toString().replace("-", "_");
        return new NamespacedKey("nameless_s2", "lifesteal_health_" + playerKeyPart);
    }

    public static ItemStack createLifeStar() {
        ItemStack item = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = item.getItemMeta();

        Component displayName = Component.text("Life Star")
                .color(NamedTextColor.RED)
                .decoration(TextDecoration.ITALIC, false);
        meta.displayName(displayName);

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(NamelessS2.CUSTOM_ITEM_KEY, PersistentDataType.STRING, LIFE_STAR_ID);
        pdc.set(LIFE_STAR_KEY, PersistentDataType.BYTE, (byte) 1);

        item.setItemMeta(meta);
        return item;
    }

    public static boolean isLifeStar(ItemStack item) {
        return ItemUtils.isCustomItem(item, LIFE_STAR_ID);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return List.of();
    }
}
