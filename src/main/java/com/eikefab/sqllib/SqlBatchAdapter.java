package com.eikefab.sqllib;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface SqlBatchAdapter<T> {

    /**
     * Passes the value into sql statements
     *
     * @param value the value itself
     * @param statement the sql statement passed by the parent
     * @throws SQLException if anything goes wrong with the statement
     */
    void batch(T value, PreparedStatement statement) throws SQLException;

}
