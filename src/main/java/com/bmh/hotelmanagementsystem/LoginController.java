package com.bmh.hotelmanagementsystem;

import com.bmh.hotelmanagementsystem.components.SideBarController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController{


    private Stage primaryStage;
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }


    @FXML
    private Button login_button;

    @FXML
    private TextField username;

    @FXML
    protected void onLoginButtonClick() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(BMHApplication.class.getResource("home-view.fxml"));

        if(username.getText().equalsIgnoreCase("admin")) {
            fxmlLoader = new FXMLLoader(BMHApplication.class.getResource("/com/bmh/hotelmanagementsystem/room/room-management-view.fxml"));
        }
        else if(username.getText().equalsIgnoreCase("restaurant")){
            fxmlLoader = new FXMLLoader(BMHApplication.class.getResource("/com/bmh/hotelmanagementsystem/Restaurant/restaurant-view.fxml"));
        }


        ScrollPane homeRoot = fxmlLoader.load();
        Scene scene = new Scene(homeRoot);
        scene.getStylesheets().add(getClass().getResource("css/styles.css").toExternalForm());

//        primaryStage.setTitle("BMH");
//        primaryStage.setHeight(1080);
//        primaryStage.setWidth(1920);
        primaryStage.setScene(scene);
//        primaryStage.setMaximized(true);
//        primaryStage.setFullScreen(true);
//        primaryStage.setFullScreenExitHint("");

        Controller homeController = fxmlLoader.getController();
        homeController.setPrimaryStage(primaryStage);

        primaryStage.show();
//        primaryStage.setFullScreen(true);
    }
}