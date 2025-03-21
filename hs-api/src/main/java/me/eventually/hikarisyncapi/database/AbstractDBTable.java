package me.eventually.hikarisyncapi.database;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

/**
 * A table definition for the database.
 * tableName is the name of the table.
 * Must implement getFields()
 * @author Eventually
 */
@ParametersAreNonnullByDefault
public abstract class AbstractDBTable {
    protected String tableName;
    protected String tablePrefix = "hs_";
    public AbstractDBTable(String tableName) {
        this.tableName = tableName;
    }
    protected abstract List<DBField> getFields();

    protected static class DBField {
        private String name;
        private String type;
        private Boolean nullable;
        private Boolean primaryKey;
        private Boolean isId = false;
        public DBField(Boolean isId){
            this.isId = isId;
        }
        public DBField(String name, String type) {
            this.name = name;
            this.type = type;
        }
        public DBField setPrimaryKey(Boolean primaryKey) {
            this.primaryKey = primaryKey;
            return this;
        }

        public DBField setNullable(Boolean nullable) {
            this.nullable = nullable;
            return this;
        }

        public DBField setType(String type) {
            this.type = type;
            return this;
        }

        public Boolean getId() {
            return isId;
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }
        public Boolean getNullable() {
            return nullable;
        }

        public Boolean getPrimaryKey() {
            return primaryKey;
        }
    }
    public String generateCreateTableScript() throws IllegalStateException{
        StringBuilder script = new StringBuilder();
        script
                .append("CREATE TABLE IF NOT EXISTS ")
                .append(tablePrefix)
                .append(tableName)
                .append(" (");
        int i = 0;
        for (DBField field : getFields()) {
            i++;
            if (field.getId()) {
                if (i != 1){
                    throw new IllegalArgumentException("The id key should be first.");
                }
                script.append("id INT AUTO_INCREMENT PRIMARY KEY, ");
            } else {
                script.append(field.getName()).append(" ").append(field.getType());
                if (field.getPrimaryKey()) {
                    script.append(" PRIMARY KEY");
                }
                if (!field.getNullable()) {
                    script.append(" NOT NULL");
                }
                script.append(", ");
            }
        }
        script.delete(script.length() - 2, script.length());
        script.append(");");
        return script.toString();
    }
    public String getTableName() {
        return tableName;
    }
}
