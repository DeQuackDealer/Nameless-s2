package me.namelesss2.items;

import me.namelesss2.NamelessS2;
import me.namelesss2.utils.ItemUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class Spear {

    public static final String ITEM_ID = "custom_spear";
    public static final NamespacedKey MAX_DURABILITY_KEY = new NamespacedKey("nameless_s2", "spear_max_durability");

    public enum Tier {
        WOOD(0, "Wooden Spear", NamedTextColor.GOLD, 250, Material.OAK_PLANKS, 1),
        COPPER(1, "Copper Spear", NamedTextColor.GOLD, 250, Material.COPPER_INGOT, 2),
        IRON(2, "Iron Spear", NamedTextColor.WHITE, 250, Material.IRON_INGOT, 3),
        DIAMOND(3, "Diamond Spear", NamedTextColor.AQUA, 1561, Material.DIAMOND, 4),
        NETHERITE(4, "Netherite Spear", NamedTextColor.DARK_RED, 2031, Material.NETHERITE_INGOT, -1);

        private final int ordinalValue;
        private final String displayName;
        private final NamedTextColor color;
        private final int durability;
        private final Material repairMaterial;
        private final int killsToUpgrade;

        Tier(int ordinalValue, String displayName, NamedTextColor color, int durability, Material repairMaterial, int killsToUpgrade) {
            this.ordinalValue = ordinalValue;
            this.displayName = displayName;
            this.color = color;
            this.durability = durability;
            this.repairMaterial = repairMaterial;
            this.killsToUpgrade = killsToUpgrade;
        }

        public int getOrdinalValue() { return ordinalValue; }
        public String getDisplayName() { return displayName; }
        public NamedTextColor getColor() { return color; }
        public int getDurability() { return durability; }
        public Material getRepairMaterial() { return repairMaterial; }
        public int getKillsToUpgrade() { return killsToUpgrade; }

        public Tier getNextTier() {
            return switch (this) {
                case WOOD -> COPPER;
                case COPPER -> IRON;
                case IRON -> DIAMOND;
                case DIAMOND -> NETHERITE;
                case NETHERITE -> null;
            };
        }

        public Tier getPreviousTier() {
            return switch (this) {
                case WOOD -> null;
                case COPPER -> WOOD;
                case IRON -> COPPER;
                case DIAMOND -> IRON;
                case NETHERITE -> DIAMOND;
            };
        }

        public static Tier fromOrdinal(int ordinal) {
            for (Tier tier : values()) {
                if (tier.ordinalValue == ordinal) return tier;
            }
            return WOOD;
        }
    }

    private Spear() {}

    public static ItemStack create(Tier tier) {
        ItemStack item = new ItemStack(Material.TRIDENT);
        updateSpear(item, tier, 0);
        return item;
    }

    public static ShapedRecipe getWoodenSpearRecipe() {
        ItemStack result = create(Tier.WOOD);
        NamespacedKey key = new NamespacedKey("nameless_s2", "wooden_spear_recipe");
        ShapedRecipe recipe = new ShapedRecipe(key, result);

        recipe.shape(
                "  D",
                " S ",
                "S  "
        );

        recipe.setIngredient('D', Material.DIAMOND);
        recipe.setIngredient('S', Material.STICK);

        return recipe;
    }

    public static void updateSpear(ItemStack item, Tier tier, int kills) {
        Map<Enchantment, Integer> enchants = item.getEnchantments();
        
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        meta.displayName(Component.text(tier.getDisplayName())
                .color(tier.getColor())
                .decoration(TextDecoration.ITALIC, false)
                .decoration(TextDecoration.BOLD, true));

        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("Tier: " + tier.name())
                .color(NamedTextColor.GRAY)
                .decoration(TextDecoration.ITALIC, false));

        if (tier.getNextTier() != null) {
            lore.add(Component.text("Kills: " + kills + "/" + tier.getKillsToUpgrade())
                    .color(NamedTextColor.DARK_GRAY)
                    .decoration(TextDecoration.ITALIC, false));
            lore.add(Component.text("Next: " + tier.getNextTier().getDisplayName())
                    .color(NamedTextColor.GREEN)
                    .decoration(TextDecoration.ITALIC, false));
        } else {
            lore.add(Component.text("MAX TIER")
                    .color(NamedTextColor.GOLD)
                    .decoration(TextDecoration.ITALIC, false)
                    .decoration(TextDecoration.BOLD, true));
        }

        lore.add(Component.text("Repair with: " + formatMaterialName(tier.getRepairMaterial()))
                .color(NamedTextColor.DARK_GRAY)
                .decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("Melee only - cannot be thrown")
                .color(NamedTextColor.DARK_GRAY)
                .decoration(TextDecoration.ITALIC, true));

        meta.lore(lore);

        ItemUtils.setCustomItem(meta, ITEM_ID);
        meta.getPersistentDataContainer().set(NamelessS2.SPEAR_TIER_KEY, PersistentDataType.INTEGER, tier.getOrdinalValue());
        meta.getPersistentDataContainer().set(NamelessS2.SPEAR_KILLS_KEY, PersistentDataType.INTEGER, kills);
        meta.getPersistentDataContainer().set(MAX_DURABILITY_KEY, PersistentDataType.INTEGER, tier.getDurability());

        if (meta instanceof Damageable damageable) {
            damageable.setDamage(0);
        }

        item.setItemMeta(meta);
        
        for (Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
            item.addUnsafeEnchantment(entry.getKey(), entry.getValue());
        }
    }

    private static String formatMaterialName(Material material) {
        String name = material.name().replace("_", " ");
        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = true;
        for (char c : name.toLowerCase().toCharArray()) {
            if (capitalizeNext) {
                result.append(Character.toUpperCase(c));
                capitalizeNext = false;
            } else {
                result.append(c);
            }
            if (c == ' ') capitalizeNext = true;
        }
        return result.toString();
    }

    public static boolean isSpear(ItemStack item) {
        return ItemUtils.isCustomItem(item, ITEM_ID);
    }

    public static Tier getTier(ItemStack item) {
        return Tier.fromOrdinal(ItemUtils.getIntData(item, NamelessS2.SPEAR_TIER_KEY, 0));
    }

    public static int getKills(ItemStack item) {
        return ItemUtils.getIntData(item, NamelessS2.SPEAR_KILLS_KEY, 0);
    }

    public static int getMaxDurability(ItemStack item) {
        return ItemUtils.getIntData(item, MAX_DURABILITY_KEY, 250);
    }
}
