package me.eventually.hikarisyncapi.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * A wrapped class for database connection.
 * Use {@link #getDataSource()} to get the datasource, and you can use it to do anything you want
 * @author Eventually
 */
public class DBConnection {
    private final DataSource dataSource;

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


    /**
     * Common query method
     * @param sql SQL query command
     * @param params SQL query parameters
     * @return Query result
     * @throws SQLException handle it yourself
     */
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

    /**
     * Common sql command execute method
     * @param sql SQL command
     * @param params SQL command parameters
     * @return rows affected
     * @throws SQLException handle it yourself
     */
    public int execute(String sql, Object... params) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            setParameters(pstmt, params);
            return pstmt.executeUpdate();
        }
    }

    /**
     * Fast way to select data
     * @param tableName table name
     * @param whereClause sql where clause, like "id = 123456"
     * @param params SQL command parameters
     * @return query result
     * @throws SQLException handle it yourself
     */
    public List<Object[]> select(String tableName, String whereClause, Object... params) throws SQLException {
        return query("SELECT * FROM " + tableName + " WHERE " + whereClause, params);
    }

    /**
     * Fast way to insert data
     * @param tableName table name
     * @param values values to insert
     * @return rows affected
     * @throws SQLException handle it yourself
     */
    public int insert(String tableName, Object... values) throws SQLException {
        String placeholders = String.join(",",
                java.util.Collections.nCopies(values.length, "?"));
        return execute("INSERT INTO " + tableName + " VALUES(" + placeholders + ")", values);
    }

    /**
     * Set parameters to sql command
     * @param pstmt PreparedStatement
     * @param params parameters
     * @throws SQLException handle it yourself
     */
    private void setParameters(PreparedStatement pstmt, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            pstmt.setObject(i + 1, params[i]);
        }
    }

    /**
     * Fast way to do transaction
     * @param task task to run
     * @throws SQLException
     */
    public <T> T transaction(Callable<T> task) throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try {
                T result = task.call();
                conn.commit();
                return result;
            } catch (Exception e) {
                conn.rollback();
                throw new SQLException(e);
            }
        }
    }

    /**
     * Get the datasource, and you can use it to do anything you want
     * @return HikariDataSource
     */
    public HikariDataSource getDataSource() {
        return (HikariDataSource) dataSource;
    }
}