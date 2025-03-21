package me.eventually.hikarisyncapi;

import me.eventually.hikarisyncapi.database.AbstractDBTable;
import me.eventually.hikarisyncapi.database.DBConnection;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.annotation.Documented;
import java.util.ArrayList;
import java.util.List;

/**
 * Main abstract class for the addon.
 * Addon should define its own table definitions in the {@link #getTableDefinitions()} method.
 * Core will automatically load the addon and call the methods in this class.
 * <br>
 * {@link #loadData(OfflinePlayer, DBConnection)} will be called by core when the player joins the server.
 * <br>
 * {@link #saveData(OfflinePlayer, DBConnection)} will be called by core when the player leaves the server, closed inventory, died, and every X seconds that server owners can configure.
 * @author Eventually
 */
@ParametersAreNonnullByDefault
public abstract class HSAddon extends JavaPlugin {

    public abstract String getAddonName();
    public abstract String getAddonVersion();

    /**
     * This is the table definition for the database.
     * It is used to create the table in the database when the addon is loaded by core.
     * @return List of{@link AbstractDBTable}
     */
    public abstract List<AbstractDBTable> getTableDefinitions();
    /**
     * Store your data to the table in the database, will be called by core automatically.
     * @param p {@link OfflinePlayer} to be stored
     */
    public abstract void saveData(OfflinePlayer p, DBConnection db);

    /**
     * Load your data from the table in the database, will be called by core automatically.
     * @param p {@link OfflinePlayer} to be loaded
     */
    public abstract void loadData(OfflinePlayer p, DBConnection db);
    /**
     * Init your addon, will be called when core is ready.
     * If you are addon developer, make sure your addon has loaded before core.
     * Add this line to plugin.yml of your addon ↓
     * <br>  <code>loadbefore: [HikariSync-Core]</code>
     */
    public abstract void init(DBConnection db);
    /**
     * Disable your addon, will be called when core is disabled or cannot run table init scripts.
     */
    public abstract void disable(DBConnection db);
}
