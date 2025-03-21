package me.eventually.hikarisynccore;

import me.eventually.hikarisyncapi.HSAddon;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class HikariSyncCore extends JavaPlugin {
    private static HikariSyncCore instance;
    private static List<HSAddon> addons = new ArrayList<>();

    public static HikariSyncCore getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        Plugin[] plugins = Bukkit.getPluginManager().getPlugins();
        Arrays.stream(plugins).forEach(plugin -> {
            if (plugin instanceof HSAddon addon){
                addons.add(addon);
            }
        });
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
