package com.example.hotelmanagementmine.controller;

import com.example.hotelmanagementmine.dao.UserDAO;
import com.example.hotelmanagementmine.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class RegisterController {

    @FXML private TextField fullNameField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label messageLabel;

    private final UserDAO userDAO = new UserDAO();

    @FXML
    private void handleRegister() {
        String fullName = fullNameField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (fullName.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showMessage("Please fill all fields!", "red");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showMessage("Passwords do not match!", "red");
            return;
        }

        if (username.length() < 4) {
            showMessage("Username must be at least 4 characters!", "red");
            return;
        }

        if (password.length() < 6) {
            showMessage("Password must be at least 6 characters!", "red");
            return;
        }

        User user = new User(0, username, password, "CUSTOMER", fullName);
        if (userDAO.registerUser(user)) {
            showMessage("Registration successful! Please login.", "green");
            // Clear fields
            fullNameField.clear();
            usernameField.clear();
            passwordField.clear();
            confirmPasswordField.clear();

            // Navigate to login after 2 seconds
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    javafx.application.Platform.runLater(this::handleBackToLogin);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        } else {
            showMessage("Registration failed! Username may already exist.", "red");
        }
    }

    @FXML
    private void handleBackToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/hotelmanagementmine/login-view.fxml"));
            Scene scene = new Scene(loader.load(), 400, 500);
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Login - Hotel Management System");
        } catch (IOException e) {
            e.printStackTrace();
            showMessage("Error loading login page!", "red");
        }
    }

    private void showMessage(String message, String color) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: " + color + ";");
        messageLabel.setVisible(true);
    }
}
