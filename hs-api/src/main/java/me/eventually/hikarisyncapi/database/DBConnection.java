package me.eventually.hikarisyncapi.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A wrapped class for database connection.
 * @author Eventually
 */
public class DBConnection {
    private final DataSource dataSource;
    private String currentTable;

    public DBConnection(String host, int port, String database,
                        String username, String password, boolean useSSL) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database +
                "?useSSL=" + useSSL);
        config.setUsername(username);
        config.setPassword(password);
        config.setMaximumPoolSize(10);  // 连接池配置
        config.setMinimumIdle(2);
        this.dataSource = new HikariDataSource(config);
    }

    // 切换当前操作表
    public void setTable(String tableName) {
        this.currentTable = tableName;
    }

    // 通用查询方法
    public List<Object[]> query(String sql, Object... params) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            setParameters(pstmt, params);
            ResultSet rs = pstmt.executeQuery();

            List<Object[]> results = new ArrayList<>();
            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();

            while (rs.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 0; i < columnCount; i++) {
                    row[i] = rs.getObject(i + 1);
                }
                results.add(row);
            }
            return results;
        }
    }

    public int execute(String sql, Object... params) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            setParameters(pstmt, params);
            return pstmt.executeUpdate();
        }
    }

    // 快捷方法（使用当前表）
    public List<Object[]> select(String whereClause) throws SQLException {
        return query("SELECT * FROM " + currentTable + " WHERE " + whereClause);
    }

    public int insert(Object... values) throws SQLException {
        String placeholders = String.join(",",
                java.util.Collections.nCopies(values.length, "?"));
        return execute("INSERT INTO " + currentTable + " VALUES(" + placeholders + ")", values);
    }

    private void setParameters(PreparedStatement pstmt, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            pstmt.setObject(i + 1, params[i]);
        }
    }

    public void transaction(Runnable task) throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try {
                task.run();
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        }
    }
    public HikariDataSource getDataSource() {
        return (HikariDataSource) dataSource;
    }
}