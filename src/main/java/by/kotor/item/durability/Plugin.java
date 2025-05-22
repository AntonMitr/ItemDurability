package by.kotor.item.durability;

import by.kotor.item.durability.event.DurabilityListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class Plugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("Plugin enabled");
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new DurabilityListener(this), this);

    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin disabled");
    }
}
