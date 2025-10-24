package com.example.hotelmanagementmine.controller;

import com.example.hotelmanagementmine.dao.BookingDAO;
import com.example.hotelmanagementmine.dao.GuestDAO;
import com.example.hotelmanagementmine.dao.RoomDAO;
import com.example.hotelmanagementmine.model.Booking;
import com.example.hotelmanagementmine.model.Guest;
import com.example.hotelmanagementmine.model.Room;
import com.example.hotelmanagementmine.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

public class CustomerController {

    @FXML private Label welcomeLabel;
    @FXML private ComboBox<String> roomTypeFilterCombo;
    @FXML private TableView<Room> availableRoomsTable;
    @FXML private TableColumn<Room, String> roomNumberColumn;
    @FXML private TableColumn<Room, String> roomTypeColumn;
    @FXML private TableColumn<Room, Double> roomPriceColumn;
    @FXML private TableColumn<Room, String> roomStatusColumn;

    @FXML private ComboBox<Room> bookingRoomCombo;
    @FXML private DatePicker checkInDatePicker;
    @FXML private DatePicker checkOutDatePicker;
    @FXML private Label totalAmountLabel;

    @FXML private TableView<Booking> myBookingsTable;
    @FXML private TableColumn<Booking, Integer> bookingIdColumn;
    @FXML private TableColumn<Booking, String> bookingRoomNumberColumn;
    @FXML private TableColumn<Booking, LocalDate> bookingCheckInColumn;
    @FXML private TableColumn<Booking, LocalDate> bookingCheckOutColumn;
    @FXML private TableColumn<Booking, Long> bookingDaysColumn;
    @FXML private TableColumn<Booking, Double> bookingPricePerDayColumn;
    @FXML private TableColumn<Booking, Double> bookingTotalColumn;
    @FXML private TableColumn<Booking, String> bookingStatusColumn;

    private final RoomDAO roomDAO = new RoomDAO();
    private final GuestDAO guestDAO = new GuestDAO();
    private final BookingDAO bookingDAO = new BookingDAO();

    private ObservableList<Room> availableRoomsList = FXCollections.observableArrayList();
    private ObservableList<Booking> myBookingsList = FXCollections.observableArrayList();
    private Guest currentGuest;

    @FXML
    public void initialize() {
        welcomeLabel.setText("Welcome, " + SessionManager.getCurrentUser().getFullName());

        initializeRoomsTable();
        initializeBookingsTable();
        setupRoomTypeFilter();
        setupPriceCalculator();

        // Create or get guest for current user
        createGuestForCurrentUser();

        loadAvailableRooms();
        loadMyBookings();
    }

    private void initializeRoomsTable() {
        roomNumberColumn.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        roomTypeColumn.setCellValueFactory(new PropertyValueFactory<>("roomType"));
        roomPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        roomStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        availableRoomsTable.setItems(availableRoomsList);
    }

    private void initializeBookingsTable() {
        bookingIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        bookingRoomNumberColumn.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        bookingCheckInColumn.setCellValueFactory(new PropertyValueFactory<>("checkInDate"));
        bookingCheckOutColumn.setCellValueFactory(new PropertyValueFactory<>("checkOutDate"));
        bookingDaysColumn.setCellValueFactory(new PropertyValueFactory<>("days"));
        bookingPricePerDayColumn.setCellValueFactory(new PropertyValueFactory<>("pricePerDay"));
        bookingTotalColumn.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        bookingStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        myBookingsTable.setItems(myBookingsList);
    }

    private void setupRoomTypeFilter() {
        roomTypeFilterCombo.setItems(FXCollections.observableArrayList(
            "All Types", "Single", "Double", "Suite", "Deluxe", "Presidential"
        ));
        roomTypeFilterCombo.setValue("All Types");
        roomTypeFilterCombo.setOnAction(e -> filterRooms());
    }

    private void setupPriceCalculator() {
        checkInDatePicker.setOnAction(e -> calculateTotalAmount());
        checkOutDatePicker.setOnAction(e -> calculateTotalAmount());
        bookingRoomCombo.setOnAction(e -> calculateTotalAmount());

        // Custom cell factory for room combo box
        bookingRoomCombo.setCellFactory(lv -> new ListCell<Room>() {
            @Override
            protected void updateItem(Room room, boolean empty) {
                super.updateItem(room, empty);
                setText(empty || room == null ? null : room.getRoomNumber() + " - " + room.getRoomType() + " ($" + room.getPrice() + ")");
            }
        });
        bookingRoomCombo.setButtonCell(new ListCell<Room>() {
            @Override
            protected void updateItem(Room room, boolean empty) {
                super.updateItem(room, empty);
                setText(empty || room == null ? null : room.getRoomNumber() + " - " + room.getRoomType() + " ($" + room.getPrice() + ")");
            }
        });
    }

