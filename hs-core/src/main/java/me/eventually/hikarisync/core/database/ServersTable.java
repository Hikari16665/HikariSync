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
                new DBField("server_uuid", "VARCHAR(36)")
        );
    }
}
