package me.namelesss2.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class CreditCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        sender.sendMessage(Component.empty());

        sender.sendMessage(Component.text("═══════════════════════════════")
                .color(NamedTextColor.DARK_PURPLE));

        sender.sendMessage(Component.text("  Nameless-s2")
                .color(NamedTextColor.LIGHT_PURPLE)
                .decoration(TextDecoration.BOLD, true));

        sender.sendMessage(Component.empty());

        sender.sendMessage(Component.text("  Made by ")
                .color(NamedTextColor.GRAY)
                .append(Component.text("DeQuackDealer")
                        .color(NamedTextColor.GOLD)
                        .decoration(TextDecoration.BOLD, true)));

        sender.sendMessage(Component.empty());

        sender.sendMessage(Component.text("  GitHub: ")
                .color(NamedTextColor.GRAY)
                .append(Component.text("https://github.com/DeQuackDealer")
                        .color(NamedTextColor.AQUA)
                        .clickEvent(ClickEvent.openUrl("https://github.com/DeQuackDealer"))));

        sender.sendMessage(Component.text("  Discord: ")
                .color(NamedTextColor.GRAY)
                .append(Component.text("dequackdea1er")
                        .color(NamedTextColor.BLUE)));

        sender.sendMessage(Component.empty());

        sender.sendMessage(Component.text("  Open for hire! Message me on Discord!")
                .color(NamedTextColor.GREEN)
                .decoration(TextDecoration.ITALIC, true));

        sender.sendMessage(Component.text("═══════════════════════════════")
                .color(NamedTextColor.DARK_PURPLE));

        sender.sendMessage(Component.empty());

        return true;
    }
}
