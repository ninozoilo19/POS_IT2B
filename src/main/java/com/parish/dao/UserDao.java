package com.parish.dao;

import com.parish.model.SacramentalRecord;
import com.parish.model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDao {
    private final ActivityLogDao logDao = new ActivityLogDao();

    public User login(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password_hash = ?";
        User loggedInUser = null;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    loggedInUser = new User(
                            rs.getInt("user_id"),
                            rs.getString("username"),
                            rs.getString("full_name"),
                            rs.getString("role")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (loggedInUser != null) {
            logDao.logAction(loggedInUser.getUserId(), "LOGIN", "User " + loggedInUser.getUsername() + " logged in.");
        }
        return loggedInUser;
    }

    public void logout(User user) {
        if (user != null) {
            logDao.logAction(user.getUserId(), "LOGOUT", "User " + user.getUsername() + " logged out.");
        }
    }

    public boolean registerUser(String username, String password, String fullName, String role, int adminUserId) {
        String sql = "INSERT INTO users (username, password_hash, full_name, role) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, fullName);
            stmt.setString(4, role);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        logDao.logAction(adminUserId, "CREATE", "Registered new user account: " + username + " (Role: " + role + ")");
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteUser(int targetUserId, int adminUserId) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, targetUserId);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                logDao.logAction(adminUserId, "DELETE", "Deleted user account ID: " + targetUserId);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean addParishionerAndRecord(User currentUser, String fName, String lName, String dob, String contact,
                                           String type, String date, String priest, int book, int page) {
        String parishionerSql = "INSERT INTO parishioners (first_name, last_name, date_of_birth, contact_number) VALUES (?, ?, ?, ?)";
        String recordSql = "INSERT INTO sacramental_records (parishioner_id, sacrament_type, sacrament_date, officiating_priest, registry_book_number, registry_page_number) VALUES (?, ?, ?, ?, ?, ?)";

        boolean success = false;
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement pStmt = conn.prepareStatement(parishionerSql, Statement.RETURN_GENERATED_KEYS)) {
                pStmt.setString(1, fName);
                pStmt.setString(2, lName);
                pStmt.setString(3, dob);
                pStmt.setString(4, contact);
                pStmt.executeUpdate();

                try (ResultSet rs = pStmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int parishionerId = rs.getInt(1);

                        try (PreparedStatement rStmt = conn.prepareStatement(recordSql)) {
                            rStmt.setInt(1, parishionerId);
                            rStmt.setString(2, type);
                            rStmt.setString(3, date);
                            rStmt.setString(4, priest);
                            rStmt.setInt(5, book);
                            rStmt.setInt(6, page);
                            rStmt.executeUpdate();
                        }
                    }
                }
            }
            conn.commit();
            success = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (success) {
            logDao.logAction(currentUser.getUserId(), "CREATE", "Created " + type + " record for parishioner " + fName + " " + lName);
        }
        return success;
    }

    public List<SacramentalRecord> getAllRecords() {
        List<SacramentalRecord> list = new ArrayList<>();
        String sql = "SELECT * FROM sacramental_records";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new SacramentalRecord(
                        rs.getInt("record_id"),
                        rs.getInt("parishioner_id"),
                        rs.getString("sacrament_type"),
                        rs.getString("sacrament_date"),
                        rs.getString("officiating_priest")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT user_id, username, full_name, role FROM users";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                users.add(new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("full_name"),
                        rs.getString("role")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
}