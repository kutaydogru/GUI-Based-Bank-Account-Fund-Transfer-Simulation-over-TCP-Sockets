package server.dao;

import common.User;

import java.sql.*;

public class UserDAO {
    private String url = "jdbc:sqlite:server/db/bank.db";

    public boolean addUser(User user) {
        String sql = "INSERT INTO users(username, password, full_name) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getFullName());
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.out.println("Failed to add user: " + e.getMessage());
            return false;
        }
    }

    public User findUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("full_name")
                );
            }
        } catch (SQLException e) {
            System.out.println("Failed to find user: " + e.getMessage());
        }
        return null;
    }
}