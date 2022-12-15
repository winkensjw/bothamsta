package org.winkensjw.server.db;

import org.jboss.logging.Logger;
import org.jooq.Record;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.winkensjw.platform.configuration.BothamstaServerProperties.DbJdbcUrlProperty;
import org.winkensjw.platform.configuration.BothamstaServerProperties.DbPasswordProperty;
import org.winkensjw.platform.configuration.BothamstaServerProperties.DbUserNameProperty;
import org.winkensjw.platform.configuration.util.CONFIG;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB {

    private static final Logger LOG = Logger.getLogger(DB.class);

    private DB() {
    }

    protected static Connection getConnection() throws SQLException {
        String userName = CONFIG.get(DbUserNameProperty.class);
        String password = CONFIG.get(DbPasswordProperty.class);
        String url = CONFIG.get(DbJdbcUrlProperty.class);
        return DriverManager.getConnection(url, userName, password);
    }

    protected static Result<Record> connectAndFetch(String sql) {
        try (Connection conn = getConnection()) {
            DSLContext context = DSL.using(conn, SQLDialect.POSTGRES);
            return context.fetch(sql);
        } catch (SQLException e) {
            LOG.error("Error creating database connection!", e);
            throw new RuntimeException(e);
        }
    }

    public static Result<Record> list(String sql) {
        return connectAndFetch(sql);
    }

    public static Record uniqueResult(String sql) {
        Result<Record> result = connectAndFetch(sql);
        return result.isEmpty() ? null : result.get(0);
    }

    public static SelectSelectStep<Record> select() {
        return DSL.using(SQLDialect.POSTGRES).select();
    }
}
