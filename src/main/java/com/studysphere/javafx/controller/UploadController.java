package com.studysphere.javafx.controller;

import com.studysphere.javafx.ViewNavigator;
import com.studysphere.model.Document;
import com.studysphere.model.User;
import com.studysphere.service.DocumentService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;

/**
 * Controller for Document Upload & Multithreaded Processing view.
 */
public class UploadController {
    private static final Logger logger = LoggerFactory.getLogger(UploadController.class);

    @FXML private TextField titleField;
    @FXML private Label selectedFileLabel;
    @FXML private ProgressBar progressBar;
    @FXML private Label progressLabel;
    @FXML private Label stageDescriptionLabel;
    @FXML private Button selectFileButton;
    @FXML private Button uploadButton;
    @FXML private Button cancelButton;

    private File selectedFile;
    private final DocumentService documentService = new DocumentService();

    @FXML
    public void initialize() {
        progressBar.setVisible(false);
        progressLabel.setVisible(false);
        stageDescriptionLabel.setVisible(false);
    }

    @FXML
    private void handleSelectFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Academic Study Material");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Supported Documents (*.pdf, *.docx, *.txt)", "*.pdf", "*.docx", "*.txt"),
                new FileChooser.ExtensionFilter("PDF Documents (*.pdf)", "*.pdf"),
                new FileChooser.ExtensionFilter("Word Documents (*.docx)", "*.docx"),
                new FileChooser.ExtensionFilter("Plain Text Files (*.txt)", "*.txt")
        );

        File file = fileChooser.showOpenDialog(ViewNavigator.getPrimaryStage());
        if (file != null) {
            this.selectedFile = file;
            selectedFileLabel.setText(file.getName() + " (" + String.format("%.2f KB", file.length() / 1024.0) + ")");
            if (titleField.getText().isBlank()) {
                String defaultTitle = file.getName().replaceFirst("[.][^.]+$", "");
                titleField.setText(defaultTitle);
            }
        }
    }

    @FXML
    private void handleUploadAndProcess() {
        if (selectedFile == null) {
            ViewNavigator.showAlert(Alert.AlertType.WARNING, "No File Selected", "Please select a study document to upload.");
            return;
        }

        String title = titleField.getText().trim();
        if (title.isEmpty()) {
            title = selectedFile.getName();
        }

        User user = ViewNavigator.getCurrentUser();
        if (user == null) {
            ViewNavigator.showLogin();
            return;
        }

        // Lock inputs and show progress indicators
        setProcessingUI(true);

        try {
            FileInputStream fis = new FileInputStream(selectedFile);
            documentService.uploadAndProcessAsync(
                    user.getId(),
                    title,
                    selectedFile.getName(),
                    fis,
                    selectedFile.length(),
                    (percent, stage, status) -> Platform.runLater(() -> {
                        progressBar.setProgress(percent / 100.0);
                        progressLabel.setText(percent + "%");
                        stageDescriptionLabel.setText(stage);
                    })
            ).thenAccept(doc -> Platform.runLater(() -> {
                ViewNavigator.showAlert(Alert.AlertType.INFORMATION, "Processing Complete!",
                        "StudySphere successfully analyzed \"" + doc.getTitle() + "\"!\nGenerated " +
                        doc.getTotalTopics() + " topics and structured notes.");
                ViewNavigator.showNotesViewer(doc.getId(), null);
            })).exceptionally(ex -> {
                Platform.runLater(() -> {
                    setProcessingUI(false);
                    ViewNavigator.showAlert(Alert.AlertType.ERROR, "Processing Failed",
                            "Document analysis could not be completed: " + ex.getCause().getMessage());
                });
                return null;
            });
        } catch (Exception e) {
            setProcessingUI(false);
            ViewNavigator.showAlert(Alert.AlertType.ERROR, "Upload Error", e.getMessage());
        }
    }

    private void setProcessingUI(boolean processing) {
        selectFileButton.setDisable(processing);
        uploadButton.setDisable(processing);
        titleField.setDisable(processing);
        progressBar.setVisible(processing);
        progressLabel.setVisible(processing);
        stageDescriptionLabel.setVisible(processing);
    }

    @FXML private void handleBackToDashboard() { ViewNavigator.showDashboard(); }
    @FXML private void handleGoToNotes() { ViewNavigator.showNotesViewer(null, null); }
    @FXML private void handleGoToAskNotes() { ViewNavigator.showAskNotes(); }
    @FXML private void handleGoToRevision() { ViewNavigator.showRevision(); }
    @FXML private void handleGoToCSE1007() { ViewNavigator.showCSE1007Demo(); }
    @FXML private void handleGoToSettings() { ViewNavigator.showSettings(); }
    @FXML private void handleLogout() { ViewNavigator.showLogin(); }
}
