package com.studysphere.javafx.controller;

import com.studysphere.javafx.ViewNavigator;
import com.studysphere.model.User;
import com.studysphere.service.AskNotesService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for "Ask My Notes" Grounded Academic Q&A.
 */
public class AskNotesController {
    private static final Logger logger = LoggerFactory.getLogger(AskNotesController.class);

    @FXML private TextField questionField;
    @FXML private Button askButton;
    @FXML private TextArea answerArea;
    @FXML private VBox citationsBox;
    @FXML private ProgressIndicator loadingIndicator;

    private final AskNotesService askNotesService = new AskNotesService();

    @FXML
    public void initialize() {
        if (loadingIndicator != null) loadingIndicator.setVisible(false);
        answerArea.setEditable(false);
        answerArea.setWrapText(true);
    }

    @FXML
    private void handleAskQuestion() {
        String question = questionField.getText().trim();
        if (question.isEmpty()) {
            ViewNavigator.showAlert(Alert.AlertType.WARNING, "Empty Question", "Please enter a question.");
            return;
        }

        User user = ViewNavigator.getCurrentUser();
        if (user == null) {
            ViewNavigator.showLogin();
            return;
        }

        if (loadingIndicator != null) loadingIndicator.setVisible(true);
        askButton.setDisable(true);
        answerArea.setText("Searching stored academic notes and synthesizing grounded response...");
        citationsBox.getChildren().clear();

        new Thread(() -> {
            try {
                AskNotesService.GroundedAnswerResult result = askNotesService.askQuestion(user.getId(), question);
                Platform.runLater(() -> {
                    answerArea.setText(result.getAnswer());
                    citationsBox.getChildren().clear();

                    if (!result.getCitations().isEmpty()) {
                        Label title = new Label("Sources from your notes:");
                        title.setStyle("-fx-font-weight: bold; -fx-text-fill: #38bdf8; -fx-padding: 0 0 6 0;");
                        citationsBox.getChildren().add(title);

                        for (String citation : result.getCitations()) {
                            Label citLabel = new Label("• " + citation);
                            citLabel.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 13px;");
                            citationsBox.getChildren().add(citLabel);
                        }
                    }

                    askButton.setDisable(false);
                    if (loadingIndicator != null) loadingIndicator.setVisible(false);
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    answerArea.setText("Error answering question: " + e.getMessage());
                    askButton.setDisable(false);
                    if (loadingIndicator != null) loadingIndicator.setVisible(false);
                });
            }
        }).start();
    }

    @FXML
    private void handleSampleQuestion(javafx.event.ActionEvent event) {
        if (event.getSource() instanceof Button btn) {
            questionField.setText(btn.getText());
            handleAskQuestion();
        }
    }

    @FXML private void handleBackToDashboard() { ViewNavigator.showDashboard(); }
    @FXML private void handleGoToUpload() { ViewNavigator.showUpload(); }
    @FXML private void handleGoToNotes() { ViewNavigator.showNotesViewer(null, null); }
    @FXML private void handleGoToRevision() { ViewNavigator.showRevision(); }
    @FXML private void handleGoToCSE1007() { ViewNavigator.showCSE1007Demo(); }
    @FXML private void handleGoToSettings() { ViewNavigator.showSettings(); }
    @FXML private void handleLogout() { ViewNavigator.showLogin(); }
}
