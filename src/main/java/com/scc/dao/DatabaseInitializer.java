package com.scc.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    public void initialize() throws SQLException {

        String usersTable = """
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT NOT NULL UNIQUE,
                    password TEXT NOT NULL,
                    roles TEXT NOT NULL DEFAULT 'STUDENT'
                        CHECK (
                            roles IN (
                                'ADMIN',
                                'STAFF',
                                'STUDENT',
                                'TEACHER'
                            )
                        ),
                    status TEXT NOT NULL DEFAULT 'PENDING'
                )
                """;

        String activityLogsTable = """
                CREATE TABLE IF NOT EXISTS activity_logs (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    action_type TEXT NOT NULL,
                    description TEXT NOT NULL,
                    timestamp TEXT DEFAULT CURRENT_TIMESTAMP,

                    FOREIGN KEY (user_id)
                    REFERENCES users(id)
                    ON DELETE CASCADE
                )
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {

            statement.execute(usersTable);
            statement.execute(activityLogsTable);

            statement.execute("""
                    CREATE INDEX IF NOT EXISTS idx_activity_logs_user_id
                    ON activity_logs(user_id)
                    """);

            System.out.println("Database initialized successfully.");
            System.out.println("Users table ready.");
            System.out.println("Activity logs table ready.");
        }
    }
}