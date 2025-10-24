package com.example.hotelmanagementmine.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseUtil {
    private static final String URL = "jdbc:mysql://localhost:3306/hotel_management";
    private static final String USER = "root";
    private static final String PASSWORD = "1234";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/", USER, PASSWORD);
             Statement stmt = conn.createStatement()) {

            // Create database if not exists
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS hotel_management");
            stmt.executeUpdate("USE hotel_management");

            // Create rooms table
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS rooms (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    room_number VARCHAR(10) UNIQUE NOT NULL,
                    room_type VARCHAR(50) NOT NULL,
                    price DECIMAL(10, 2) NOT NULL,
                    status VARCHAR(20) DEFAULT 'AVAILABLE'
                )
            """);

            // Create guests table
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS guests (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(100) NOT NULL,
                    phone VARCHAR(20) NOT NULL,
                    email VARCHAR(100),
                    id_number VARCHAR(50) NOT NULL
                )
            """);

            // Create bookings table
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS bookings (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    guest_id INT NOT NULL,
                    room_id INT NOT NULL,
                    check_in_date DATE NOT NULL,
                    check_out_date DATE NOT NULL,
                    total_amount DECIMAL(10, 2) NOT NULL,
                    status VARCHAR(20) DEFAULT 'ACTIVE',
                    FOREIGN KEY (guest_id) REFERENCES guests(id),
                    FOREIGN KEY (room_id) REFERENCES rooms(id)
                )
            """);

            // Create users table
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS users (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    username VARCHAR(50) UNIQUE NOT NULL,
                    password VARCHAR(255) NOT NULL,
                    role VARCHAR(20) NOT NULL,
                    full_name VARCHAR(100) NOT NULL
                )
            """);

            // Insert default admin user if not exists
            stmt.executeUpdate("""
                INSERT IGNORE INTO users (username, password, role, full_name)
                VALUES ('admin', 'admin123', 'ADMIN', 'Administrator')
            """);

            // Insert default customer user if not exists
            stmt.executeUpdate("""
                INSERT IGNORE INTO users (username, password, role, full_name)
                VALUES ('customer', 'customer123', 'CUSTOMER', 'Test Customer')
            """);

            System.out.println("Database initialized successfully!");
        } catch (SQLException e) {
            System.err.println("Database initialization error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
