package com.example.hotelmanagementmine.dao;

import com.example.hotelmanagementmine.model.Booking;
import com.example.hotelmanagementmine.util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {

    public boolean addBooking(Booking booking) {
        String sql = "INSERT INTO bookings (guest_id, room_id, check_in_date, check_out_date, total_amount, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, booking.getGuestId());
            pstmt.setInt(2, booking.getRoomId());
            pstmt.setDate(3, Date.valueOf(booking.getCheckInDate()));
            pstmt.setDate(4, Date.valueOf(booking.getCheckOutDate()));
            pstmt.setDouble(5, booking.getTotalAmount());
            pstmt.setString(6, booking.getStatus());

            boolean success = pstmt.executeUpdate() > 0;

            // Update room status to OCCUPIED
            if (success) {
                updateRoomStatus(booking.getRoomId(), "OCCUPIED");
            }

            return success;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Booking> getAllBookings() {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT b.*, g.name as guest_name, r.room_number, r.price " +
                     "FROM bookings b " +
                     "JOIN guests g ON b.guest_id = g.id " +
                     "JOIN rooms r ON b.room_id = r.id";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                LocalDate checkIn = rs.getDate("check_in_date").toLocalDate();
                LocalDate checkOut = rs.getDate("check_out_date").toLocalDate();
                long days = java.time.temporal.ChronoUnit.DAYS.between(checkIn, checkOut);
                double pricePerDay = rs.getDouble("price");

                Booking booking = new Booking(
                    rs.getInt("id"),
                    rs.getInt("guest_id"),
                    rs.getInt("room_id"),
                    checkIn,
                    checkOut,
                    rs.getDouble("total_amount"),
                    rs.getString("status")
                );
                booking.setGuestName(rs.getString("guest_name"));
                booking.setRoomNumber(rs.getString("room_number"));
                booking.setPricePerDay(pricePerDay);
                booking.setDays(days);
                bookings.add(booking);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }

    public List<Booking> getActiveBookings() {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT b.*, g.name as guest_name, r.room_number, r.price " +
                     "FROM bookings b " +
                     "JOIN guests g ON b.guest_id = g.id " +
                     "JOIN rooms r ON b.room_id = r.id " +
                     "WHERE b.status = 'ACTIVE'";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                LocalDate checkIn = rs.getDate("check_in_date").toLocalDate();
                LocalDate checkOut = rs.getDate("check_out_date").toLocalDate();
                long days = java.time.temporal.ChronoUnit.DAYS.between(checkIn, checkOut);
                double pricePerDay = rs.getDouble("price");

                Booking booking = new Booking(
                    rs.getInt("id"),
                    rs.getInt("guest_id"),
                    rs.getInt("room_id"),
                    checkIn,
                    checkOut,
                    rs.getDouble("total_amount"),
                    rs.getString("status")
                );
                booking.setGuestName(rs.getString("guest_name"));
                booking.setRoomNumber(rs.getString("room_number"));
                booking.setPricePerDay(pricePerDay);
                booking.setDays(days);
                bookings.add(booking);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }

    public boolean checkoutBooking(int bookingId) {
        String sql = "UPDATE bookings SET status = 'COMPLETED' WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookingId);
            boolean success = pstmt.executeUpdate() > 0;

            // Get room_id and update room status to AVAILABLE
            if (success) {
                String getRoomSql = "SELECT room_id FROM bookings WHERE id = ?";
                try (PreparedStatement getRoomStmt = conn.prepareStatement(getRoomSql)) {
                    getRoomStmt.setInt(1, bookingId);
                    ResultSet rs = getRoomStmt.executeQuery();
                    if (rs.next()) {
                        updateRoomStatus(rs.getInt("room_id"), "AVAILABLE");
                    }
                }
            }

            return success;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void updateRoomStatus(int roomId, String status) {
        String sql = "UPDATE rooms SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, roomId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean deleteBooking(int id) {
        String sql = "DELETE FROM bookings WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
