package com.studysphere.javafx;

import com.studysphere.model.Student;
import com.studysphere.model.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Screen transition and session coordinator for the JavaFX Desktop Client.
 */
public class ViewNavigator {
    private static final Logger logger = LoggerFactory.getLogger(ViewNavigator.class);

    private static Stage primaryStage;
    private static User currentUser;
    private static Student currentStudent;

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
        if (user instanceof Student s) {
            currentStudent = s;
        } else {
            currentStudent = null;
        }
    }

    public static Student getCurrentStudent() {
        return currentStudent;
    }

    public static void showLogin() {
        currentUser = null;
        currentStudent = null;
        loadScene("/com/studysphere/javafx/fxml/login.fxml", "StudySphere — Student Login", 500, 600);
    }

    public static void showRegister() {
        loadScene("/com/studysphere/javafx/fxml/register.fxml", "StudySphere — Student Registration", 550, 700);
    }

    public static void showDashboard() {
        loadScene("/com/studysphere/javafx/fxml/dashboard.fxml", "StudySphere — Dashboard", 1150, 750);
    }

    public static void showUpload() {
        loadScene("/com/studysphere/javafx/fxml/upload.fxml", "StudySphere — Upload Study Material", 1150, 750);
    }

    public static void showNotesViewer(Integer documentId, Integer topicId) {
        try {
            FXMLLoader loader = new FXMLLoader(ViewNavigator.class.getResource("/com/studysphere/javafx/fxml/notes_viewer.fxml"));
            Parent root = loader.load();
            com.studysphere.javafx.controller.NotesViewerController controller = loader.getController();
            if (controller != null && documentId != null) {
                controller.loadDocument(documentId, topicId);
            }
            setScene(root, "StudySphere — Academic Notes Viewer", 1200, 800);
        } catch (IOException e) {
            logger.error("Failed to load notes viewer: {}", e.getMessage(), e);
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load Notes Viewer: " + e.getMessage());
        }
    }

    public static void showAskNotes() {
        loadScene("/com/studysphere/javafx/fxml/ask_notes.fxml", "StudySphere — Ask My Notes", 1150, 750);
    }

    public static void showRevision() {
        loadScene("/com/studysphere/javafx/fxml/revision.fxml", "StudySphere — Revision Dashboard", 1150, 750);
    }

    public static void showQuiz(Integer topicId) {
        try {
            FXMLLoader loader = new FXMLLoader(ViewNavigator.class.getResource("/com/studysphere/javafx/fxml/quiz.fxml"));
            Parent root = loader.load();
            com.studysphere.javafx.controller.QuizController controller = loader.getController();
            if (controller != null && topicId != null) {
                controller.loadQuizForTopic(topicId);
            }
            setScene(root, "StudySphere — Revision Quiz", 1150, 750);
        } catch (IOException e) {
            logger.error("Failed to load quiz view: {}", e.getMessage(), e);
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load Quiz: " + e.getMessage());
        }
    }

    public static void showCSE1007Demo() {
        loadScene("/com/studysphere/javafx/fxml/cse1007_demo.fxml", "StudySphere — CSE1007 Technical Demonstration Mode", 1150, 750);
    }

    public static void showSettings() {
        loadScene("/com/studysphere/javafx/fxml/settings.fxml", "StudySphere — System Settings & AI Configuration", 1150, 750);
    }

    private static void loadScene(String fxmlPath, String title, double width, double height) {
        try {
            FXMLLoader loader = new FXMLLoader(ViewNavigator.class.getResource(fxmlPath));
            Parent root = loader.load();
            setScene(root, title, width, height);
        } catch (IOException e) {
            logger.error("Failed to load FXML {}: {}", fxmlPath, e.getMessage(), e);
            showAlert(Alert.AlertType.ERROR, "View Error", "Could not load screen: " + e.getMessage());
        }
    }

    private static void setScene(Parent root, String title, double width, double height) {
        if (primaryStage == null) return;
        Scene scene = primaryStage.getScene();
        if (scene == null) {
            scene = new Scene(root, width, height);
            primaryStage.setScene(scene);
        } else {
            scene.setRoot(root);
            primaryStage.setWidth(Math.max(primaryStage.getWidth(), width));
            primaryStage.setHeight(Math.max(primaryStage.getHeight(), height));
        }
        primaryStage.setTitle(title);
        primaryStage.show();
    }

    public static void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
