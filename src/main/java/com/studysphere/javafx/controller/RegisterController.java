package com.studysphere.javafx.controller;

import com.studysphere.javafx.ViewNavigator;
import com.studysphere.model.Student;
import com.studysphere.service.AuthService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for Student Registration view.
 */
public class RegisterController {
    private static final Logger logger = LoggerFactory.getLogger(RegisterController.class);

    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextField fullNameField;
    @FXML private TextField studentIdField;
    @FXML private TextField enrollmentNoField;
    @FXML private TextField majorField;
    @FXML private Spinner<Integer> semesterSpinner;
    @FXML private Label errorLabel;
    @FXML private Button registerButton;

    private final AuthService authService = new AuthService();

    @FXML
    public void initialize() {
        errorLabel.setVisible(false);
        if (semesterSpinner != null) {
            semesterSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 5));
        }
    }

    @FXML
    private void handleRegister() {
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String fullName = fullNameField.getText();
        String studentId = studentIdField.getText();
        String enrollment = enrollmentNoField.getText();
        String major = majorField.getText();
        int semester = semesterSpinner != null ? semesterSpinner.getValue() : 1;

        registerButton.setDisable(true);
        errorLabel.setVisible(false);

        new Thread(() -> {
            try {
                Student student = authService.registerStudent(username, email, password, fullName,
                        studentId, enrollment, major, semester);
                Platform.runLater(() -> {
                    ViewNavigator.showAlert(Alert.AlertType.INFORMATION, "Registration Successful",
                            "Welcome to StudySphere, " + student.getFullName() + "! You may now log in.");
                    ViewNavigator.showLogin();
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    errorLabel.setText(e.getMessage());
                    errorLabel.setVisible(true);
                    registerButton.setDisable(false);
                });
            }
        }).start();
    }

    @FXML
    private void handleGoToLogin() {
        ViewNavigator.showLogin();
    }
}
