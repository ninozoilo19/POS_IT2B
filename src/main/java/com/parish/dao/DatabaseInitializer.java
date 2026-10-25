package com.parish.dao;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class DatabaseInitializer {
    public static void initializeDatabase() {
        String sql = """
            CREATE TABLE IF NOT EXISTS users (
                user_id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT UNIQUE NOT NULL,
                password_hash TEXT NOT NULL,
                full_name TEXT NOT NULL,
                role TEXT NOT NULL CHECK (role IN ('ADMIN', 'STAFF', 'PRIEST')),
                created_at DATETIME DEFAULT CURRENT_TIMESTAMP
            );

            CREATE TABLE IF NOT EXISTS parishioners (
                parishioner_id INTEGER PRIMARY KEY AUTOINCREMENT,
                first_name TEXT NOT NULL,
                last_name TEXT NOT NULL,
                middle_name TEXT,
                date_of_birth TEXT,
                contact_number TEXT,
                address TEXT,
                created_at DATETIME DEFAULT CURRENT_TIMESTAMP
            );

            CREATE TABLE IF NOT EXISTS sacramental_records (
                record_id INTEGER PRIMARY KEY AUTOINCREMENT,
                parishioner_id INTEGER NOT NULL,
                sacrament_type TEXT NOT NULL CHECK (sacrament_type IN ('BAPTISM', 'CONFIRMATION', 'MARRIAGE', 'FUNERAL')),
                sacrament_date TEXT NOT NULL,
                officiating_priest TEXT,
                godparents_or_witnesses TEXT,
                registry_book_number INTEGER,
                registry_page_number INTEGER,
                FOREIGN KEY (parishioner_id) REFERENCES parishioners(parishioner_id) ON DELETE CASCADE
            );

            CREATE TABLE IF NOT EXISTS activity_logs (
                log_id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER,
                action_type TEXT NOT NULL CHECK (action_type IN ('LOGIN', 'LOGOUT', 'CREATE', 'UPDATE', 'DELETE', 'CONFIG_CHANGE')),
                module_affected TEXT,
                description TEXT,
                timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL
            );

            INSERT OR IGNORE INTO users (user_id, username, password_hash, full_name, role) 
            VALUES (1, 'admin', 'admin123', 'System Administrator', 'ADMIN');
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}