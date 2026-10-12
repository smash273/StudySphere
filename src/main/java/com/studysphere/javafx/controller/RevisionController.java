package com.studysphere.javafx.controller;

import com.studysphere.javafx.ViewNavigator;
import com.studysphere.model.RevisionRecord;
import com.studysphere.model.RevisionStatus;
import com.studysphere.model.User;
import com.studysphere.service.RevisionService;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Controller for Revision Dashboard and Spaced Retention Tracking.
 */
public class RevisionController {
    private static final Logger logger = LoggerFactory.getLogger(RevisionController.class);

    @FXML private TableView<RevisionRecord> revisionTable;
    @FXML private TableColumn<RevisionRecord, String> topicColumn;
    @FXML private TableColumn<RevisionRecord, String> documentColumn;
    @FXML private TableColumn<RevisionRecord, String> scoreColumn;
    @FXML private TableColumn<RevisionRecord, String> statusColumn;
    @FXML private TableColumn<RevisionRecord, String> countColumn;
    @FXML private TableColumn<RevisionRecord, String> lastReviewedColumn;

    @FXML private Label masteredCountLabel;
    @FXML private Label strongCountLabel;
    @FXML private Label needsRevisionCountLabel;

    private final RevisionService revisionService = new RevisionService();
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");

    @FXML
    public void initialize() {
        topicColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTopicName() != null ? data.getValue().getTopicName() : "Topic #" + data.getValue().getTopicId()));
        documentColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDocumentTitle() != null ? data.getValue().getDocumentTitle() : "—"));
        scoreColumn.setCellValueFactory(data -> new SimpleStringProperty((int)data.getValue().getRevisionScore() + "%"));
        statusColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus().getLabel()));
        countColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRevisionCount() + " reviews"));
        lastReviewedColumn.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getLastReviewed() != null ? data.getValue().getLastReviewed().format(DATE_FMT) : "Never"));

        User user = ViewNavigator.getCurrentUser();
        if (user != null) {
            loadRevisionData(user.getId());
        }
    }

    private void loadRevisionData(int userId) {
        new Thread(() -> {
            try {
                List<RevisionRecord> records = revisionService.getRevisionRecordsForUser(userId);
                long mastered = records.stream().filter(r -> r.getStatus() == RevisionStatus.MASTERED).count();
                long strong = records.stream().filter(r -> r.getStatus() == RevisionStatus.STRONG).count();
                long needs = records.stream().filter(r -> r.getStatus() == RevisionStatus.NEEDS_REVISION).count();

                Platform.runLater(() -> {
                    revisionTable.getItems().setAll(records);
                    masteredCountLabel.setText(String.valueOf(mastered));
                    strongCountLabel.setText(String.valueOf(strong));
                    needsRevisionCountLabel.setText(String.valueOf(needs));
                });
            } catch (Exception e) {
                logger.error("Failed to load revision data: {}", e.getMessage(), e);
            }
        }).start();
    }

    @FXML
    private void handleMarkSelectedRevised() {
        RevisionRecord selected = revisionTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            ViewNavigator.showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a topic from the table to mark as revised.");
            return;
        }

        User user = ViewNavigator.getCurrentUser();
        if (user == null) return;

        new Thread(() -> {
            try {
                revisionService.markAsRevised(user.getId(), selected.getTopicId(), "Revised via Revision Dashboard");
                Platform.runLater(() -> {
                    ViewNavigator.showAlert(Alert.AlertType.INFORMATION, "Success", "Topic marked as revised! Retention score updated.");
                    loadRevisionData(user.getId());
                });
            } catch (Exception e) {
                logger.error("Failed to revise topic: {}", e.getMessage(), e);
            }
        }).start();
    }

    @FXML
    private void handleTakeQuizForSelected() {
        RevisionRecord selected = revisionTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            ViewNavigator.showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a topic to take a revision quiz.");
            return;
        }
        ViewNavigator.showQuiz(selected.getTopicId());
    }

    @FXML
    private void handleViewNotesForSelected() {
        RevisionRecord selected = revisionTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            ViewNavigator.showNotesViewer(null, selected.getTopicId());
        }
    }

    @FXML private void handleBackToDashboard() { ViewNavigator.showDashboard(); }
    @FXML private void handleGoToUpload() { ViewNavigator.showUpload(); }
    @FXML private void handleGoToNotes() { ViewNavigator.showNotesViewer(null, null); }
    @FXML private void handleGoToAskNotes() { ViewNavigator.showAskNotes(); }
    @FXML private void handleGoToCSE1007() { ViewNavigator.showCSE1007Demo(); }
    @FXML private void handleGoToSettings() { ViewNavigator.showSettings(); }
    @FXML private void handleLogout() { ViewNavigator.showLogin(); }
}
