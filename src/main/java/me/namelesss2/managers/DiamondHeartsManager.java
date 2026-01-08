package me.namelesss2.managers;

import me.namelesss2.NamelessS2;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DiamondHeartsManager {

    private static DiamondHeartsManager instance;
    private final Map<UUID, Double> diamondHealth = new ConcurrentHashMap<>();
    private final Map<UUID, BukkitRunnable> actionBarTasks = new ConcurrentHashMap<>();

    private static final double HP_PER_HEART = 2.0;

    private DiamondHeartsManager() {}

    public static DiamondHeartsManager getInstance() {
        if (instance == null) {
            instance = new DiamondHeartsManager();
        }
        return instance;
    }

    public void addDiamondHearts(Player player, int hearts) {
        UUID uuid = player.getUniqueId();
        double currentHealth = diamondHealth.getOrDefault(uuid, 0.0);
        double newHealth = currentHealth + (hearts * HP_PER_HEART);
        diamondHealth.put(uuid, newHealth);
        
        startActionBarTask(player);
        updateActionBar(player);
    }

    public double getDiamondHealth(Player player) {
        return diamondHealth.getOrDefault(player.getUniqueId(), 0.0);
    }

    public int getDiamondHearts(Player player) {
        return (int) Math.ceil(getDiamondHealth(player) / HP_PER_HEART);
    }

    public boolean hasDiamondHealth(Player player) {
        return getDiamondHealth(player) > 0;
    }

    public double absorbDamage(Player player, double damage) {
        UUID uuid = player.getUniqueId();
        double currentHealth = diamondHealth.getOrDefault(uuid, 0.0);
        
        if (currentHealth <= 0) {
            return damage;
        }

        int heartsBefore = (int) Math.ceil(currentHealth / HP_PER_HEART);
        
        double absorbed = Math.min(damage, currentHealth);
        double remaining = damage - absorbed;
        double newHealth = currentHealth - absorbed;
        
        int heartsAfter = (int) Math.ceil(newHealth / HP_PER_HEART);
        int heartsLost = heartsBefore - heartsAfter;
        
        if (heartsLost > 0) {
            int durabilityPerPiece = NamelessS2.getInstance().getConfig()
                    .getInt("diamond-apple.diamond-hearts.durability-repair-per-piece", 100);
            
            for (int i = 0; i < heartsLost; i++) {
                repairArmor(player, durabilityPerPiece);
            }
            
            player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 1.0f, 1.5f);
        }
        
        if (newHealth <= 0) {
            diamondHealth.remove(uuid);
            stopActionBarTask(player);
            player.sendMessage(Component.text("Your Diamond Hearts have been depleted!")
                    .color(NamedTextColor.AQUA));
        } else {
            diamondHealth.put(uuid, newHealth);
            updateActionBar(player);
        }
        
        return remaining;
    }

    private void repairArmor(Player player, int amount) {
        PlayerInventory inv = player.getInventory();
        ItemStack[] armorContents = inv.getArmorContents();
        
        for (int i = 0; i < armorContents.length; i++) {
            ItemStack armor = armorContents[i];
            if (armor == null || armor.getType() == Material.AIR) continue;
            
            ItemMeta meta = armor.getItemMeta();
            if (meta instanceof Damageable damageable) {
                int currentDamage = damageable.getDamage();
                int newDamage = Math.max(0, currentDamage - amount);
                damageable.setDamage(newDamage);
                armor.setItemMeta(meta);
            }
        }
        
        inv.setArmorContents(armorContents);
    }

    private void updateActionBar(Player player) {
        double health = getDiamondHealth(player);
        if (health <= 0) return;
        
        int fullHearts = (int) (health / HP_PER_HEART);
        boolean halfHeart = (health % HP_PER_HEART) > 0;
        
        StringBuilder heartDisplay = new StringBuilder();
        for (int i = 0; i < fullHearts; i++) {
            heartDisplay.append("\u2764 ");
        }
        if (halfHeart) {
            heartDisplay.append("\u2661 ");
        }
        
        player.sendActionBar(Component.text(heartDisplay.toString().trim())
                .color(NamedTextColor.AQUA));
    }

    private void startActionBarTask(Player player) {
        UUID uuid = player.getUniqueId();
        
        if (actionBarTasks.containsKey(uuid)) {
            return;
        }
        
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline() || !hasDiamondHealth(player)) {
                    stopActionBarTask(player);
                    return;
                }
                updateActionBar(player);
            }
        };
        
        task.runTaskTimer(NamelessS2.getInstance(), 0L, 20L);
        actionBarTasks.put(uuid, task);
    }

    private void stopActionBarTask(Player player) {
        UUID uuid = player.getUniqueId();
        BukkitRunnable task = actionBarTasks.remove(uuid);
        if (task != null) {
            task.cancel();
        }
        player.sendActionBar(Component.empty());
    }

    public void clearPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        diamondHealth.remove(uuid);
        stopActionBarTask(player);
    }

    public void clearAll() {
        diamondHealth.clear();
        for (BukkitRunnable task : actionBarTasks.values()) {
            task.cancel();
        }
        actionBarTasks.clear();
    }
}
