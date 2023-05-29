package com.eikefab.sqllib;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Used to handle connections
 */
public final class SqlLib {

    private final Connection connection;
    private final SqlStatement statement;

    /**
     * Returns a valid SqlLib instance
     *
     * @param connection the sql connection, required to be not null
     */
    private SqlLib(Connection connection) {
        this.connection = Objects.requireNonNull(connection);
        this.statement = new SqlStatement(connection);
    }

    /**
     *
     * @return the connection
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Allows to consume the connection
     * @param consumer the consumer
     */
    public void consume(Consumer<Connection> consumer) {
        consumer.accept(connection);
    }

    /**
     * Check if the database connection isn't closed and then disconnect.
     */
    public void disconnect() {
        try {
            if (connection.isClosed()) {
                connection.close();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Created only for tests purposes
     *
     * @return a SqlLib instance using an H2 database connection
     */
    public static SqlLib memory() {
        try {
            Class.forName("org.h2.Driver");

            return new SqlLib(DriverManager.getConnection("jdbc:h2:mem:"));
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return null;
    }

    /**
     * Creates a SQLite connection
     *
     * @param file the file ending in sqlite or db extensions
     * @return a SqlLib instance using an SQLite database connection
     */
    public static SqlLib connect(File file) {
        try {
            Class.forName("org.sqlite.JDBC");

            if (!file.exists()) {
                file.createNewFile();
            }

            return new SqlLib(DriverManager.getConnection("jdbc:sqlite:" + file));
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return null;
    }

    /**
     * Creates a MySQL connection
     *
     * @param username the mysql username
     * @param password the mysql password
     * @param database the mysql database
     * @param server the mysql address or so
     * @return a SqlLib instance using an MySQL database connection
     */
    public static SqlLib connect(String username, String password, String database, String server) {
        try {
            Class.forName("com.mysql.jdbc.Driver");

            final String url = String.format("jdbc:mysql://%s/%s?autoReconnect=true", server, database);

            return new SqlLib(DriverManager.getConnection(url, username, password));
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return null;
    }

    public static SqlLib of(Connection connection) {
        return new SqlLib(connection);
    }

    public SqlStatement getStatement() {
        return statement;
    }

}
