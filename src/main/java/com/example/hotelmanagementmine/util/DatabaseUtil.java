package com.example.hotelmanagementmine.util;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseUtil {
    private static final Dotenv dotenv = Dotenv.configure()
            .directory(".")
            .ignoreIfMissing()
            .load();

    private static final String DB_HOST = dotenv.get("DB_HOST", "localhost");
    private static final String DB_PORT = dotenv.get("DB_PORT", "3306");
    private static final String DB_NAME = dotenv.get("DB_NAME", "hotel_management");
    private static final String DB_USER = dotenv.get("DB_USER", "root");
    private static final String DB_PASSWORD = dotenv.get("DB_PASSWORD", "");

    private static final String URL = String.format("jdbc:mysql://%s:%s/%s",
            DB_HOST, DB_PORT, DB_NAME);
    private static final String BASE_URL = String.format("jdbc:mysql://%s:%s/",
            DB_HOST, DB_PORT);

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, DB_USER, DB_PASSWORD);
    }

    public static void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(BASE_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {

            // Create database if not exists
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
            stmt.executeUpdate("USE " + DB_NAME);

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
