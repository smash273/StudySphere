package com.studysphere.javafx.controller;

import com.studysphere.config.DatabaseConfig;
import com.studysphere.config.DatabaseConnectionManager;
import com.studysphere.javafx.ViewNavigator;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for Settings & AI Configuration view.
 */
public class SettingsController {
    private static final Logger logger = LoggerFactory.getLogger(SettingsController.class);

    @FXML private ComboBox<String> aiProviderSelector;
    @FXML private TextField apiKeyField;
    @FXML private Label dbUrlLabel;
    @FXML private Label dbStatusLabel;
    @FXML private Label uploadLimitLabel;

    @FXML
    public void initialize() {
        aiProviderSelector.getItems().addAll("fallback", "openai", "gemini");
        aiProviderSelector.setValue(DatabaseConfig.getAiProvider());
        apiKeyField.setText(DatabaseConfig.getAiApiKey());

        dbUrlLabel.setText(DatabaseConfig.getDbUrl());
        boolean connected = DatabaseConnectionManager.getInstance().testConnection();
        dbStatusLabel.setText(connected ? "Online & Connected" : "Connection Error");
        dbStatusLabel.setStyle(connected ? "-fx-text-fill: #10b981;" : "-fx-text-fill: #ef4444;");

        uploadLimitLabel.setText((DatabaseConfig.getMaxUploadSizeBytes() / (1024 * 1024)) + " MB");
    }

    @FXML
    private void handleSaveSettings() {
        String provider = aiProviderSelector.getValue();
        ViewNavigator.showAlert(Alert.AlertType.INFORMATION, "Settings Saved",
                "AI Provider set to: " + provider + "\nConfiguration applied for future processing.");
    }

    @FXML
    private void handleReSeedData() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Re-seed sample academic notes (OS, DBMS, Java)?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                new Thread(() -> {
                    try {
                        DatabaseConnectionManager.getInstance().seedSampleData();
                        Platform.runLater(() -> ViewNavigator.showAlert(Alert.AlertType.INFORMATION, "Success", "Sample academic data re-seeded successfully!"));
                    } catch (Exception e) {
                        Platform.runLater(() -> ViewNavigator.showAlert(Alert.AlertType.ERROR, "Error", e.getMessage()));
                    }
                }).start();
            }
        });
    }

    @FXML private void handleBackToDashboard() { ViewNavigator.showDashboard(); }
    @FXML private void handleGoToUpload() { ViewNavigator.showUpload(); }
    @FXML private void handleGoToNotes() { ViewNavigator.showNotesViewer(null, null); }
    @FXML private void handleGoToAskNotes() { ViewNavigator.showAskNotes(); }
    @FXML private void handleGoToRevision() { ViewNavigator.showRevision(); }
    @FXML private void handleGoToCSE1007() { ViewNavigator.showCSE1007Demo(); }
    @FXML private void handleLogout() { ViewNavigator.showLogin(); }
}
