package com.bmh.hotelmanagementsystem.RoomManagement;

import com.bmh.hotelmanagementsystem.BMHApplication;
import com.bmh.hotelmanagementsystem.BackendService.utils.AuthFileCache;
import com.bmh.hotelmanagementsystem.HomeController;
import com.bmh.hotelmanagementsystem.TokenStorage;
import com.bmh.hotelmanagementsystem.Utils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class RoomSideBarController {

    @FXML
    private VBox side_bar;

    @FXML
    private Button side_bar_home;

//    public void setPrimaryStage(Stage primaryStage) {
//        this.primaryStage = primaryStage;
//    }

    @FXML
    protected void checkIn(){
        Stage primaryStage = (Stage) side_bar_home.getScene().getWindow() ;
        try {
            Utils utils = new Utils();
            utils.switchScreen("/com/bmh/hotelmanagementsystem/room/checkIn-view.fxml", primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void checkOut(){
        Stage primaryStage = (Stage) side_bar_home.getScene().getWindow() ;
        try {
            Utils utils = new Utils();
            utils.switchScreen("/com/bmh/hotelmanagementsystem/room/checkOut-view.fxml", primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    protected void reservation(){
        Stage primaryStage = (Stage) side_bar_home.getScene().getWindow() ;
        try {
            Utils utils = new Utils();
            utils.switchScreen("/com/bmh/hotelmanagementsystem/room/reservation-log-view.fxml", primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void home() throws IOException {
        Stage primaryStage = (Stage) side_bar_home.getScene().getWindow() ;

        FXMLLoader fxmlLoader = new FXMLLoader(BMHApplication.class.getResource("/com/bmh/hotelmanagementsystem/home-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        HomeController homeController = fxmlLoader.getController();
        homeController.setPrimaryStage(primaryStage);
        scene.getStylesheets().add(getClass().getResource("/com/bmh/hotelmanagementsystem/css/styles.css").toExternalForm());
////        primaryStage.setTitle("BMH");
//        primaryStage.setHeight(1080);
//        primaryStage.setWidth(1920);
        primaryStage.setScene(scene);
//        primaryStage.setMaximized(true);
//        primaryStage.setFullScreen(true);
//        primaryStage.setFullScreenExitHint("");
        primaryStage.show();
    }

    @FXML
    protected void logout() throws IOException {
        TokenStorage.clearCredentials();
        Stage primaryStage = (Stage) side_bar_home.getScene().getWindow() ;

        FXMLLoader fxmlLoader = new FXMLLoader(BMHApplication.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(getClass().getResource("/com/bmh/hotelmanagementsystem/css/styles.css").toExternalForm());
        primaryStage.setScene(scene);
        Utils utils = new Utils();
        utils.setControllerPrimaryStage(fxmlLoader, primaryStage);
        primaryStage.show();
    }

}