    private void createGuestForCurrentUser() {
        String username = SessionManager.getCurrentUser().getUsername();
        // Check if guest exists with this username as ID number
        List<Guest> allGuests = guestDAO.getAllGuests();
        currentGuest = allGuests.stream()
                .filter(g -> g.getIdNumber().equals(username))
                .findFirst()
                .orElse(null);

        if (currentGuest == null) {
            // Create new guest
            currentGuest = new Guest(0, SessionManager.getCurrentUser().getFullName(),
                    "N/A", "N/A", username);
            guestDAO.addGuest(currentGuest);
        }
    }

    private void calculateTotalAmount() {
        Room selectedRoom = bookingRoomCombo.getValue();
        LocalDate checkIn = checkInDatePicker.getValue();
        LocalDate checkOut = checkOutDatePicker.getValue();

        if (selectedRoom != null && checkIn != null && checkOut != null) {
            if (checkOut.isAfter(checkIn)) {
                long days = ChronoUnit.DAYS.between(checkIn, checkOut);
                double total = days * selectedRoom.getPrice();
                totalAmountLabel.setText("Total: $" + String.format("%.2f", total) + " (" + days + " days)");
            } else {
                totalAmountLabel.setText("Invalid dates!");
            }
        } else {
            totalAmountLabel.setText("");
        }
    }

    @FXML
    private void refreshRooms() {
        loadAvailableRooms();
    }

    @FXML
    private void createBooking() {
        Room selectedRoom = bookingRoomCombo.getValue();
        LocalDate checkIn = checkInDatePicker.getValue();
        LocalDate checkOut = checkOutDatePicker.getValue();

        if (selectedRoom == null || checkIn == null || checkOut == null) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please select room and dates!");
            return;
        }

        if (checkOut.isBefore(checkIn) || checkOut.isEqual(checkIn)) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Check-out date must be after check-in date!");
            return;
        }

        long days = ChronoUnit.DAYS.between(checkIn, checkOut);
        double totalAmount = days * selectedRoom.getPrice();

        Booking booking = new Booking(0, currentGuest.getId(), selectedRoom.getId(),
                checkIn, checkOut, totalAmount, "ACTIVE");

        if (bookingDAO.addBooking(booking)) {
            showAlert(Alert.AlertType.INFORMATION, "Success",
                    "Booking created successfully!\nTotal Amount: $" + totalAmount);
            clearBookingFields();
            loadAvailableRooms();
            loadMyBookings();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to create booking!");
        }
    }

    @FXML
    private void cancelBooking() {
        Booking selectedBooking = myBookingsTable.getSelectionModel().getSelectedItem();
        if (selectedBooking == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Error", "Please select a booking to cancel!");
            return;
        }

        if (!"ACTIVE".equals(selectedBooking.getStatus())) {
            showAlert(Alert.AlertType.WARNING, "Invalid Action", "Can only cancel active bookings!");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Cancellation");
        confirmation.setHeaderText("Cancel Booking?");
        confirmation.setContentText("Are you sure you want to cancel this booking?");

        if (confirmation.showAndWait().get() == ButtonType.OK) {
            if (bookingDAO.checkoutBooking(selectedBooking.getId())) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Booking cancelled successfully!");
                loadAvailableRooms();
                loadMyBookings();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to cancel booking!");
            }
        }
    }

    @FXML
    private void refreshBookings() {
        loadMyBookings();
    }

    @FXML
    private void handleLogout() {
        SessionManager.clearSession();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/hotelmanagementmine/login-view.fxml"));
            Scene scene = new Scene(loader.load(), 400, 500);
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Login - Hotel Management System");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadAvailableRooms() {
        availableRoomsList.clear();
        availableRoomsList.addAll(roomDAO.getAvailableRooms());
        bookingRoomCombo.setItems(FXCollections.observableArrayList(roomDAO.getAvailableRooms()));
        filterRooms();
    }

    private void filterRooms() {
        String selectedType = roomTypeFilterCombo.getValue();
        if ("All Types".equals(selectedType)) {
            availableRoomsTable.setItems(availableRoomsList);
        } else {
            ObservableList<Room> filtered = availableRoomsList.stream()
                    .filter(room -> room.getRoomType().equals(selectedType))
                    .collect(Collectors.toCollection(FXCollections::observableArrayList));
            availableRoomsTable.setItems(filtered);
        }
    }

    private void loadMyBookings() {
        myBookingsList.clear();
        List<Booking> allBookings = bookingDAO.getAllBookings();
        List<Booking> myBookings = allBookings.stream()
                .filter(b -> b.getGuestId() == currentGuest.getId())
                .collect(Collectors.toList());
        myBookingsList.addAll(myBookings);
    }

    private void clearBookingFields() {
        bookingRoomCombo.getSelectionModel().clearSelection();
        checkInDatePicker.setValue(null);
        checkOutDatePicker.setValue(null);
        totalAmountLabel.setText("");
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
