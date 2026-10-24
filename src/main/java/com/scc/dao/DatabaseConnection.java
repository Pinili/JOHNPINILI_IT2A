
package com.scc.dao;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    private static final String DATABASE_URL =
            "jdbc:sqlite:school_property.db";

    private DatabaseConnection() {
    }

    public static Connection getConnection() throws SQLException {
        Connection connection =
                DriverManager.getConnection(DATABASE_URL);

        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
        } catch (SQLException e) {
            connection.close();
            throw e;
        }

        return connection;
    }

    public static void testConnection() {
        try (Connection connection = getConnection()) {
            System.out.println("Database connected successfully!");
            System.out.println(
                    "Database path: "
                            + Path.of("school_property.db")
                            .toAbsolutePath()
            );
        } catch (SQLException e) {
            System.err.println(
                    "Database connection failed: " + e.getMessage()
            );
        }
    }
}
