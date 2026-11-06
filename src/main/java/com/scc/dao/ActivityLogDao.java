package com.scc.dao;

import com.scc.model.ActivityLog;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ActivityLogDao {

    // INSERT ACTIVITY LOG
    public void logAction(
            int userId,
            String actionType,
            String description
    ) throws SQLException {

        String sql = """
                INSERT INTO activity_logs
                (user_id, action_type, description)
                VALUES (?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);
            statement.setString(2, actionType);
            statement.setString(3, description);

            statement.executeUpdate();
        }
    }

    // GET ALL ACTIVITY LOGS
    public List<ActivityLog> getAllLogs() throws SQLException {

        String sql = """
                SELECT id, user_id, action_type,
                       description, timestamp
                FROM activity_logs
                ORDER BY timestamp DESC, id DESC
                """;

        List<ActivityLog> logs = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                logs.add(readLog(result));
            }
        }

        return logs;
    }

    // GET LOGS BY USER ID
    public List<ActivityLog> getLogsByUserId(int userId)
            throws SQLException {

        String sql = """
                SELECT id, user_id, action_type,
                       description, timestamp
                FROM activity_logs
                WHERE user_id = ?
                ORDER BY timestamp DESC, id DESC
                """;

        List<ActivityLog> logs = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);

            try (ResultSet result = statement.executeQuery()) {

                while (result.next()) {
                    logs.add(readLog(result));
                }
            }
        }

        return logs;
    }

    // CLEAR ALL HISTORICAL LOGS
    public int clearAllLogs() throws SQLException {

        String sql = "DELETE FROM activity_logs";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            return statement.executeUpdate();
        }
    }

    // READ DATABASE RESULT
    private ActivityLog readLog(ResultSet result)
            throws SQLException {

        return new ActivityLog(
                result.getInt("id"),
                result.getInt("user_id"),
                result.getString("action_type"),
                result.getString("description"),
                result.getString("timestamp")
        );
    }
}