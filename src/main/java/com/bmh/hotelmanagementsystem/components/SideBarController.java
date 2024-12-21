package com.bmh.hotelmanagementsystem.components;

import com.bmh.hotelmanagementsystem.BMHApplication;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class SideBarController {

    @FXML
    private VBox side_bar;

    @FXML
    private Button side_bar_rooms;
//    public void setPrimaryStage(Stage primaryStage) {
//        this.primaryStage = primaryStage;
//    }

    @FXML
    protected void home() throws IOException {
        Stage primaryStage = (Stage) side_bar_rooms.getScene().getWindow() ;

        FXMLLoader fxmlLoader = new FXMLLoader(BMHApplication.class.getResource("/com/bmh/hotelmanagementsystem/home-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        scene.getStylesheets().add(getClass().getResource("/com/bmh/hotelmanagementsystem/css/styles.css").toExternalForm());
        primaryStage.setTitle("BMH");
        primaryStage.setHeight(1080);
        primaryStage.setWidth(1920);
        primaryStage.setScene(scene);
//        stage.setFullScreen(true);
        primaryStage.show();
    }

    @FXML
    protected void rooms() throws IOException {
        Stage primaryStage = (Stage) side_bar_rooms.getScene().getWindow() ;

        FXMLLoader fxmlLoader = new FXMLLoader(BMHApplication.class.getResource("/com/bmh/hotelmanagementsystem/room-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        scene.getStylesheets().add(getClass().getResource("/com/bmh/hotelmanagementsystem/css/styles.css").toExternalForm());
        primaryStage.setTitle("BMH - Rooms");
        primaryStage.setHeight(1080);
        primaryStage.setWidth(1920);
        primaryStage.setScene(scene);
//        stage.setFullScreen(true);
        primaryStage.show();
    }

}
