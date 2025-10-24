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

public class MainController {

    @FXML private Label welcomeLabel;

    // Room Tab Components
    @FXML private TextField roomNumberField;
    @FXML private ComboBox<String> roomTypeCombo;
    @FXML private TextField roomPriceField;
    @FXML private TableView<Room> roomsTable;
    @FXML private TableColumn<Room, Integer> roomIdColumn;
    @FXML private TableColumn<Room, String> roomNumberColumn;
    @FXML private TableColumn<Room, String> roomTypeColumn;
    @FXML private TableColumn<Room, Double> roomPriceColumn;
    @FXML private TableColumn<Room, String> roomStatusColumn;

    // Guest Tab Components
    @FXML private TextField guestNameField;
    @FXML private TextField guestPhoneField;
    @FXML private TextField guestEmailField;
    @FXML private TextField guestIdNumberField;
    @FXML private TableView<Guest> guestsTable;
    @FXML private TableColumn<Guest, Integer> guestIdColumn;
    @FXML private TableColumn<Guest, String> guestNameColumn;
    @FXML private TableColumn<Guest, String> guestPhoneColumn;
    @FXML private TableColumn<Guest, String> guestEmailColumn;
    @FXML private TableColumn<Guest, String> guestIdNumberColumn;

    // Booking Tab Components
    @FXML private ComboBox<Guest> bookingGuestCombo;
    @FXML private ComboBox<Room> bookingRoomCombo;
    @FXML private DatePicker checkInDatePicker;
    @FXML private DatePicker checkOutDatePicker;
    @FXML private TableView<Booking> bookingsTable;
    @FXML private TableColumn<Booking, Integer> bookingIdColumn;
    @FXML private TableColumn<Booking, String> bookingGuestNameColumn;
    @FXML private TableColumn<Booking, String> bookingRoomNumberColumn;
    @FXML private TableColumn<Booking, LocalDate> bookingCheckInColumn;
    @FXML private TableColumn<Booking, LocalDate> bookingCheckOutColumn;
    @FXML private TableColumn<Booking, Long> bookingDaysColumn;
    @FXML private TableColumn<Booking, Double> bookingPricePerDayColumn;
    @FXML private TableColumn<Booking, Double> bookingTotalColumn;
    @FXML private TableColumn<Booking, String> bookingStatusColumn;

    // DAOs
    private final RoomDAO roomDAO = new RoomDAO();
    private final GuestDAO guestDAO = new GuestDAO();
    private final BookingDAO bookingDAO = new BookingDAO();

    // Observable Lists
    private ObservableList<Room> roomsList = FXCollections.observableArrayList();
    private ObservableList<Guest> guestsList = FXCollections.observableArrayList();
    private ObservableList<Booking> bookingsList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        if (SessionManager.getCurrentUser() != null) {
            welcomeLabel.setText("Welcome, " + SessionManager.getCurrentUser().getFullName());
        }
        initializeRoomTab();
        initializeGuestTab();
        initializeBookingTab();
        loadAllData();
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

    // ====================== ROOM TAB METHODS ======================

    private void initializeRoomTab() {
        roomTypeCombo.setItems(FXCollections.observableArrayList(
            "Single", "Double", "Suite", "Deluxe", "Presidential"
        ));

        roomIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        roomNumberColumn.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        roomTypeColumn.setCellValueFactory(new PropertyValueFactory<>("roomType"));
        roomPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        roomStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        roomsTable.setItems(roomsList);
    }

    @FXML
    private void addRoom() {
        try {
            String roomNumber = roomNumberField.getText();
            String roomType = roomTypeCombo.getValue();
            double price = Double.parseDouble(roomPriceField.getText());

            if (roomNumber.isEmpty() || roomType == null) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill all fields!");
                return;
            }

            Room room = new Room(0, roomNumber, roomType, price, "AVAILABLE");
            if (roomDAO.addRoom(room)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Room added successfully!");
                clearRoomFields();
                loadRooms();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add room!");
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Invalid price format!");
        }
    }

