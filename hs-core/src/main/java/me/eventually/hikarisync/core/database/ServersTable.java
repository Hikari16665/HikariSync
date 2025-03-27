package me.eventually.hikarisync.core.database;

import me.eventually.hikarisyncapi.database.AbstractDBTable;

import java.util.List;

public class ServersTable extends AbstractDBTable {

    public ServersTable(String tableName) {
        super(tableName);
    }

    @Override
    protected List<DBField> getFields() {
        return List.of(
                new DBField(true),
                new DBField("instance_id", "VARCHAR(36)").setUnique(true),
                new DBField("created_at", "TIMESTAMP").setNullable(false),
                new DBField("status ENUM('ACTIVE','INACTIVE') NOT NULL DEFAULT 'ACTIVE'"),
                new DBField("last_heartbeat", "TIMESTAMP").setNullable(false).setDefaultValue("CURRENT_TIMESTAMP")
        );
    }
}
