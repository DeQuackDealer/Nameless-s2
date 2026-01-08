package me.namelesss2.utils;

import me.namelesss2.NamelessS2;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public final class ItemUtils {

    private ItemUtils() {}

    public static boolean isCustomItem(ItemStack item, String itemId) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        String storedId = pdc.get(NamelessS2.CUSTOM_ITEM_KEY, PersistentDataType.STRING);
        return itemId.equals(storedId);
    }

    public static void setCustomItem(ItemMeta meta, String itemId) {
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(NamelessS2.CUSTOM_ITEM_KEY, PersistentDataType.STRING, itemId);
    }

    public static int getIntData(ItemStack item, org.bukkit.NamespacedKey key, int defaultValue) {
        if (item == null || !item.hasItemMeta()) {
            return defaultValue;
        }
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        Integer value = pdc.get(key, PersistentDataType.INTEGER);
        return value != null ? value : defaultValue;
    }

    public static long getLongData(ItemStack item, org.bukkit.NamespacedKey key, long defaultValue) {
        if (item == null || !item.hasItemMeta()) {
            return defaultValue;
        }
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        Long value = pdc.get(key, PersistentDataType.LONG);
        return value != null ? value : defaultValue;
    }

    public static void setIntData(ItemStack item, org.bukkit.NamespacedKey key, int value) {
        if (item == null) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(key, PersistentDataType.INTEGER, value);
        item.setItemMeta(meta);
    }

    public static void setLongData(ItemStack item, org.bukkit.NamespacedKey key, long value) {
        if (item == null) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(key, PersistentDataType.LONG, value);
        item.setItemMeta(meta);
    }
}
