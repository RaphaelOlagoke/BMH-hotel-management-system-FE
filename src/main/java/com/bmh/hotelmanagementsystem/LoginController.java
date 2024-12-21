package com.bmh.hotelmanagementsystem;

import com.bmh.hotelmanagementsystem.components.SideBarController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {


    private Stage primaryStage;
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }


    @FXML
    private Button login_button;

    @FXML
    protected void onLoginButtonClick() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(BMHApplication.class.getResource("home-view.fxml"));
        HBox homeRoot = fxmlLoader.load();
        Scene scene = new Scene(homeRoot, 320, 240);
        scene.getStylesheets().add(getClass().getResource("css/styles.css").toExternalForm());

        HomeController homeController = fxmlLoader.getController();
        homeController.setPrimaryStage(primaryStage);



        primaryStage.setTitle("BMH");
        primaryStage.setHeight(1080);
        primaryStage.setWidth(1920);
        primaryStage.setScene(scene);
//        stage.setFullScreen(true);
        primaryStage.show();
    }
}