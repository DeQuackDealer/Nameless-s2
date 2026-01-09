package me.namelesss2.items;

import me.namelesss2.NamelessS2;
import me.namelesss2.utils.ItemUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public final class LifestealSword {

    public static final String ITEM_ID = "lifesteal_sword";
    public static final int MAX_DURABILITY = 2031;
    public static final double LIFESTEAL_PERCENT = 0.5;
    public static final int HITS_BEFORE_COOLDOWN = 10;
    public static final long COOLDOWN_DURATION_MS = 25000;
    public static final double ATTACK_DAMAGE = 9.0;
    public static final double ATTACK_KNOCKBACK = 3.0;

    private LifestealSword() {}

    public static ItemStack create() {
        ItemStack item = new ItemStack(Material.NETHERITE_SWORD);
        ItemMeta meta = item.getItemMeta();

        Component displayName = Component.text("Lifesteal Sword")
                .color(NamedTextColor.DARK_RED)
                .decoration(TextDecoration.ITALIC, false)
                .decoration(TextDecoration.BOLD, true);
        meta.displayName(displayName);

        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("Grants permanent hearts on player kills")
                .color(NamedTextColor.GRAY)
                .decoration(TextDecoration.ITALIC, false));
        meta.lore(lore);

        // Uses vanilla netherite sword model for now
        // TODO: Add custom model when resource pack is ready

        AttributeModifier damageModifier = new AttributeModifier(
                new NamespacedKey("nameless_s2", "lifesteal_damage"),
                ATTACK_DAMAGE,
                AttributeModifier.Operation.ADD_NUMBER,
                EquipmentSlotGroup.MAINHAND
        );
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, damageModifier);

        AttributeModifier knockbackModifier = new AttributeModifier(
                new NamespacedKey("nameless_s2", "lifesteal_knockback"),
                ATTACK_KNOCKBACK,
                AttributeModifier.Operation.ADD_NUMBER,
                EquipmentSlotGroup.MAINHAND
        );
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_KNOCKBACK, knockbackModifier);

        ItemUtils.setCustomItem(meta, ITEM_ID);

        item.setItemMeta(meta);
        return item;
    }

    public static ShapedRecipe getRecipe() {
        ItemStack result = create();
        NamespacedKey key = new NamespacedKey("nameless_s2", "lifesteal_sword_recipe");
        ShapedRecipe recipe = new ShapedRecipe(key, result);

        recipe.shape(
                " N ",
                " N ",
                " B "
        );

        recipe.setIngredient('N', Material.NETHERITE_INGOT);
        recipe.setIngredient('B', Material.BLAZE_ROD);

        return recipe;
    }

    public static boolean isLifestealSword(ItemStack item) {
        return ItemUtils.isCustomItem(item, ITEM_ID);
    }
}
