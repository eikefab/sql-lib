package com.eikefab.sqllib.tests;

import com.eikefab.sqllib.SqlLib;
import com.eikefab.sqllib.SqlStatement;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

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

        Assertions.assertEquals(2, user.size());
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

    @AfterAll
    public static void drop() {
        STATEMENT.update("DROP TABLE `users`;");
        SQL_LIB.disconnect();
    }

}
