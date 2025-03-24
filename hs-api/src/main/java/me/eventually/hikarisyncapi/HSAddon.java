package me.eventually.hikarisyncapi;

import me.eventually.hikarisyncapi.database.AbstractDBTable;
import me.eventually.hikarisyncapi.database.DBConnection;
import me.eventually.hikarisyncapi.structure.LoadReason;
import me.eventually.hikarisyncapi.structure.SaveReason;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

/**
 * Main abstract class for the addon.
 * <br>
 * Addon should be a subclass of {@link HSAddon}.
 * <br>
 * Addon should define its own table definitions in the {@link #getTableDefinitions()} method.
 * <br>
 * Example Addon: {@link ExampleHSAddon}
 * <br>
 * Core will automatically load the addon and call the methods in this class.
 * <br>
 * {@link #loadData(OfflinePlayer, DBConnection, LoadReason)} will be called by core when the player joins the server.
 * <br>
 * {@link #saveData(OfflinePlayer, DBConnection, SaveReason)} will be called by core when the player leaves the server, closed inventory, died, and every X seconds that server owners can configure.
 * @author Eventually
 */
@ParametersAreNonnullByDefault
public abstract class HSAddon extends JavaPlugin {

    public abstract String getAddonName();
    public abstract String getAddonVersion();

    /**
     * This is the table definition for the database.
     * <br>
     * It is used to create the table in the database when the addon is loaded by core.
     * <br>
     * It's just a fast way to let the core manage the table creation,
     * <br>
     * But you can still return an empty list and do it manually if you want.
     * @return List of class extends{@link AbstractDBTable}
     */
    public abstract List<? extends AbstractDBTable> getTableDefinitions();
    /**
     * Store your data to the table in the database, will be called by core automatically.
     * @param p {@link OfflinePlayer} to be stored
     */
    public abstract void saveData(OfflinePlayer p, DBConnection db, SaveReason reason);

    /**
     * Load your data from the table in the database, will be called by core automatically.
     * @param p {@link OfflinePlayer} to be loaded
     */
    public abstract void loadData(OfflinePlayer p, DBConnection db, LoadReason reason);
    /**
     * Init your addon, will be called when core is ready.
     * This method will always be called before {@link #saveData(OfflinePlayer, DBConnection, SaveReason)} and {@link #loadData(OfflinePlayer, DBConnection, LoadReason)}
     */
    public abstract void init(DBConnection db);
    /**
     * Disable your addon, will be called when core is disabled or cannot run table init scripts.
     */
    public abstract void disable(DBConnection db);
    /**
     * Get the list of load reasons that your addon care about.
     * <br>
     * {@link #loadData(OfflinePlayer, DBConnection, LoadReason)} will be called with the reason.
     * <br>
     * If specific reason is not in the enum, you can use {@link DBConnection} in {@link #init(DBConnection)} method and EventListener.
     * @see LoadReason
     * @see #loadData(OfflinePlayer, DBConnection, LoadReason)
     * @return List of {@link LoadReason}
     */
    public abstract List<LoadReason> getLoadReasons();
    /**
     * Get the list of save reasons that your addon care about.
     * <br>
     * {@link #saveData(OfflinePlayer, DBConnection, SaveReason)} will be called with the reason.
     * <br>
     * If specific reason is not in the enum, you can use {@link DBConnection} in {@link #init(DBConnection)} method and EventListener.
     * @see SaveReason
     * @see #saveData(OfflinePlayer, DBConnection, SaveReason)
     * @return List of {@link SaveReason}
     */
    public abstract List<SaveReason> getSaveReasons();
}
