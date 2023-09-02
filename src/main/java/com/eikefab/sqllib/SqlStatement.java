package com.eikefab.sqllib;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Used to database CRUD
 */
public class SqlStatement {

    private final Connection connection;

    public interface AdaptConsumer<T> {

        void accept(T value) throws SQLException;

    }

    protected SqlStatement(Connection connection) {
        this.connection = connection;
    }

    public Connection getConnection() {
        return connection;
    }

    /**
     * Executes a update query on the database
     *
     * @param query the sql query
     * @param consumer the consumer to use PreparedStatement methods
     */
    public void update(String query, AdaptConsumer<PreparedStatement> consumer) {
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            consumer.accept(statement);

            statement.executeUpdate();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Executes a update query on the database
     *
     * @param query the sql query
     */
    public void update(String query) {
        update(query, (statement) -> {});
    }

    /**
     * Executes a batch update on the database
     *
     * @param query the sql query
     * @param adapter the batch adapter
     * @param collection the data to adapt into the batch
     * @return the executed batch count
     */
    public <T> int[] batch(String query, SqlBatchAdapter<T> adapter, Collection<T> collection) {
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            for (T value : collection) {
                adapter.batch(value, statement);

                statement.addBatch();
            }

            return statement.executeBatch();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return new int[0];
    }

    /**
     * Executes a query on the database and allows to consume its result set
     *
     * @param query the sql query
     * @param statementConsumer the statement consumer
     * @param resultConsumer the result set consumer
     */
    public void query(String query, AdaptConsumer<PreparedStatement> statementConsumer, AdaptConsumer<ResultSet> resultConsumer) {
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statementConsumer.accept(statement);

            try (ResultSet resultSet = statement.executeQuery()) {
                resultConsumer.accept(resultSet);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * @param query the sql query
     * @param consumer the statement consumer
     * @param adapterClass the adapter .class
     * @return The first row generated by sql query adapted using the provided SqlResultAdapter or null if it doesn't provide any
     * @param <T> the generic value
     */
    public <T> T resultOne(String query, AdaptConsumer<PreparedStatement> consumer, Class<? extends SqlResultAdapter<T>> adapterClass) {
        try {
            final SqlResultAdapter<T> adapter = adapterClass.newInstance();
            final AtomicReference<T> value = new AtomicReference<>();

            query(
                    query,
                    consumer,
                    (resultSet) -> {
                        if (!resultSet.next()) {
                            return;
                        }

                        value.set(adapter.adapt(resultSet));
                    }
            );

            return value.get();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return null;
    }

    /**
     * @param query the sql query
     * @param consumer the statement consumer
     * @param adapterClass the adapter .class
     * @return A set containing all rows that the sql query could provide
     * @param <T> the generic value
     */
    public <T> Set<T> resultMany(String query, AdaptConsumer<PreparedStatement> consumer, Class<? extends SqlResultAdapter<T>> adapterClass) {
        final Set<T> set = new HashSet<>();

        try {
            final SqlResultAdapter<T> adapter = adapterClass.newInstance();

            query(
                    query,
                    consumer,
                    (resultSet) -> {
                        while (resultSet.next()) {
                            final T value = adapter.adapt(resultSet);

                            set.add(value);
                        }
                    }
            );
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return set;
    }

}
