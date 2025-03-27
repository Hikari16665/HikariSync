package me.eventually.hikarisync.core.database;

import me.eventually.hikarisyncapi.database.AbstractDBTable;

import java.util.List;

public class UUIDTable extends AbstractDBTable {
    public UUIDTable(String tableName) {
        super(tableName);
    }

    @Override
    protected List<DBField> getFields() {
        return List.of(
                new DBField("uuid", "VARCHAR(36)").setPrimaryKey(true),
                new DBField("instance_id", "VARCHAR(36)").setNullable(false),
                new DBField("FOREIGN KEY (instance_id) REFERENCES hs_core_servers(instance_id)")
        );
    }

}
