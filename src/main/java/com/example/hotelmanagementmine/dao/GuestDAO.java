package com.example.hotelmanagementmine.dao;

import com.example.hotelmanagementmine.model.Guest;
import com.example.hotelmanagementmine.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GuestDAO {

    public boolean addGuest(Guest guest) {
        String sql = "INSERT INTO guests (name, phone, email, id_number) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, guest.getName());
            pstmt.setString(2, guest.getPhone());
            pstmt.setString(3, guest.getEmail());
            pstmt.setString(4, guest.getIdNumber());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    guest.setId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Guest> getAllGuests() {
        List<Guest> guests = new ArrayList<>();
        String sql = "SELECT * FROM guests";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Guest guest = new Guest(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("phone"),
                    rs.getString("email"),
                    rs.getString("id_number")
                );
                guests.add(guest);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return guests;
    }

    public boolean updateGuest(Guest guest) {
        String sql = "UPDATE guests SET name = ?, phone = ?, email = ?, id_number = ? WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, guest.getName());
            pstmt.setString(2, guest.getPhone());
            pstmt.setString(3, guest.getEmail());
            pstmt.setString(4, guest.getIdNumber());
            pstmt.setInt(5, guest.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteGuest(int id) {
        String sql = "DELETE FROM guests WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Guest getGuestById(int id) {
        String sql = "SELECT * FROM guests WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Guest(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("phone"),
                    rs.getString("email"),
                    rs.getString("id_number")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
