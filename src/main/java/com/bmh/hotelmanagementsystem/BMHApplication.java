package com.bmh.hotelmanagementsystem;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class BMHApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(BMHApplication.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        scene.getStylesheets().add(getClass().getResource("css/styles.css").toExternalForm());

        LoginController loginController = fxmlLoader.getController();
        loginController.setPrimaryStage(stage);

        stage.setTitle("BMH");
        stage.setHeight(1080);
        stage.setWidth(1920);
        stage.setScene(scene);
//        stage.setFullScreen(true);
        stage.show();


    }

    public static void main(String[] args) {
        launch();
    }
}