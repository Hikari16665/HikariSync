package me.eventually.hikarisync.core;

import me.eventually.hikarisyncapi.database.DBConnection;
import me.eventually.hikarisyncapi.HSAddon;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class HikariSyncCore extends JavaPlugin {
    private static HikariSyncCore instance;
    private static final List<HSAddon> addons = new ArrayList<>();
    private static DBConnection db;
    private static boolean isCoreReady = false;

    public static HikariSyncCore getInstance() {
        return instance;
    }

    public static DBConnection getDb() {
        return db;
    }

    public static boolean isCoreReady() {
        return isCoreReady;
    }

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        if (!initDbConnection()) {
            getLogger().severe("Failed to connect to database, plugin will shut down.");
        }

        initCore();
        isCoreReady = true;

    }
    public void initCore() {
        Plugin[] plugins = Bukkit.getPluginManager().getPlugins();
        Arrays.stream(plugins).forEach(plugin -> {
            if (plugin instanceof HSAddon addon){
                addons.add(addon);
            }
        });

        if (addons.isEmpty()) {
            getLogger().severe("No addons found, plugin will shut down and no features present.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        initAddons();
    }

    private void initAddons() {
        addons.forEach(addon -> {
            addon.init(db);
            addon.getTableDefinitions().forEach(table -> {
                try {
                    String createTableScript = table.generateCreateTableScript();
                    db.transaction(() -> {
                        try {
                            db.execute(createTableScript);
                        } catch (SQLException e) {
                            throw new RuntimeException();
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    getLogger().severe("Failed to create table %s for addon %s, disabling addon.".formatted(table.getTableName(), addon.getAddonName()));
                    addon.disable(db);
                }
            });
        });
    }
    private void disableAddons() {
        addons.forEach(addon -> {
            addon.disable(db);
        });
    }
    private boolean initDbConnection() {
        try{
            db = new DBConnection(
                    getConfig().getString("mysql.host"),
                    getConfig().getInt("mysql.port"),
                    getConfig().getString("mysql.database"),
                    getConfig().getString("mysql.user"),
                    getConfig().getString("mysql.password"),
                    getConfig().getBoolean("mysql.usessl")
            );
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void onDisable() {
        disableAddons();
    }


}
