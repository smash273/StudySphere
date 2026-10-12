package com.studysphere.javafx.controller;

import com.studysphere.javafx.ViewNavigator;
import com.studysphere.model.*;
import com.studysphere.service.QuizService;
import com.studysphere.service.TopicService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for interactive Quiz assessment.
 */
public class QuizController {
    private static final Logger logger = LoggerFactory.getLogger(QuizController.class);

    @FXML private ComboBox<Topic> topicSelector;
    @FXML private Label topicTitleLabel;
    @FXML private VBox questionsContainer;
    @FXML private Button submitButton;
    @FXML private Label resultScoreLabel;

    private final QuizService quizService = new QuizService();
    private final TopicService topicService = new TopicService();

    private Quiz currentQuiz;
    private final Map<Integer, ToggleGroup> questionToggleGroups = new HashMap<>();

    @FXML
    public void initialize() {
        resultScoreLabel.setVisible(false);
        User user = ViewNavigator.getCurrentUser();
        if (user != null) {
            loadTopics(user.getId(), null);
        }

        topicSelector.setOnAction(e -> {
            Topic sel = topicSelector.getValue();
            if (sel != null) {
                loadQuizForTopic(sel.getId());
            }
        });
    }

    public void loadQuizForTopic(int topicId) {
        new Thread(() -> {
            try {
                Quiz quiz = quizService.getOrGenerateQuiz(topicId, 5);
                Platform.runLater(() -> renderQuiz(quiz));
            } catch (Exception e) {
                logger.error("Failed to load quiz: {}", e.getMessage(), e);
                Platform.runLater(() -> ViewNavigator.showAlert(Alert.AlertType.ERROR, "Quiz Error", e.getMessage()));
            }
        }).start();
    }

    private void loadTopics(int userId, Integer initialTopicId) {
        new Thread(() -> {
            try {
                List<Topic> topics = topicService.getTopicsByUserId(userId);
                Platform.runLater(() -> {
                    topicSelector.getItems().setAll(topics);
                    if (!topics.isEmpty()) {
                        Topic toSelect = topics.get(0);
                        if (initialTopicId != null) {
                            for (Topic t : topics) {
                                if (t.getId() == initialTopicId) {
                                    toSelect = t;
                                    break;
                                }
                            }
                        }
                        topicSelector.setValue(toSelect);
                        loadQuizForTopic(toSelect.getId());
                    }
                });
            } catch (Exception e) {
                logger.error("Failed to load topics: {}", e.getMessage(), e);
            }
        }).start();
    }

    private void renderQuiz(Quiz quiz) {
        this.currentQuiz = quiz;
        topicTitleLabel.setText("Quiz: " + quiz.getTopicName());
        questionsContainer.getChildren().clear();
        questionToggleGroups.clear();
        resultScoreLabel.setVisible(false);
        submitButton.setDisable(false);

        int qNum = 1;
        for (Question q : quiz.getQuestions()) {
            VBox card = new VBox(10);
            card.getStyleClass().add("card");
            card.setStyle("-fx-padding: 16; -fx-margin: 0 0 14 0;");

            Label qText = new Label(qNum++ + ". " + q.getQuestionText());
            qText.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #f8fafc;");
            qText.setWrapText(true);
            card.getChildren().add(qText);

            ToggleGroup group = new ToggleGroup();
            questionToggleGroups.put(q.getId(), group);

            if ("TRUE_FALSE".equalsIgnoreCase(q.getQuestionType())) {
                RadioButton optA = new RadioButton("True");
                optA.setToggleGroup(group);
                optA.setUserData("True");
                RadioButton optB = new RadioButton("False");
                optB.setToggleGroup(group);
                optB.setUserData("False");
                card.getChildren().addAll(optA, optB);
            } else {
                if (q.getOptionA() != null) {
                    RadioButton a = new RadioButton("A) " + q.getOptionA());
                    a.setToggleGroup(group);
                    a.setUserData(q.getOptionA());
                    card.getChildren().add(a);
                }
                if (q.getOptionB() != null) {
                    RadioButton b = new RadioButton("B) " + q.getOptionB());
                    b.setToggleGroup(group);
                    b.setUserData(q.getOptionB());
                    card.getChildren().add(b);
                }
                if (q.getOptionC() != null) {
                    RadioButton c = new RadioButton("C) " + q.getOptionC());
                    c.setToggleGroup(group);
                    c.setUserData(q.getOptionC());
                    card.getChildren().add(c);
                }
                if (q.getOptionD() != null) {
                    RadioButton d = new RadioButton("D) " + q.getOptionD());
                    d.setToggleGroup(group);
                    d.setUserData(q.getOptionD());
                    card.getChildren().add(d);
                }
            }

            questionsContainer.getChildren().add(card);
        }
    }

    @FXML
    private void handleSubmitQuiz() {
        if (currentQuiz == null) return;
        User user = ViewNavigator.getCurrentUser();
        if (user == null) return;

        List<Answer> answers = new ArrayList<>();
        for (Question q : currentQuiz.getQuestions()) {
            ToggleGroup group = questionToggleGroups.get(q.getId());
            String selected = null;
            if (group != null && group.getSelectedToggle() != null) {
                selected = (String) group.getSelectedToggle().getUserData();
            }
            if (selected == null) selected = "";
            answers.add(new Answer(q.getId(), selected));
        }

        submitButton.setDisable(true);

        new Thread(() -> {
            try {
                QuizAttempt attempt = quizService.submitQuiz(user.getId(), currentQuiz.getTopicId(), answers);
                Platform.runLater(() -> {
                    resultScoreLabel.setText("Your Score: " + (int)attempt.getScore() + "% (" +
                            attempt.getCorrectCount() + " of " + attempt.getTotalQuestions() + " Correct)");
                    resultScoreLabel.setVisible(true);
                    ViewNavigator.showAlert(Alert.AlertType.INFORMATION, "Quiz Results",
                            "Score: " + (int)attempt.getScore() + "%\n" +
                            (attempt.getScore() >= 60.0 ? "Great job! Topic marked as reviewed." : "Keep studying and review the weak concepts."));
                });
            } catch (Exception e) {
                logger.error("Failed to submit quiz: {}", e.getMessage(), e);
            }
        }).start();
    }

    @FXML private void handleBackToDashboard() { ViewNavigator.showDashboard(); }
    @FXML private void handleGoToUpload() { ViewNavigator.showUpload(); }
    @FXML private void handleGoToNotes() { ViewNavigator.showNotesViewer(null, null); }
    @FXML private void handleGoToAskNotes() { ViewNavigator.showAskNotes(); }
    @FXML private void handleGoToRevision() { ViewNavigator.showRevision(); }
    @FXML private void handleGoToCSE1007() { ViewNavigator.showCSE1007Demo(); }
    @FXML private void handleGoToSettings() { ViewNavigator.showSettings(); }
    @FXML private void handleLogout() { ViewNavigator.showLogin(); }
}