    @FXML
    private void updateRoom() {
        Room selectedRoom = roomsTable.getSelectionModel().getSelectedItem();
        if (selectedRoom == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Error", "Please select a room to update!");
            return;
        }

        try {
            selectedRoom.setRoomNumber(roomNumberField.getText());
            selectedRoom.setRoomType(roomTypeCombo.getValue());
            selectedRoom.setPrice(Double.parseDouble(roomPriceField.getText()));

            if (roomDAO.updateRoom(selectedRoom)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Room updated successfully!");
                clearRoomFields();
                loadRooms();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update room!");
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Invalid price format!");
        }
    }

    @FXML
    private void deleteRoom() {
        Room selectedRoom = roomsTable.getSelectionModel().getSelectedItem();
        if (selectedRoom == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Error", "Please select a room to delete!");
            return;
        }

        if (roomDAO.deleteRoom(selectedRoom.getId())) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Room deleted successfully!");
            clearRoomFields();
            loadRooms();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete room!");
        }
    }

    private void clearRoomFields() {
        roomNumberField.clear();
        roomTypeCombo.getSelectionModel().clearSelection();
        roomPriceField.clear();
    }

    private void loadRooms() {
        roomsList.clear();
        roomsList.addAll(roomDAO.getAllRooms());
        // Update booking room combo box
        bookingRoomCombo.setItems(FXCollections.observableArrayList(roomDAO.getAvailableRooms()));
    }

    // ====================== GUEST TAB METHODS ======================

    private void initializeGuestTab() {
        guestIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        guestNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        guestPhoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        guestEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        guestIdNumberColumn.setCellValueFactory(new PropertyValueFactory<>("idNumber"));

        guestsTable.setItems(guestsList);
    }

