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

    // Local SQL Server defaults (.env overrides these)
    private static final String DB_HOST =
            dotenv.get("DB_HOST", "localhost");

    private static final String DB_PORT =
            dotenv.get("DB_PORT", "53473");

    private static final String DB_NAME =
            dotenv.get("DB_NAME", "hotel_management");

    private static final String DB_USER =
            dotenv.get("DB_USER", "admin");

    private static final String DB_PASSWORD =
            dotenv.get("DB_PASSWORD", "1234");

    // SQL Server JDBC options for local development
    // trustServerCertificate=true avoids SSL certificate validation issues
    private static final String JDBC_OPTIONS =
            ";encrypt=true;trustServerCertificate=true;loginTimeout=30;";

    // Connection URL for the target database
    private static final String URL = String.format(
            "jdbc:sqlserver://%s:%s;databaseName=%s%s",
            DB_HOST,
            DB_PORT,
            DB_NAME,
            JDBC_OPTIONS
    );

    // Connection URL without specifying database (used to create database)
    private static final String BASE_URL = String.format(
            "jdbc:sqlserver://%s:%s%s",
            DB_HOST,
            DB_PORT,
            JDBC_OPTIONS
    );

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, DB_USER, DB_PASSWORD);
    }

    public static void initializeDatabase() {
        try {
            // Create database if it doesn't exist
            try (Connection conn = DriverManager.getConnection(BASE_URL, DB_USER, DB_PASSWORD);
                 Statement stmt = conn.createStatement()) {

                stmt.executeUpdate(
                        "IF NOT EXISTS (SELECT 1 FROM sys.databases WHERE name = '" + DB_NAME + "') " +
                                "CREATE DATABASE " + DB_NAME
                );
            }

            // Connect to the database and create tables
            try (Connection conn = DriverManager.getConnection(URL, DB_USER, DB_PASSWORD);
                 Statement stmt = conn.createStatement()) {

                // Create rooms table
                stmt.executeUpdate("""
                    IF OBJECT_ID(N'rooms', N'U') IS NULL
                    CREATE TABLE rooms (
                        id INT IDENTITY(1,1) PRIMARY KEY,
                        room_number VARCHAR(10) UNIQUE NOT NULL,
                        room_type VARCHAR(50) NOT NULL,
                        price DECIMAL(10,2) NOT NULL,
                        status VARCHAR(20) DEFAULT 'AVAILABLE'
                    )
                """);

                // Create guests table
                stmt.executeUpdate("""
                    IF OBJECT_ID(N'guests', N'U') IS NULL
                    CREATE TABLE guests (
                        id INT IDENTITY(1,1) PRIMARY KEY,
                        name VARCHAR(100) NOT NULL,
                        phone VARCHAR(20) NOT NULL,
                        email VARCHAR(100),
                        id_number VARCHAR(50) NOT NULL
                    )
                """);

                // Create bookings table
                stmt.executeUpdate("""
                    IF OBJECT_ID(N'bookings', N'U') IS NULL
                    CREATE TABLE bookings (
                        id INT IDENTITY(1,1) PRIMARY KEY,
                        guest_id INT NOT NULL,
                        room_id INT NOT NULL,
                        check_in_date DATE NOT NULL,
                        check_out_date DATE NOT NULL,
                        total_amount DECIMAL(10,2) NOT NULL,
                        status VARCHAR(20) DEFAULT 'ACTIVE',
                        CONSTRAINT FK_Bookings_Guests 
                            FOREIGN KEY (guest_id) REFERENCES guests(id),
                        CONSTRAINT FK_Bookings_Rooms 
                            FOREIGN KEY (room_id) REFERENCES rooms(id)
                    )
                """);

                // Create users table
                stmt.executeUpdate("""
                    IF OBJECT_ID(N'users', N'U') IS NULL
                    CREATE TABLE users (
                        id INT IDENTITY(1,1) PRIMARY KEY,
                        username VARCHAR(50) UNIQUE NOT NULL,
                        password VARCHAR(255) NOT NULL,
                        role VARCHAR(20) NOT NULL,
                        full_name VARCHAR(100) NOT NULL
                    )
                """);

                // Default admin
                stmt.executeUpdate("""
                    IF NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin')
                    INSERT INTO users (username, password, role, full_name)
                    VALUES ('admin', 'admin123', 'ADMIN', 'Administrator')
                """);

                // Default customer
                stmt.executeUpdate("""
                    IF NOT EXISTS (SELECT 1 FROM users WHERE username = 'customer')
                    INSERT INTO users (username, password, role, full_name)
                    VALUES ('customer', 'customer123', 'CUSTOMER', 'Test Customer')
                """);

                System.out.println("Database initialized successfully!");
            }

        } catch (SQLException e) {
            System.err.println("Database initialization error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}