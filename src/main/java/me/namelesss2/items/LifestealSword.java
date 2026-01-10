package me.namelesss2.items;

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

public final class LifestealSword {

    public static final String ITEM_ID = "lifesteal_sword";
    public static final int MAX_DURABILITY = 2031;
    public static final double ATTACK_DAMAGE = 7.0;
    public static final double ATTACK_SPEED = 1.6;
    public static final double ATTACK_KNOCKBACK = 1.0;

    private LifestealSword() {}

    public static ItemStack create() {
        ItemStack item = new ItemStack(Material.NETHERITE_SWORD);
        ItemMeta meta = item.getItemMeta();

        Component displayName = Component.text("Lifesteal Sword")
                .color(NamedTextColor.DARK_RED)
                .decoration(TextDecoration.ITALIC, false);
        meta.displayName(displayName);

        AttributeModifier damageModifier = new AttributeModifier(
                new NamespacedKey("nameless_s2", "lifesteal_damage"),
                ATTACK_DAMAGE,
                AttributeModifier.Operation.ADD_NUMBER,
                EquipmentSlotGroup.MAINHAND
        );
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, damageModifier);

        AttributeModifier speedModifier = new AttributeModifier(
                new NamespacedKey("nameless_s2", "lifesteal_speed"),
                ATTACK_SPEED,
                AttributeModifier.Operation.ADD_NUMBER,
                EquipmentSlotGroup.MAINHAND
        );
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, speedModifier);

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
