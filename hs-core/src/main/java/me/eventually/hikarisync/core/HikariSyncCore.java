package me.eventually.hikarisync.core;

import com.zaxxer.hikari.HikariDataSource;
import me.eventually.hikarisync.core.command.HikariSyncCommand;
import me.eventually.hikarisync.core.logging.Banner;
import me.eventually.hikarisync.core.manager.AddonManager;
import me.eventually.hikarisyncapi.database.DBConnection;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class HikariSyncCore extends JavaPlugin {
    private static HikariSyncCore instance;
    private static AddonManager addonManager;
    private static DBConnection db;
    private static boolean isCoreReady = false;

    public static HikariSyncCore getInstance() {
        return instance;
    }

    public static AddonManager getAddonManager() {
        return addonManager;
    }

    public DBConnection getDb() {
        return db;
    }

    public static boolean isCoreReady() {
        return isCoreReady;
    }

    @Override
    public void onEnable() {
        instance = this;

        Banner.printBanner(getLogger());

        addonManager = new AddonManager(this);

        saveDefaultConfig();

        Bukkit.getPluginCommand("hikarisync").setTabCompleter(new HikariSyncCommand());
        Bukkit.getPluginCommand("hikarisync").setExecutor(new HikariSyncCommand());

        if (!initDbConnection()) {
            getLogger().severe("Failed to connect to database, plugin will shut down.");
            getLogger().severe("Use \"/hikarisync reconnect\" to reconnect your database.");
            return;
        }

        initCore();
        isCoreReady = true;
    }
    public void initCore() {
        if (!addonManager.loadAddons(Bukkit.getPluginManager().getPlugins())) {
            getLogger().severe("No addons found, plugin will shut down and no features present.");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }
    public boolean checkDbReady() {
        if (getDb() == null) return false;
        HikariDataSource db = getDb().getDataSource();
        return db != null && db.isRunning();
    }
    public boolean initDbConnection() {
        if (checkDbReady()) {
            db.getDataSource().close();
            db = null;
            return false;
        }

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
        addonManager.disableAddons();
    }
}