    @FXML
    private void addGuest() {
        String name = guestNameField.getText();
        String phone = guestPhoneField.getText();
        String email = guestEmailField.getText();
        String idNumber = guestIdNumberField.getText();

        if (name.isEmpty() || phone.isEmpty() || idNumber.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill all required fields!");
            return;
        }

        Guest guest = new Guest(0, name, phone, email, idNumber);
        if (guestDAO.addGuest(guest)) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Guest added successfully!");
            clearGuestFields();
            loadGuests();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add guest!");
        }
    }

    @FXML
    private void updateGuest() {
        Guest selectedGuest = guestsTable.getSelectionModel().getSelectedItem();
        if (selectedGuest == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Error", "Please select a guest to update!");
            return;
        }

        selectedGuest.setName(guestNameField.getText());
        selectedGuest.setPhone(guestPhoneField.getText());
        selectedGuest.setEmail(guestEmailField.getText());
        selectedGuest.setIdNumber(guestIdNumberField.getText());

        if (guestDAO.updateGuest(selectedGuest)) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Guest updated successfully!");
            clearGuestFields();
            loadGuests();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update guest!");
        }
    }

    @FXML
    private void deleteGuest() {
        Guest selectedGuest = guestsTable.getSelectionModel().getSelectedItem();
        if (selectedGuest == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Error", "Please select a guest to delete!");
            return;
        }

        if (guestDAO.deleteGuest(selectedGuest.getId())) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Guest deleted successfully!");
            clearGuestFields();
            loadGuests();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete guest!");
        }
    }

    private void clearGuestFields() {
        guestNameField.clear();
        guestPhoneField.clear();
        guestEmailField.clear();
        guestIdNumberField.clear();
    }

    private void loadGuests() {
        guestsList.clear();
        guestsList.addAll(guestDAO.getAllGuests());
        // Update booking guest combo box
        bookingGuestCombo.setItems(FXCollections.observableArrayList(guestDAO.getAllGuests()));
    }

    // ====================== BOOKING TAB METHODS ======================

    private void initializeBookingTab() {
        bookingIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        bookingGuestNameColumn.setCellValueFactory(new PropertyValueFactory<>("guestName"));
        bookingRoomNumberColumn.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        bookingCheckInColumn.setCellValueFactory(new PropertyValueFactory<>("checkInDate"));
        bookingCheckOutColumn.setCellValueFactory(new PropertyValueFactory<>("checkOutDate"));
        bookingDaysColumn.setCellValueFactory(new PropertyValueFactory<>("days"));
        bookingPricePerDayColumn.setCellValueFactory(new PropertyValueFactory<>("pricePerDay"));
        bookingTotalColumn.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        bookingStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        bookingsTable.setItems(bookingsList);

        // Custom cell factory to display guest names in combo box
        bookingGuestCombo.setCellFactory(lv -> new ListCell<Guest>() {
            @Override
            protected void updateItem(Guest guest, boolean empty) {
                super.updateItem(guest, empty);
                setText(empty || guest == null ? null : guest.getName() + " (" + guest.getPhone() + ")");
            }
        });
        bookingGuestCombo.setButtonCell(new ListCell<Guest>() {
            @Override
            protected void updateItem(Guest guest, boolean empty) {
                super.updateItem(guest, empty);
                setText(empty || guest == null ? null : guest.getName() + " (" + guest.getPhone() + ")");
            }
        });

        // Custom cell factory to display room info in combo box
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

    @FXML
    private void createBooking() {
        Guest selectedGuest = bookingGuestCombo.getValue();
        Room selectedRoom = bookingRoomCombo.getValue();
        LocalDate checkIn = checkInDatePicker.getValue();
        LocalDate checkOut = checkOutDatePicker.getValue();

        if (selectedGuest == null || selectedRoom == null || checkIn == null || checkOut == null) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill all fields!");
            return;
        }

        if (checkOut.isBefore(checkIn) || checkOut.isEqual(checkIn)) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Check-out date must be after check-in date!");
            return;
        }

        long days = ChronoUnit.DAYS.between(checkIn, checkOut);
        double totalAmount = days * selectedRoom.getPrice();

        Booking booking = new Booking(0, selectedGuest.getId(), selectedRoom.getId(),
                                      checkIn, checkOut, totalAmount, "ACTIVE");

        if (bookingDAO.addBooking(booking)) {
            showAlert(Alert.AlertType.INFORMATION, "Success",
                     "Booking created successfully!\nTotal Amount: $" + totalAmount);
            clearBookingFields();
            loadBookings();
            loadRooms(); // Refresh rooms to update status
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to create booking!");
        }
    }

    @FXML
    private void checkoutBooking() {
        Booking selectedBooking = bookingsTable.getSelectionModel().getSelectedItem();
        if (selectedBooking == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Error", "Please select a booking to check out!");
            return;
        }

        if (bookingDAO.checkoutBooking(selectedBooking.getId())) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Guest checked out successfully!");
            loadBookings();
            loadRooms(); // Refresh rooms to update status
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to check out!");
        }
    }

    @FXML
    private void deleteBooking() {
        Booking selectedBooking = bookingsTable.getSelectionModel().getSelectedItem();
        if (selectedBooking == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Error", "Please select a booking to delete!");
            return;
        }

        if (bookingDAO.deleteBooking(selectedBooking.getId())) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Booking deleted successfully!");
            clearBookingFields();
            loadBookings();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete booking!");
        }
    }

    @FXML
    private void refreshBookings() {
        loadBookings();
    }

    private void clearBookingFields() {
        bookingGuestCombo.getSelectionModel().clearSelection();
        bookingRoomCombo.getSelectionModel().clearSelection();
        checkInDatePicker.setValue(null);
        checkOutDatePicker.setValue(null);
    }

    private void loadBookings() {
        bookingsList.clear();
        bookingsList.addAll(bookingDAO.getAllBookings());

        // Update combo boxes
        bookingGuestCombo.setItems(FXCollections.observableArrayList(guestDAO.getAllGuests()));
        bookingRoomCombo.setItems(FXCollections.observableArrayList(roomDAO.getAvailableRooms()));
    }

    // ====================== UTILITY METHODS ======================

    private void loadAllData() {
        loadRooms();
        loadGuests();
        loadBookings();
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
