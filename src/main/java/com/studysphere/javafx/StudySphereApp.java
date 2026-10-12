package com.studysphere.javafx;

import com.studysphere.config.DatabaseConnectionManager;
import javafx.application.Application;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main JavaFX Desktop Application entry point for StudySphere.
 */
public class StudySphereApp extends Application {
    private static final Logger logger = LoggerFactory.getLogger(StudySphereApp.class);

    @Override
    public void start(Stage primaryStage) {
        logger.info("Starting StudySphere Desktop Application...");
        ViewNavigator.setPrimaryStage(primaryStage);

        // Pre-warm database connection in background
        new Thread(() -> {
            try {
                DatabaseConnectionManager.getInstance().getConnection();
            } catch (Exception e) {
                logger.warn("Database pre-warming warning: {}", e.getMessage());
            }
        }).start();

        // Launch with Login view
        ViewNavigator.showLogin();
    }

    @Override
    public void stop() {
        logger.info("Stopping StudySphere Desktop Application...");
        DatabaseConnectionManager.getInstance().shutdown();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
