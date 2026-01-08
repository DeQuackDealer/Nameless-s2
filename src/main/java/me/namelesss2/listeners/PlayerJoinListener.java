package me.namelesss2.listeners;

import me.namelesss2.items.Spear;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerJoinListener implements Listener {

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!player.hasPlayedBefore()) {
            giveStarterSpear(player);
        }
    }

    private void giveStarterSpear(Player player) {
        ItemStack spear = Spear.create(Spear.Tier.WOOD);
        player.getInventory().addItem(spear);

        player.sendMessage(Component.text("Welcome! You have received a ")
                .color(NamedTextColor.GREEN)
                .append(Component.text("Wooden Spear")
                        .color(NamedTextColor.GOLD))
                .append(Component.text("!")
                        .color(NamedTextColor.GREEN)));

        player.sendMessage(Component.text("Kill players to upgrade your spear!")
                .color(NamedTextColor.GRAY));
    }
}
