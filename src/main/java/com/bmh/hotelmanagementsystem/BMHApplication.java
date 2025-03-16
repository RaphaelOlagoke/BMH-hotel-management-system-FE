package com.bmh.hotelmanagementsystem;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class BMHApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        SceneManager.setPrimaryStage(stage);

        FXMLLoader fxmlLoader = new FXMLLoader(BMHApplication.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(getClass().getResource("css/styles.css").toExternalForm());


//        stage.setHeight(stage.getMaxHeight());
//        stage.setWidth(stage.getMaxWidth());
        stage.setScene(scene);
//        stage.setFullScreen(true);
//        stage.setFullScreenExitHint("");
//        stage.setMaximized(true);
        stage.setTitle("BMH");

        LoginController loginController = fxmlLoader.getController();
        loginController.setPrimaryStage(stage);

        stage.show();


    }

    public static void main(String[] args) {
        launch();
    }
}