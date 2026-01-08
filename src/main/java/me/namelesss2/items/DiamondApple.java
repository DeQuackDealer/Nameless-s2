package me.namelesss2.items;

import me.namelesss2.utils.ItemUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public final class DiamondApple {

    public static final String ITEM_ID = "diamond_apple";

    private DiamondApple() {}

    public static ItemStack create() {
        ItemStack item = new ItemStack(Material.ENCHANTED_GOLDEN_APPLE);
        ItemMeta meta = item.getItemMeta();

        Component displayName = Component.text("Diamond Apple")
                .color(NamedTextColor.AQUA)
                .decoration(TextDecoration.ITALIC, false)
                .decoration(TextDecoration.BOLD, true);
        meta.displayName(displayName);

        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("Ultra Powerful")
                .color(NamedTextColor.LIGHT_PURPLE)
                .decoration(TextDecoration.ITALIC, true));
        lore.add(Component.text("Grants incredible buffs")
                .color(NamedTextColor.GRAY)
                .decoration(TextDecoration.ITALIC, false));
        meta.lore(lore);

        ItemUtils.setCustomItem(meta, ITEM_ID);

        item.setItemMeta(meta);
        return item;
    }

    public static ShapedRecipe getRecipe() {
        ItemStack result = create();
        NamespacedKey key = new NamespacedKey("nameless_s2", "diamond_apple_recipe");
        ShapedRecipe recipe = new ShapedRecipe(key, result);

        recipe.shape(
                "DDD",
                "DGD",
                "DDD"
        );

        recipe.setIngredient('D', Material.DIAMOND_BLOCK);
        recipe.setIngredient('G', Material.GOLDEN_APPLE);

        return recipe;
    }

    public static boolean isDiamondApple(ItemStack item) {
        return ItemUtils.isCustomItem(item, ITEM_ID);
    }
}
