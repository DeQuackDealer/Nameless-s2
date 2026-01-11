package me.namelesss2.commands;

import me.namelesss2.items.DiamondApple;
import me.namelesss2.items.LifestealSword;
import me.namelesss2.items.Spear;
import me.namelesss2.items.SwapRod;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GiveCommand implements CommandExecutor, TabCompleter {

    private static final List<String> ITEMS = Arrays.asList(
            "lifesteal_sword",
            "swap_rod",
            "diamond_apple",
            "spear_wood",
            "spear_copper",
            "spear_iron",
            "spear_diamond",
            "spear_netherite"
    );

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("namelesss2.give")) {
            sender.sendMessage(Component.text("No permission!").color(NamedTextColor.RED));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(Component.text("Usage: /ns <player> <item>").color(NamedTextColor.RED));
            sender.sendMessage(Component.text("Items: " + String.join(", ", ITEMS)).color(NamedTextColor.GRAY));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(Component.text("Player not found!").color(NamedTextColor.RED));
            return true;
        }

        String itemName = args[1].toLowerCase();
        ItemStack item = getItem(itemName);

        if (item == null) {
            sender.sendMessage(Component.text("Unknown item: " + itemName).color(NamedTextColor.RED));
            sender.sendMessage(Component.text("Items: " + String.join(", ", ITEMS)).color(NamedTextColor.GRAY));
            return true;
        }

        if (itemName.equals("swap_rod") && hasSwapRod(target)) {
            sender.sendMessage(Component.text("Warning: " + target.getName() + " already has a Swap Rod!")
                    .color(NamedTextColor.YELLOW));
        }

        if (itemName.startsWith("spear_") && hasSpear(target)) {
            sender.sendMessage(Component.text("Warning: " + target.getName() + " already has a Spear!")
                    .color(NamedTextColor.YELLOW));
        }

        target.getInventory().addItem(item);
        sender.sendMessage(Component.text("Gave " + itemName + " to " + target.getName()).color(NamedTextColor.GREEN));

        return true;
    }

    private boolean hasSwapRod(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && SwapRod.isSwapRod(item)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasSpear(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && Spear.isSpear(item)) {
                return true;
            }
        }
        return false;
    }

    private ItemStack getItem(String name) {
        return switch (name) {
            case "lifesteal_sword" -> LifestealSword.create();
            case "swap_rod" -> SwapRod.create();
            case "diamond_apple" -> DiamondApple.create();
            case "spear_wood" -> Spear.create(Spear.Tier.WOOD);
            case "spear_copper" -> Spear.create(Spear.Tier.COPPER);
            case "spear_iron" -> Spear.create(Spear.Tier.IRON);
            case "spear_diamond" -> Spear.create(Spear.Tier.DIAMOND);
            case "spear_netherite" -> Spear.create(Spear.Tier.NETHERITE);
            default -> null;
        };
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (args.length == 2) {
            return ITEMS.stream()
                    .filter(item -> item.startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return List.of();
    }
}
