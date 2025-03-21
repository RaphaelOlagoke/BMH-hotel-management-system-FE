package com.bmh.hotelmanagementsystem;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneManager {
    private static Stage primaryStage;

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static void switchToLogin() {
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource("login-view.fxml"));
                Parent root = loader.load();

                // Get the LoginController from the loader and set the primary stage
                LoginController loginController = loader.getController();
                loginController.setPrimaryStage(primaryStage);

                // Set the scene for the primary stage
                primaryStage.setScene(new Scene(root));
                primaryStage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
