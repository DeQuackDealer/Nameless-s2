package me.namelesss2.commands;

import me.namelesss2.NamelessS2;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class CreditCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("items")) {
            showItemsHelp(sender);
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("namelesss2.reload")) {
                sender.sendMessage(Component.text("You don't have permission to reload the config!")
                        .color(NamedTextColor.RED));
                return true;
            }
            NamelessS2.getInstance().reloadConfig();
            sender.sendMessage(Component.text("Configuration reloaded!")
                    .color(NamedTextColor.GREEN));
            return true;
        }

        sender.sendMessage(Component.empty());
        
        sender.sendMessage(Component.text("\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550")
                .color(NamedTextColor.DARK_AQUA));

        sender.sendMessage(Component.text("         NAMELESS-S2")
                .color(NamedTextColor.AQUA)
                .decoration(TextDecoration.BOLD, true));

        sender.sendMessage(Component.text("      Season 2 Custom Items")
                .color(NamedTextColor.GRAY)
                .decoration(TextDecoration.ITALIC, true));

        sender.sendMessage(Component.empty());

        sender.sendMessage(Component.text("  Version: ")
                .color(NamedTextColor.GRAY)
                .append(Component.text(NamelessS2.getInstance().getDescription().getVersion())
                        .color(NamedTextColor.GREEN)));

        sender.sendMessage(Component.empty());

        sender.sendMessage(Component.text("  Developer: ")
                .color(NamedTextColor.GRAY)
                .append(Component.text("DeQuackDealer")
                        .color(NamedTextColor.GOLD)
                        .decoration(TextDecoration.BOLD, true)));

        sender.sendMessage(Component.empty());

        Component githubLink = Component.text("  GitHub: ")
                .color(NamedTextColor.GRAY)
                .append(Component.text("DeQuackDealer/NamelessSMPPlugin")
                        .color(NamedTextColor.AQUA)
                        .decoration(TextDecoration.UNDERLINED, true)
                        .clickEvent(ClickEvent.openUrl("https://github.com/DeQuackDealer/NamelessSMPPlugin"))
                        .hoverEvent(HoverEvent.showText(Component.text("Click to open GitHub")
                                .color(NamedTextColor.YELLOW))));
        sender.sendMessage(githubLink);

        Component discordInfo = Component.text("  Discord: ")
                .color(NamedTextColor.GRAY)
                .append(Component.text("dequackdea1er")
                        .color(NamedTextColor.BLUE)
                        .hoverEvent(HoverEvent.showText(Component.text("Add me on Discord!")
                                .color(NamedTextColor.LIGHT_PURPLE))));
        sender.sendMessage(discordInfo);

        sender.sendMessage(Component.empty());

        sender.sendMessage(Component.text("  Features:")
                .color(NamedTextColor.WHITE)
                .decoration(TextDecoration.BOLD, true));
        
        sender.sendMessage(Component.text("   - Lifesteal Sword")
                .color(NamedTextColor.DARK_RED));
        sender.sendMessage(Component.text("   - Swap Rod")
                .color(NamedTextColor.GOLD));
        sender.sendMessage(Component.text("   - Diamond Apple (Diamond Hearts)")
                .color(NamedTextColor.AQUA));
        sender.sendMessage(Component.text("   - Upgradeable Spear System")
                .color(NamedTextColor.GREEN));

        sender.sendMessage(Component.empty());

        Component hireMe = Component.text("  Open for commissions! DM on Discord")
                .color(NamedTextColor.LIGHT_PURPLE)
                .decoration(TextDecoration.ITALIC, true);
        sender.sendMessage(hireMe);

        sender.sendMessage(Component.empty());

        Component helpHint = Component.text("  Use ")
                .color(NamedTextColor.DARK_GRAY)
                .append(Component.text("/custompluginauthor items")
                        .color(NamedTextColor.YELLOW))
                .append(Component.text(" for item info")
                        .color(NamedTextColor.DARK_GRAY));
        sender.sendMessage(helpHint);

        sender.sendMessage(Component.text("\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550")
                .color(NamedTextColor.DARK_AQUA));

        sender.sendMessage(Component.empty());

        return true;
    }

    private void showItemsHelp(CommandSender sender) {
        sender.sendMessage(Component.empty());
        
        sender.sendMessage(Component.text("\u2550\u2550\u2550 CUSTOM ITEMS \u2550\u2550\u2550")
                .color(NamedTextColor.GOLD)
                .decoration(TextDecoration.BOLD, true));

        sender.sendMessage(Component.empty());

        sender.sendMessage(Component.text("LIFESTEAL SWORD")
                .color(NamedTextColor.DARK_RED)
                .decoration(TextDecoration.BOLD, true));
        sender.sendMessage(Component.text("  Heals 50% of damage dealt")
                .color(NamedTextColor.GRAY));
        sender.sendMessage(Component.text("  9 Attack Damage, 3 Knockback")
                .color(NamedTextColor.GRAY));
        sender.sendMessage(Component.text("  25s cooldown after 10 hits")
                .color(NamedTextColor.DARK_GRAY));
        sender.sendMessage(Component.text("  Recipe: 2 Netherite Ingots + Blaze Rod")
                .color(NamedTextColor.YELLOW));

        sender.sendMessage(Component.empty());

        sender.sendMessage(Component.text("SWAP ROD")
                .color(NamedTextColor.GOLD)
                .decoration(TextDecoration.BOLD, true));
        sender.sendMessage(Component.text("  Swaps positions with hooked entities")
                .color(NamedTextColor.GRAY));
        sender.sendMessage(Component.text("  Recipe: 2 Ender Pearls + Fishing Rod")
                .color(NamedTextColor.YELLOW));

        sender.sendMessage(Component.empty());

        sender.sendMessage(Component.text("DIAMOND APPLE")
                .color(NamedTextColor.AQUA)
                .decoration(TextDecoration.BOLD, true));
        sender.sendMessage(Component.text("  Grants 3 Diamond Hearts that absorb damage")
                .color(NamedTextColor.GRAY));
        sender.sendMessage(Component.text("  When a heart is fully depleted, repairs 100 armor durability")
                .color(NamedTextColor.GRAY));
        sender.sendMessage(Component.text("  Also grants Regen III, Resistance II, Strength II, Fire Res")
                .color(NamedTextColor.DARK_GRAY));
        sender.sendMessage(Component.text("  Recipe: 8 Diamond Blocks + Golden Apple")
                .color(NamedTextColor.YELLOW));

        sender.sendMessage(Component.empty());

        sender.sendMessage(Component.text("UPGRADEABLE SPEAR")
                .color(NamedTextColor.GREEN)
                .decoration(TextDecoration.BOLD, true));
        sender.sendMessage(Component.text("  Upgrades through tiers by killing players")
                .color(NamedTextColor.GRAY));
        sender.sendMessage(Component.text("  Wood -> Copper -> Iron -> Diamond -> Netherite")
                .color(NamedTextColor.GRAY));
        sender.sendMessage(Component.text("  Dying with a spear downgrades it one tier")
                .color(NamedTextColor.RED));
        sender.sendMessage(Component.text("  Given to new players on first join")
                .color(NamedTextColor.YELLOW));

        sender.sendMessage(Component.empty());
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return Arrays.asList("items", "reload");
        }
        return List.of();
    }
}
