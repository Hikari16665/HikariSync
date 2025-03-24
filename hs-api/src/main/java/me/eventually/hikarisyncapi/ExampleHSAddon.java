package me.eventually.hikarisyncapi;

import me.eventually.hikarisyncapi.database.AbstractDBTable;
import me.eventually.hikarisyncapi.database.DBConnection;
import me.eventually.hikarisyncapi.structure.LoadReason;
import me.eventually.hikarisyncapi.structure.SaveReason;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.SQLException;
import java.util.List;

/**
 * Example HSAddon, includes data table definition, save and load methods, and init and disable methods.
 * You can simply extend / copy this class to create your own addon.
 * Javadocs are in {@link HSAddon}, see that for more info.
 * @author Eventually
 */
@ParametersAreNonnullByDefault
public class ExampleHSAddon extends HSAddon {

    @Override
    public String getAddonName() {
        return "example";
    }

    @Override
    public String getAddonVersion() {
        return "1.0.114514";
    }
    @Override
    public List<? extends AbstractDBTable> getTableDefinitions() {
        return List.of(
            new ExampleDBTable("example")
        );
    }

    @Override
    public void saveData(OfflinePlayer p, DBConnection db, SaveReason reason) {
        String uuid = p.getUniqueId().toString();
        String data = "example data";
        try {
            db.transaction(() -> {
                db.insert(
                        "example",
                        uuid,
                        data
                );
                return null;
                }
            );
        } catch (SQLException ignored) {}
    }

    @Override
    public void loadData(OfflinePlayer p, DBConnection db, LoadReason reason) {
        /*
         * Do something
         * see method saveData for example
         */
    }

    @Override
    public void init(DBConnection db) {
        // do something
        Bukkit.getLogger().info("Example Addon loaded");
    }

    @Override
    public void disable(DBConnection db) {
        // do something
        Bukkit.getLogger().info("Example Addon disabled");
    }

    @Override
    public List<LoadReason> getLoadReasons() {
        // return the list of LoadReason that you care about
        return List.of(
                LoadReason.PLAYER_JOIN
        );
    }

    @Override
    public List<SaveReason> getSaveReasons() {
        // return the list of SaveReason that you care about
        return List.of(
                SaveReason.PLAYER_QUIT,
                SaveReason.TIMED_SAVE
        );
    }

    static class ExampleDBTable extends AbstractDBTable {
        public ExampleDBTable(String tableName) {
            super(tableName);
        }

        @Override
        protected List<DBField> getFields() {
            return List.of(
                    new DBField(true),
                    new DBField("uuid", "VARCHAR(36)"),
                    new DBField("data", "TEXT")
            );
        }
    }
}
