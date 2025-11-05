package com.example.hotelmanagementmine.controller;

import com.example.hotelmanagementmine.dao.UserDAO;
import com.example.hotelmanagementmine.model.User;
import com.example.hotelmanagementmine.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Label;

public class ProfileController {
    @FXML
    private TextField fullNameField;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField oldPasswordField;
    @FXML
    private PasswordField newPasswordField;

    @FXML
    private Label statusLabel;

    private final UserDAO userDAO = new UserDAO();

    @FXML
    public void initialize() {
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser != null) {
            fullNameField.setText(currentUser.getFullName());
            usernameField.setText(currentUser.getUsername());
            // Do not pre-populate password for security reasons
        }
    }

    @FXML
    public void handleUpdateProfile() {
        try {
            User currentUser = SessionManager.getCurrentUser();
            if (currentUser != null) {
                currentUser.setFullName(fullNameField.getText());
                userDAO.updateUserProfile(currentUser);
                statusLabel.setText("Profile updated successfully!");
            }
        } catch (Exception e) {
            statusLabel.setText("Failed to update profile.");
            e.printStackTrace();
        }
    }
}
