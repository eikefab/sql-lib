package com.eikefab.sqllib.tests;

import com.eikefab.sqllib.SqlLib;
import com.eikefab.sqllib.SqlStatement;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class UserTest {

    private static final SqlLib SQL_LIB = SqlLib.memory();
    private static final SqlStatement STATEMENT = SQL_LIB.getStatement();

    @BeforeAll
    public static void createEntry() {
        STATEMENT.update("CREATE TABLE `users` (`id` INTEGER NOT NULL, `name` VARCHAR(16) NOT NULL);");
        STATEMENT.update("INSERT INTO `users` VALUES ('1', 'eike');");
        STATEMENT.update("INSERT INTO `users` VALUES ('2', 'fabricio');");
    }

    @Test
    public void testOneAdapter() {
        final User user = STATEMENT.resultOne(
                "SELECT `id`, `name` FROM `users` WHERE `id` = ?;",
                (statement) -> {
                    statement.setInt(1, 1);
                },
                UserAdapter.class
        );

        Assertions.assertNotNull(user);
        Assertions.assertEquals(1, user.getId());
        Assertions.assertEquals("eike", user.getName());
    }

    @Test
    public void testManyAdapter() {
        final Set<User> user = STATEMENT.resultMany(
                "SELECT `id`, `name` FROM `users`;",
                (statement) -> {},
                UserAdapter.class
        );

        Assertions.assertTrue(user.size() > 1);
    }

    @Test
    public void testQueryRaw() {
        STATEMENT.query(
                "SELECT `id`, `name` FROM `users` WHERE `id` = ?",
                (statement) -> {
                    statement.setInt(1, 1);
                },
                (resultSet) -> {
                    if (!resultSet.next()) {
                        Assertions.fail();

                        return;
                    }

                    final int id = resultSet.getInt("id");
                    final String name = resultSet.getString("name");

                    Assertions.assertEquals(1, id);
                    Assertions.assertEquals("eike", name);
                }
        );
    }

    @Test
    public void testBatch() {
        final List<User> users = new ArrayList<>();

        for (int id = 3; id < 100; id++) {
            final String name = Double.toHexString(Math.random()).substring(0, 16);
            final User user = new User(id, name);

            users.add(user);
        }

        final int[] batches = STATEMENT.batch(
                "INSERT INTO `users` (`id`, `name`) VALUES (?, ?)",
                (value, statement) -> {
                    statement.setInt(1, value.getId());
                    statement.setString(2, value.getName());
                },
                users
        );

        Assertions.assertEquals(97, batches.length);
    }

    @AfterAll
    public static void drop() {
        STATEMENT.update("DROP TABLE `users`;");
        SQL_LIB.disconnect();
    }

}
