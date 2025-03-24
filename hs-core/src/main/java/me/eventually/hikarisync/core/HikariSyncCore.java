package me.eventually.hikarisync.core;

import com.zaxxer.hikari.HikariDataSource;
import me.eventually.hikarisync.core.command.HikariSyncCommand;
import me.eventually.hikarisync.core.database.ServersTable;
import me.eventually.hikarisync.core.database.UUIDTable;
import me.eventually.hikarisync.core.listener.PlayerListener;
import me.eventually.hikarisync.core.listener.ServerListener;
import me.eventually.hikarisync.core.logging.Banner;
import me.eventually.hikarisync.core.manager.AddonManager;
import me.eventually.hikarisync.core.task.TimedSaveTask;
import me.eventually.hikarisyncapi.database.DBConnection;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public final class HikariSyncCore extends JavaPlugin {
    private static HikariSyncCore instance;
    private static AddonManager addonManager;
    private static DBConnection db;
    private static TimedSaveTask saveTask;
    private static boolean isCoreReady = false;
    private static boolean debug = false;
    private static boolean full_debug = false;

    public static HikariSyncCore getInstance() {
        return instance;
    }

    public static AddonManager getAddonManager() {
        return addonManager;
    }

    public static boolean isDebug() {
        return debug;
    }

    public static boolean isFull_debug() {
        return full_debug;
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
        debug = getConfig().getBoolean("debug");
        full_debug = getConfig().getBoolean("full_debug");

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

        saveTask = new TimedSaveTask(this);

        // now wait for server starting finished
        Bukkit.getPluginManager().registerEvents(new ServerListener(this), this);

    }
    public void initCore() {
        Bukkit.getLogger().info("Loading addons...");
        // Init all addons
        if (!addonManager.loadAddons(Bukkit.getPluginManager().getPlugins())) {
            getLogger().severe("No addons found, plugin will shut down and no features present.");
            Bukkit.getPluginManager().disablePlugin(this);
        }else{
            Bukkit.getLogger().info("Loaded " + addonManager.getAddonClasses().size() + " addons.");
        }
        // Auto Save
        int autoSaveInterval = getConfig().getInt("save.auto_save");
        if (autoSaveInterval > 0) {
            saveTask.runTaskTimer(this, 0, 20L * autoSaveInterval);
        }
        // Register PlayerListener
        Bukkit.getPluginManager().registerEvents(new PlayerListener(
                getConfig().getBoolean("load.on_join"),
                getConfig().getBoolean("save.on_quit"),
                getConfig().getBoolean("save.on_inventory_close"),
                getConfig().getBoolean("save.on_death")
        ), this);
        // All things are ready!
        Bukkit.getLogger().info("HikariSync enabled.");
        isCoreReady = true;
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
            // establish connection
            db = new DBConnection(
                    getConfig().getString("mysql.host"),
                    getConfig().getInt("mysql.port"),
                    getConfig().getString("mysql.database"),
                    getConfig().getString("mysql.username"),
                    getConfig().getString("mysql.password"),
                    getConfig().getBoolean("mysql.usessl")
            );
            // create table for uuid storage (to check player join status)
            db.execute(new UUIDTable("hs_core_uuid").generateCreateTableScript());
            // create table for server storage (random uuid for each server)
            db.execute(new ServersTable("hs_core_servers").generateCreateTableScript());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void onDisable() {
        //delete all entries from table hs_core_uuid
        try {
            getDb().execute("DELETE FROM hs_core_uuid");
        } catch (SQLException ignored) {
        }
        addonManager.disableAddons();
    }
}
