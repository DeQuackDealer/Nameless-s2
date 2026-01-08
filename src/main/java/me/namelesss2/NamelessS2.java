package me.namelesss2;

import me.namelesss2.commands.CreditCommand;
import me.namelesss2.commands.GiveCommand;
import me.namelesss2.items.DiamondApple;
import me.namelesss2.items.LifestealSword;
import me.namelesss2.items.Spear;
import me.namelesss2.items.SwapRod;
import me.namelesss2.listeners.DiamondAppleListener;
import me.namelesss2.listeners.LifestealSwordListener;
import me.namelesss2.listeners.PlayerJoinListener;
import me.namelesss2.listeners.SpearListener;
import me.namelesss2.listeners.SwapRodListener;
import me.namelesss2.managers.DiamondHeartsManager;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public final class NamelessS2 extends JavaPlugin {

    private static NamelessS2 instance;

    public static final NamespacedKey CUSTOM_ITEM_KEY = new NamespacedKey("nameless_s2", "custom_item");
    public static final NamespacedKey LIFESTEAL_HITS_KEY = new NamespacedKey("nameless_s2", "lifesteal_hits");
    public static final NamespacedKey LIFESTEAL_COOLDOWN_KEY = new NamespacedKey("nameless_s2", "lifesteal_cooldown");
    public static final NamespacedKey SPEAR_TIER_KEY = new NamespacedKey("nameless_s2", "spear_tier");
    public static final NamespacedKey SPEAR_KILLS_KEY = new NamespacedKey("nameless_s2", "spear_kills");

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        registerRecipes();
        registerListeners();
        registerCommands();

        getLogger().info("=================================");
        getLogger().info("Nameless-s2 v" + getDescription().getVersion());
        getLogger().info("Season 2 Custom Items Loaded!");
        getLogger().info("=================================");
    }

    @Override
    public void onDisable() {
        DiamondHeartsManager.getInstance().clearAll();
        getLogger().info("Nameless-s2 has been disabled!");
    }

    private void registerRecipes() {
        getServer().addRecipe(LifestealSword.getRecipe());
        getServer().addRecipe(SwapRod.getRecipe());
        getServer().addRecipe(DiamondApple.getRecipe());
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new LifestealSwordListener(), this);
        getServer().getPluginManager().registerEvents(new SwapRodListener(), this);
        getServer().getPluginManager().registerEvents(new DiamondAppleListener(), this);
        getServer().getPluginManager().registerEvents(new SpearListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
    }

    private void registerCommands() {
        CreditCommand creditCommand = new CreditCommand();
        getCommand("custompluginauthor").setExecutor(creditCommand);
        getCommand("custompluginauthor").setTabCompleter(creditCommand);

        GiveCommand giveCommand = new GiveCommand();
        getCommand("ns").setExecutor(giveCommand);
        getCommand("ns").setTabCompleter(giveCommand);
    }

    public static NamelessS2 getInstance() {
        return instance;
    }
}
