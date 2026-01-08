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

public final class SwapRod {

    public static final String ITEM_ID = "swap_rod";
    public static final int MAX_DURABILITY = 2031;

    private SwapRod() {}

    public static ItemStack create() {
        ItemStack item = new ItemStack(Material.FISHING_ROD);
        ItemMeta meta = item.getItemMeta();

        Component displayName = Component.text("Swap Rod")
                .color(NamedTextColor.DARK_PURPLE)
                .decoration(TextDecoration.ITALIC, false)
                .decoration(TextDecoration.BOLD, true);
        meta.displayName(displayName);

        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("Boogie Woogie!")
                .color(NamedTextColor.LIGHT_PURPLE)
                .decoration(TextDecoration.ITALIC, true));
        lore.add(Component.text("Swap positions with hooked entities")
                .color(NamedTextColor.GRAY)
                .decoration(TextDecoration.ITALIC, false));
        meta.lore(lore);

        ItemUtils.setCustomItem(meta, ITEM_ID);

        item.setItemMeta(meta);
        return item;
    }

    public static ShapedRecipe getRecipe() {
        ItemStack result = create();
        NamespacedKey key = new NamespacedKey("nameless_s2", "swap_rod_recipe");
        ShapedRecipe recipe = new ShapedRecipe(key, result);

        recipe.shape(
                "  N",
                " NS",
                "N S"
        );

        recipe.setIngredient('N', Material.NETHERITE_INGOT);
        recipe.setIngredient('S', Material.STRING);

        return recipe;
    }

    public static boolean isSwapRod(ItemStack item) {
        return ItemUtils.isCustomItem(item, ITEM_ID);
    }
}
