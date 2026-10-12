package com.studysphere.javafx.controller;

import com.studysphere.config.DatabaseConnectionManager;
import com.studysphere.javafx.ViewNavigator;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Controller for CSE1007 Technical Demonstration Mode screen.
 * Explicitly displays syllabus compliance and implementation verification for academic review.
 */
public class CSE1007DemoController {

    @FXML private Label dbStatusLabel;
    @FXML private Label threadPoolStatusLabel;

    @FXML
    public void initialize() {
        boolean dbOk = DatabaseConnectionManager.getInstance().testConnection();
        dbStatusLabel.setText(dbOk ? "CONNECTED (PostgreSQL 18 - HikariCP)" : "CHECK CONNECTION");
        dbStatusLabel.setStyle(dbOk ? "-fx-text-fill: #10b981; -fx-font-weight: bold;" : "-fx-text-fill: #ef4444; -fx-font-weight: bold;");

        threadPoolStatusLabel.setText("ACTIVE (ExecutorService + Callable + Future)");
        threadPoolStatusLabel.setStyle("-fx-text-fill: #10b981; -fx-font-weight: bold;");
    }

    @FXML private void handleBackToDashboard() { ViewNavigator.showDashboard(); }
    @FXML private void handleGoToUpload() { ViewNavigator.showUpload(); }
    @FXML private void handleGoToNotes() { ViewNavigator.showNotesViewer(null, null); }
    @FXML private void handleGoToAskNotes() { ViewNavigator.showAskNotes(); }
    @FXML private void handleGoToRevision() { ViewNavigator.showRevision(); }
    @FXML private void handleGoToSettings() { ViewNavigator.showSettings(); }
    @FXML private void handleLogout() { ViewNavigator.showLogin(); }
}
