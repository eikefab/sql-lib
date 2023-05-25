package com.eikefab.sqllib;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Used to adapt results at SqlStatement#resultOne and SqlStatement#resultMany
 * @param <T> the generic reference
 */
public interface SqlResultAdapter<T> {

    /**
     * Uses the resultSet and adapt its data to the generic value
     *
     * @param resultSet the sql result set
     * @return the <T> data
     * @throws SQLException if anything goes wrong with resultSet
     */
    T adapt(ResultSet resultSet) throws SQLException;

}
