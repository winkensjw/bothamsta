package org.winkensjw.platform.db;

import org.jboss.logging.Logger;
import org.jooq.Record;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.winkensjw.platform.configuration.BothamstaProperties.DbJdbcUrlProperty;
import org.winkensjw.platform.configuration.BothamstaProperties.DbPasswordProperty;
import org.winkensjw.platform.configuration.BothamstaProperties.DbUserNameProperty;
import org.winkensjw.platform.configuration.util.CONFIG;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.function.Function;

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

    protected static <V> V callWithConnection(Function<DSLContext, V> func) {
        try (Connection conn = getConnection()) {
            DSLContext context = DSL.using(conn, SQLDialect.POSTGRES);
            return func.apply(context);
        } catch (SQLException e) {
            LOG.error("Error creating database connection!", e);
            throw new RuntimeException(e);
        }
    }

    public static <R extends Record> Result<R> list(ResultQuery<R> query) {
        return callWithConnection(ctx -> ctx.fetch(query));
    }

    public static Record uniqueResult(String sql) {
        return callWithConnection(ctx -> ctx.fetchOne(sql));
    }

    public static int count(Table<?> table) {
        return callWithConnection(ctx -> ctx.fetchCount(table));
    }

    public static void insertInto(TableRecord<?> record) {
        callWithConnection(ctx -> ctx.executeInsert(record));
    }

    public static DSLContext createQuery() {
        return DSL.using(SQLDialect.POSTGRES);
    }
}
