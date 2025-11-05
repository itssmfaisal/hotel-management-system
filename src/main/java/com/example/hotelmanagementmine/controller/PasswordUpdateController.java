package com.example.hotelmanagementmine.controller;

import com.example.hotelmanagementmine.dao.UserDAO;
import com.example.hotelmanagementmine.model.User;
import com.example.hotelmanagementmine.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;

import java.sql.SQLException;

public class PasswordUpdateController {
    @FXML private PasswordField oldPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private Label statusLabel;

    private final UserDAO userDAO = new UserDAO();

    @FXML
    public void handleUpdatePassword() throws SQLException {
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) return;
        String oldPassInput = oldPasswordField.getText();
        String newPassInput = newPasswordField.getText();

        if (oldPassInput.isEmpty() || newPassInput.isEmpty()) {
            statusLabel.setText("Fill in all password fields.");
            return;
        }
        if (!oldPassInput.equals(currentUser.getPassword())) {
            statusLabel.setText("Current password incorrect!");
            return;
        }
        currentUser.setPassword(newPassInput);
        userDAO.updateUserProfile(currentUser);
        statusLabel.setText("Password updated successfully.");
    }
}
