package com.studysphere.javafx.controller;

import com.studysphere.javafx.ViewNavigator;
import com.studysphere.model.*;
import com.studysphere.service.DocumentService;
import com.studysphere.service.RevisionService;
import com.studysphere.service.TopicService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for interactive Academic Notes Viewer and Topic Tree.
 */
public class NotesViewerController {
    private static final Logger logger = LoggerFactory.getLogger(NotesViewerController.class);

    @FXML private ComboBox<Document> documentSelector;
    @FXML private TreeView<String> topicTree;
    @FXML private TextField searchField;

    // Right details pane
    @FXML private Label topicTitleLabel;
    @FXML private Label priorityBadge;
    @FXML private Label revisionBadge;
    @FXML private TextArea summaryArea;
    @FXML private VBox importantPointsBox;
    @FXML private VBox definitionsBox;
    @FXML private VBox formulasBox;
    @FXML private FlowPane keywordsPane;
    @FXML private Button markRevisedButton;
    @FXML private Button takeQuizButton;
    @FXML private Button copyNotesButton;

    private final DocumentService documentService = new DocumentService();
    private final TopicService topicService = new TopicService();
    private final RevisionService revisionService = new RevisionService();

    private final Map<String, Topic> treeItemToTopicMap = new HashMap<>();
    private Topic currentTopic;
    private List<Document> userDocuments;

