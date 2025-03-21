package me.eventually.hikarisyncapi.database;

import java.util.List;

public abstract class AbstractDBKey {
    protected String tableName;
    public AbstractDBKey(String tableName) {
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
        public void setPrimaryKey(Boolean primaryKey) {
            this.primaryKey = primaryKey;
        }

        public void setNullable(Boolean nullable) {
            this.nullable = nullable;
        }

        public void setType(String type) {
            this.type = type;
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
    public String generateCreateTableScript() {
        StringBuilder script = new StringBuilder();
        script
                .append("CREATE TABLE IF NOT EXISTS ")
                .append(tableName)
                .append(" (");
        for (DBField field : getFields()) {
            if (field.getId()) {
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
}
