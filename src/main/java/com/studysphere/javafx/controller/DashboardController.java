package com.studysphere.javafx.controller;

import com.studysphere.javafx.ViewNavigator;
import com.studysphere.model.Document;
import com.studysphere.model.Topic;
import com.studysphere.model.User;
import com.studysphere.service.DocumentService;
import com.studysphere.service.RevisionService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Controller for Student Dashboard view.
 */
public class DashboardController {
    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);

    @FXML private Label greetingLabel;
    @FXML private Label docCountLabel;
    @FXML private Label topicCountLabel;
    @FXML private Label revisedCountLabel;
    @FXML private Label completionPercentLabel;
    @FXML private ProgressBar completionProgressBar;
    @FXML private ListView<String> recentDocsList;
    @FXML private ListView<String> recommendedTopicsList;

    private final DocumentService documentService = new DocumentService();
    private final RevisionService revisionService = new RevisionService();

    private List<Document> userDocuments;
    private List<Topic> recommendedTopics;

    @FXML
    public void initialize() {
        User user = ViewNavigator.getCurrentUser();
        if (user != null) {
            greetingLabel.setText("Welcome back, " + user.getFullName());
            loadDashboardData(user.getId());
        }

        // Attach double-click listener to open document in notes viewer
        if (recentDocsList != null) {
            recentDocsList.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
                    int idx = recentDocsList.getSelectionModel().getSelectedIndex();
                    if (userDocuments != null && idx >= 0 && idx < userDocuments.size()) {
                        ViewNavigator.showNotesViewer(userDocuments.get(idx).getId(), null);
                    }
                }
            });
        }

        // Attach double-click listener to open recommended topic
        if (recommendedTopicsList != null) {
            recommendedTopicsList.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
                    int idx = recommendedTopicsList.getSelectionModel().getSelectedIndex();
                    if (recommendedTopics != null && idx >= 0 && idx < recommendedTopics.size()) {
                        Topic t = recommendedTopics.get(idx);
                        ViewNavigator.showNotesViewer(t.getDocumentId(), t.getId());
                    }
                }
            });
        }
    }

    private void loadDashboardData(int userId) {
        new Thread(() -> {
            try {
                Map<String, Object> stats = revisionService.getDashboardStats(userId);
                userDocuments = documentService.getDocumentsForUser(userId);
                recommendedTopics = revisionService.getRecommendedRevisionTopics(userId, 6);

                Platform.runLater(() -> {
                    docCountLabel.setText(String.valueOf(stats.get("totalDocuments")));
                    topicCountLabel.setText(String.valueOf(stats.get("totalTopics")));
                    revisedCountLabel.setText(String.valueOf(stats.get("revisedTopics")));

                    double percent = (double) stats.get("completionPercent");
                    completionPercentLabel.setText(percent + "%");
                    completionProgressBar.setProgress(percent / 100.0);

                    recentDocsList.getItems().clear();
                    for (Document d : userDocuments) {
                        recentDocsList.getItems().add(d.getTitle() + " (" + d.getFileType() + " • " + d.getTotalTopics() + " topics)");
                    }
                    if (recentDocsList.getItems().isEmpty()) {
                        recentDocsList.getItems().add("No documents uploaded yet. Click 'Upload Material' to start!");
                    }

                    recommendedTopicsList.getItems().clear();
                    int rank = 1;
                    for (Topic t : recommendedTopics) {
                        double score = t.getRevisionRecord() != null ? t.getRevisionRecord().getRevisionScore() : 0.0;
                        recommendedTopicsList.getItems().add(rank++ + ". " + t.getName() + " [Retention: " + (int)score + "% • " + t.getPriorityLevel() + "]");
                    }
                    if (recommendedTopicsList.getItems().isEmpty()) {
                        recommendedTopicsList.getItems().add("All topics reviewed! Upload more material to expand your knowledge.");
                    }
                });
            } catch (Exception e) {
                logger.error("Failed to load dashboard data: {}", e.getMessage(), e);
            }
        }).start();
    }

    // Navigation Actions
    @FXML private void handleGoToUpload() { ViewNavigator.showUpload(); }
    @FXML private void handleGoToNotes() { ViewNavigator.showNotesViewer(null, null); }
    @FXML private void handleGoToAskNotes() { ViewNavigator.showAskNotes(); }
    @FXML private void handleGoToRevision() { ViewNavigator.showRevision(); }
    @FXML private void handleGoToCSE1007() { ViewNavigator.showCSE1007Demo(); }
    @FXML private void handleGoToSettings() { ViewNavigator.showSettings(); }
    @FXML private void handleLogout() { ViewNavigator.showLogin(); }
}