    @FXML
    public void initialize() {
        User user = ViewNavigator.getCurrentUser();
        if (user != null) {
            loadDocuments(user.getId(), null, null);
        }

        documentSelector.setOnAction(e -> {
            Document selected = documentSelector.getValue();
            if (selected != null) {
                loadTopicTree(selected.getId(), null);
            }
        });

        topicTree.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                Topic topic = treeItemToTopicMap.get(newVal.getValue());
                if (topic != null) {
                    displayTopicDetails(topic);
                }
            }
        });

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            filterTopics(newVal);
        });
    }

    public void loadDocument(int documentId, Integer topicId) {
        User user = ViewNavigator.getCurrentUser();
        if (user != null) {
            loadDocuments(user.getId(), documentId, topicId);
        }
    }

    private void loadDocuments(int userId, Integer initialDocId, Integer initialTopicId) {
        new Thread(() -> {
            try {
                userDocuments = documentService.getDocumentsForUser(userId);
                Platform.runLater(() -> {
                    documentSelector.getItems().clear();
                    documentSelector.getItems().addAll(userDocuments);

                    if (!userDocuments.isEmpty()) {
                        Document toSelect = userDocuments.get(0);
                        if (initialDocId != null) {
                            for (Document d : userDocuments) {
                                if (d.getId() == initialDocId) {
                                    toSelect = d;
                                    break;
                                }
                            }
                        }
                        documentSelector.setValue(toSelect);
                        loadTopicTree(toSelect.getId(), initialTopicId);
                    }
                });
            } catch (Exception e) {
                logger.error("Failed to load documents: {}", e.getMessage(), e);
            }
        }).start();
    }

    private void loadTopicTree(int documentId, Integer selectTopicId) {
        new Thread(() -> {
            try {
                List<Topic> topics = topicService.getTopicsByDocumentId(documentId);
                Platform.runLater(() -> {
                    treeItemToTopicMap.clear();
                    Document currentDoc = documentSelector.getValue();
                    String rootLabel = currentDoc != null ? currentDoc.getTitle() : "Document";
                    TreeItem<String> rootItem = new TreeItem<>(rootLabel);
                    rootItem.setExpanded(true);

                    TreeItem<String> itemToSelect = null;

                    for (Topic t : topics) {
                        String topicKey = t.getName();
                        TreeItem<String> topicItem = new TreeItem<>(topicKey);
                        treeItemToTopicMap.put(topicKey, t);

                        for (SubTopic st : t.getSubtopics()) {
                            TreeItem<String> subItem = new TreeItem<>(st.getName());
                            topicItem.getChildren().add(subItem);
                            // Also map subtopic selection to parent topic
                            treeItemToTopicMap.put(st.getName(), t);
                        }

                        topicItem.setExpanded(true);
                        rootItem.getChildren().add(topicItem);

                        if (selectTopicId != null && t.getId() == selectTopicId) {
                            itemToSelect = topicItem;
                        }
                    }

                    topicTree.setRoot(rootItem);
                    if (itemToSelect != null) {
                        topicTree.getSelectionModel().select(itemToSelect);
                    } else if (!rootItem.getChildren().isEmpty()) {
                        topicTree.getSelectionModel().select(rootItem.getChildren().get(0));
                    }
                });
            } catch (Exception e) {
                logger.error("Failed to load topics for tree: {}", e.getMessage(), e);
            }
        }).start();
    }

    private void displayTopicDetails(Topic topic) {
        this.currentTopic = topic;
        topicTitleLabel.setText(topic.getName());
        priorityBadge.setText(topic.getPriorityLevel().name() + " PRIORITY");

        // Set priority style
        priorityBadge.getStyleClass().removeAll("badge-critical", "badge-high", "badge-medium");
        switch (topic.getPriorityLevel()) {
            case CRITICAL -> priorityBadge.getStyleClass().add("badge-critical");
            case HIGH -> priorityBadge.getStyleClass().add("badge-high");
            default -> priorityBadge.getStyleClass().add("badge-medium");
        }

        // Summary
        if (topic.getSummary() != null) {
            summaryArea.setText(topic.getSummary().getSummaryText());
        } else if (topic.getOverview() != null) {
            summaryArea.setText(topic.getOverview());
        } else {
            summaryArea.setText("No summary generated yet for this topic.");
        }

        // Important Points
        importantPointsBox.getChildren().clear();
        for (ImportantPoint pt : topic.getImportantPoints()) {
            Label ptLabel = new Label("• " + pt.getPointText());
            ptLabel.setWrapText(true);
            ptLabel.setStyle("-fx-text-fill: #e2e8f0; -fx-padding: 3 0 3 0; -fx-font-size: 13px;");
            importantPointsBox.getChildren().add(ptLabel);
        }
        if (topic.getImportantPoints().isEmpty()) {
            importantPointsBox.getChildren().add(new Label("No key takeaways extracted."));
        }

        // Definitions
        definitionsBox.getChildren().clear();
        for (Definition def : topic.getDefinitions()) {
            VBox defItem = new VBox(2);
            defItem.setStyle("-fx-background-color: #0f172a; -fx-padding: 8; -fx-background-radius: 6;");
            Label termLabel = new Label(def.getTerm());
            termLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #38bdf8;");
            Label textLabel = new Label(def.getDefinitionText());
            textLabel.setWrapText(true);
            textLabel.setStyle("-fx-text-fill: #cbd5e1;");
            defItem.getChildren().addAll(termLabel, textLabel);
            definitionsBox.getChildren().add(defItem);
        }
        if (topic.getDefinitions().isEmpty()) {
            definitionsBox.getChildren().add(new Label("No explicit definitions found in this section."));
        }

        // Formulas
        formulasBox.getChildren().clear();
        for (Formula f : topic.getFormulas()) {
            VBox formItem = new VBox(2);
            formItem.setStyle("-fx-background-color: #0f172a; -fx-padding: 8; -fx-background-radius: 6;");
            Label exprLabel = new Label(f.getExpression());
            exprLabel.setStyle("-fx-font-family: 'Consolas', monospace; -fx-font-weight: bold; -fx-text-fill: #34d399;");
            Label expLabel = new Label(f.getExplanation());
            expLabel.setWrapText(true);
            expLabel.setStyle("-fx-text-fill: #94a3b8;");
            formItem.getChildren().addAll(exprLabel, expLabel);
            formulasBox.getChildren().add(formItem);
        }
        if (topic.getFormulas().isEmpty()) {
            formulasBox.getChildren().add(new Label("No formulas or equations detected in this section."));
        }

        // Keywords
        keywordsPane.getChildren().clear();
        for (Keyword kw : topic.getKeywords()) {
            Label kwLabel = new Label(kw.getKeyword() + " (" + kw.getOccurrenceCount() + ")");
            kwLabel.getStyleClass().add("keyword-tag");
            keywordsPane.getChildren().add(kwLabel);
        }

        // Refresh revision status
        User user = ViewNavigator.getCurrentUser();
        if (user != null) {
            new Thread(() -> {
                try {
                    var recOpt = revisionService.getRevisionRecordsForUser(user.getId()).stream()
                            .filter(r -> r.getTopicId() == topic.getId()).findFirst();
                    Platform.runLater(() -> {
                        if (recOpt.isPresent()) {
                            var rec = recOpt.get();
                            revisionBadge.setText(rec.getStatus().getLabel() + " (Score: " + (int)rec.getRevisionScore() + "%)");
                        } else {
                            revisionBadge.setText("Not Reviewed Yet");
                        }
                    });
                } catch (Exception ignored) {}
            }).start();
        }
    }

    @FXML
    private void handleMarkAsRevised() {
        if (currentTopic == null) return;
        User user = ViewNavigator.getCurrentUser();
        if (user == null) return;

        new Thread(() -> {
            try {
                RevisionRecord rec = revisionService.markAsRevised(user.getId(), currentTopic.getId(), "Revised via Notes Viewer");
                Platform.runLater(() -> {
                    revisionBadge.setText(rec.getStatus().getLabel() + " (Score: " + (int)rec.getRevisionScore() + "%)");
                    ViewNavigator.showAlert(Alert.AlertType.INFORMATION, "Topic Revised!",
                            "Topic marked as revised! Your new revision retention score is " + (int)rec.getRevisionScore() + "%.");
                });
            } catch (Exception e) {
                logger.error("Failed to mark as revised: {}", e.getMessage(), e);
            }
        }).start();
    }

    @FXML
    private void handleTakeQuiz() {
        if (currentTopic != null) {
            ViewNavigator.showQuiz(currentTopic.getId());
        }
    }

    @FXML
    private void handleCopyNotes() {
        if (currentTopic == null) return;
        StringBuilder sb = new StringBuilder();
        sb.append("=== ").append(currentTopic.getName()).append(" ===\n\n");
        sb.append("SUMMARY:\n").append(summaryArea.getText()).append("\n\n");
        sb.append("IMPORTANT POINTS:\n");
        for (ImportantPoint p : currentTopic.getImportantPoints()) {
            sb.append("• ").append(p.getPointText()).append("\n");
        }
        sb.append("\nDEFINITIONS:\n");
        for (Definition d : currentTopic.getDefinitions()) {
            sb.append("• ").append(d.getTerm()).append(": ").append(d.getDefinitionText()).append("\n");
        }
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(sb.toString());
        clipboard.setContent(content);
        ViewNavigator.showAlert(Alert.AlertType.INFORMATION, "Copied!", "Notes copied to clipboard.");
    }

    private void filterTopics(String query) {
        if (query == null || query.isBlank()) {
            Document doc = documentSelector.getValue();
            if (doc != null) loadTopicTree(doc.getId(), null);
            return;
        }
        String q = query.toLowerCase();
        for (TreeItem<String> item : topicTree.getRoot().getChildren()) {
            boolean match = item.getValue().toLowerCase().contains(q);
            item.setExpanded(match);
        }
    }

    @FXML private void handleBackToDashboard() { ViewNavigator.showDashboard(); }
    @FXML private void handleGoToUpload() { ViewNavigator.showUpload(); }
    @FXML private void handleGoToAskNotes() { ViewNavigator.showAskNotes(); }
    @FXML private void handleGoToRevision() { ViewNavigator.showRevision(); }
    @FXML private void handleGoToCSE1007() { ViewNavigator.showCSE1007Demo(); }
    @FXML private void handleGoToSettings() { ViewNavigator.showSettings(); }
    @FXML private void handleLogout() { ViewNavigator.showLogin(); }
}
