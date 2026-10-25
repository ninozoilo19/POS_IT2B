package com.parish.dao;

import com.parish.model.SacramentalRecord;
import com.parish.model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ParishSystemDAO {

    public User login(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password_hash = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("full_name"),
                        rs.getString("role")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean addParishionerAndRecord(String fName, String lName, String dob, String contact,
                                           String type, String date, String priest, int book, int page) {
        String parishionerSql = "INSERT INTO parishioners (first_name, last_name, date_of_birth, contact_number) VALUES (?, ?, ?, ?)";
        String recordSql = "INSERT INTO sacramental_records (parishioner_id, sacrament_type, sacrament_date, officiating_priest, registry_book_number, registry_page_number) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement pStmt = conn.prepareStatement(parishionerSql, Statement.RETURN_GENERATED_KEYS)) {
                pStmt.setString(1, fName);
                pStmt.setString(2, lName);
                pStmt.setString(3, dob);
                pStmt.setString(4, contact);
                pStmt.executeUpdate();

                ResultSet rs = pStmt.getGeneratedKeys();
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
            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
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
}