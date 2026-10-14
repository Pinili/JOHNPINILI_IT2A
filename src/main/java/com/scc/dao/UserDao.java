
package dao;

import model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {


    public boolean register(String username, String password, String role)
            throws SQLException {

        if (username == null || username.isBlank()
                || password == null || password.isBlank()) {
            return false;
        }

        if (!role.equals("TEACHER") && !role.equals("STUDENT")) {
            return false;
        }

        String sql = "INSERT INTO users (username, password, role, status) "
                + "VALUES (?, ?, ?, 'PENDING')";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username.trim());
            stmt.setString(2, password);
            stmt.setString(3, role);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            if (e.getErrorCode() == 19) {
                return false;
            }
            throw e;
        }
    }


    public User login(String username, String password)
            throws SQLException {

        String sql = "SELECT id, username, password, role, status "
                + "FROM users WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username.trim());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String storedPassword = rs.getString("password");

                    if (password.equals(storedPassword)) {
                        return new User(
                                rs.getInt("id"),
                                rs.getString("username"),
                                storedPassword,
                                rs.getString("role"),
                                rs.getString("status")
                        );
                    }
                }
            }
        }

        return null;
    }
}
