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

public final class SwapRod {

    public static final String ITEM_ID = "swap_rod";
    public static final int MAX_DURABILITY = 2031;

    private SwapRod() {}

    public static ItemStack create() {
        ItemStack item = new ItemStack(Material.WARPED_FUNGUS_ON_A_STICK);
        ItemMeta meta = item.getItemMeta();

        Component displayName = Component.text("Swap Rod")
                .color(NamedTextColor.DARK_PURPLE)
                .decoration(TextDecoration.ITALIC, false);
        meta.displayName(displayName);

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
                " B ",
                "N  "
        );

        recipe.setIngredient('N', Material.NETHERITE_INGOT);
        recipe.setIngredient('B', Material.BLAZE_ROD);

        return recipe;
    }

    public static boolean isSwapRod(ItemStack item) {
        return ItemUtils.isCustomItem(item, ITEM_ID);
    }
}
