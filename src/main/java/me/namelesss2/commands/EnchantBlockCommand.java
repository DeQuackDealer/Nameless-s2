package me.namelesss2.commands;

import me.namelesss2.NamelessS2;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
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

public class EnchantBlockCommand implements CommandExecutor, TabCompleter {

    public static final NamespacedKey ENCHANT_BLOCKED_KEY = new NamespacedKey("nameless_s2", "enchant_blocked");

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can use this command!").color(NamedTextColor.RED));
            return true;
        }

        if (!player.hasPermission("namelesss2.enchantblock")) {
            player.sendMessage(Component.text("No permission!").color(NamedTextColor.RED));
            return true;
        }

        ItemStack item = player.getInventory().getItemInMainHand();

        if (item.getType() == Material.AIR) {
            player.sendMessage(Component.text("You must hold an item in your hand!").color(NamedTextColor.RED));
            return true;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            player.sendMessage(Component.text("This item cannot be modified!").color(NamedTextColor.RED));
            return true;
        }

        PersistentDataContainer pdc = meta.getPersistentDataContainer();

        if (pdc.has(ENCHANT_BLOCKED_KEY, PersistentDataType.BYTE)) {
            pdc.remove(ENCHANT_BLOCKED_KEY);
            item.setItemMeta(meta);
            player.sendMessage(Component.text("Enchanting table block removed from this item!").color(NamedTextColor.GREEN));
        } else {
            pdc.set(ENCHANT_BLOCKED_KEY, PersistentDataType.BYTE, (byte) 1);
            item.setItemMeta(meta);
            player.sendMessage(Component.text("This item is now blocked from the enchanting table!").color(NamedTextColor.GREEN));
        }

        return true;
    }

    public static boolean isEnchantBlocked(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        return pdc.has(ENCHANT_BLOCKED_KEY, PersistentDataType.BYTE);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return List.of();
    }
}
