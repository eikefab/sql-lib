package com.eikefab.sqllib.tests;

import com.eikefab.sqllib.SqlBatchAdapter;
import com.eikefab.sqllib.SqlResultAdapter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserAdapter implements SqlResultAdapter<User>, SqlBatchAdapter<User> {

    @Override
    public User adapt(ResultSet resultSet) throws SQLException {
        final int id = resultSet.getInt("id");
        final String name = resultSet.getNString("name");

        return new User(id, name);
    }

    @Override
    public void batch(User value, PreparedStatement statement) throws SQLException {
        statement.setInt(1, value.getId());
        statement.setString(2, value.getName());
    }

}
