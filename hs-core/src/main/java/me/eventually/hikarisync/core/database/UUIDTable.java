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
                new DBField("uuid", "VARCHAR(36)")
                        .setPrimaryKey(true)
        );
    }

}
