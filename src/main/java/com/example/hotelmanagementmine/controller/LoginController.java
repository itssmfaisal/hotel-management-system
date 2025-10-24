package com.example.hotelmanagementmine.controller;

import com.example.hotelmanagementmine.dao.UserDAO;
import com.example.hotelmanagementmine.model.User;
import com.example.hotelmanagementmine.util.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private final UserDAO userDAO = new UserDAO();

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please fill all fields!");
            return;
        }

        User user = userDAO.authenticate(username, password);
        if (user != null) {
            SessionManager.setCurrentUser(user);
            openDashboard(user);
        } else {
            showError("Invalid username or password!");
        }
    }

    @FXML
    private void handleRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/hotelmanagementmine/register-view.fxml"));
            Scene scene = new Scene(loader.load(), 400, 500);
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Register - Hotel Management System");
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error loading registration page!");
        }
    }

    private void openDashboard(User user) {
        try {
            FXMLLoader loader;
            String title;

            if ("ADMIN".equals(user.getRole())) {
                loader = new FXMLLoader(getClass().getResource("/com/example/hotelmanagementmine/main-view.fxml"));
                title = "Admin Dashboard - Hotel Management System";
            } else {
                loader = new FXMLLoader(getClass().getResource("/com/example/hotelmanagementmine/customer-view.fxml"));
                title = "Customer Dashboard - Hotel Management System";
            }

            Scene scene = new Scene(loader.load(), 1000, 700);
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle(title);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error loading dashboard!");
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}
