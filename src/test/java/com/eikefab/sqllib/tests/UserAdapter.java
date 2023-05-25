package com.eikefab.sqllib.tests;

import com.eikefab.sqllib.SqlResultAdapter;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserAdapter implements SqlResultAdapter<User> {

    @Override
    public User adapt(ResultSet resultSet) throws SQLException {
        final int id = resultSet.getInt("id");
        final String name = resultSet.getNString("name");

        return new User(id, name);
    }

}
