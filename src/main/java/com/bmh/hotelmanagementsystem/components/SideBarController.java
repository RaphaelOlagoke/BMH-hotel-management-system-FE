package com.bmh.hotelmanagementsystem.components;

import com.bmh.hotelmanagementsystem.*;
import com.bmh.hotelmanagementsystem.BackendService.enums.LoginDepartment;
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

    @FXML
    private Button side_bar_home;

//    public void setPrimaryStage(Stage primaryStage) {
//        this.primaryStage = primaryStage;
//    }

    @FXML
    protected void home() throws IOException {
        Stage primaryStage = (Stage) side_bar_home.getScene().getWindow() ;

        FXMLLoader fxmlLoader;

        String[] credentials = TokenStorage.loadCredentials();
        String department = "";
        if (credentials != null) {
            department = credentials[2];
        }
        if(LoginDepartment.valueOf(department) == LoginDepartment.SUPER_ADMIN){
            fxmlLoader = new FXMLLoader(BMHApplication.class.getResource("/com/bmh/hotelmanagementsystem/room/admin-guest-logs-view.fxml"));
        }
        else if(LoginDepartment.valueOf(department) == LoginDepartment.ACCOUNTS || LoginDepartment.valueOf(department) == LoginDepartment.MANAGER){
            fxmlLoader = new FXMLLoader(BMHApplication.class.getResource("/com/bmh/hotelmanagementsystem/room/general-admin-guest-logs-view.fxml"));
        }
        else if(LoginDepartment.valueOf(department) == LoginDepartment.RESTAURANT_BAR){
            fxmlLoader = new FXMLLoader(BMHApplication.class.getResource("/com/bmh/hotelmanagementsystem/restaurant/restaurant-view.fxml"));
        }
        else {
            fxmlLoader = new FXMLLoader(BMHApplication.class.getResource("/com/bmh/hotelmanagementsystem/home-view.fxml"));
        }

        Scene scene = new Scene(fxmlLoader.load());

        Controller homeController = fxmlLoader.getController();
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
    protected void rooms() throws IOException {
        Stage primaryStage = (Stage) side_bar_rooms.getScene().getWindow() ;

        FXMLLoader fxmlLoader = new FXMLLoader(BMHApplication.class.getResource("/com/bmh/hotelmanagementsystem/room/room-management-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(getClass().getResource("/com/bmh/hotelmanagementsystem/css/styles.css").toExternalForm());
//        primaryStage.setTitle("BMH - Rooms");
//        primaryStage.setHeight(1080);
//        primaryStage.setWidth(1920);
        primaryStage.setScene(scene);
        Utils utils = new Utils();
        utils.setControllerPrimaryStage(fxmlLoader, primaryStage);
//        primaryStage.setMaximized(true);
//        primaryStage.setFullScreen(true);
        primaryStage.show();
    }

    @FXML
    protected void houseKeeping() throws IOException {
        Stage primaryStage = (Stage) side_bar_rooms.getScene().getWindow() ;

        FXMLLoader fxmlLoader = new FXMLLoader(BMHApplication.class.getResource("/com/bmh/hotelmanagementsystem/HouseKeeping/cleaning-log-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(getClass().getResource("/com/bmh/hotelmanagementsystem/css/styles.css").toExternalForm());
        primaryStage.setScene(scene);
        Utils utils = new Utils();
        utils.setControllerPrimaryStage(fxmlLoader, primaryStage);
        primaryStage.show();
    }

    @FXML
    protected void invoice() throws IOException {
        Stage primaryStage = (Stage) side_bar_rooms.getScene().getWindow() ;

        FXMLLoader fxmlLoader = new FXMLLoader(BMHApplication.class.getResource("/com/bmh/hotelmanagementsystem/invoice/invoice-log-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(getClass().getResource("/com/bmh/hotelmanagementsystem/css/styles.css").toExternalForm());
        primaryStage.setScene(scene);
        Utils utils = new Utils();
        utils.setControllerPrimaryStage(fxmlLoader, primaryStage);
        primaryStage.show();
    }

    @FXML
    protected void restaurant() throws IOException {
        Stage primaryStage = (Stage) side_bar_rooms.getScene().getWindow() ;

        FXMLLoader fxmlLoader = new FXMLLoader(BMHApplication.class.getResource("/com/bmh/hotelmanagementsystem/restaurant/restaurant-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(getClass().getResource("/com/bmh/hotelmanagementsystem/css/styles.css").toExternalForm());
        primaryStage.setScene(scene);
        Utils utils = new Utils();
        utils.setControllerPrimaryStage(fxmlLoader, primaryStage);
        primaryStage.show();
    }

    @FXML
    protected void inventory() throws IOException {
        Stage primaryStage = (Stage) side_bar_rooms.getScene().getWindow() ;

        FXMLLoader fxmlLoader = new FXMLLoader(BMHApplication.class.getResource("/com/bmh/hotelmanagementsystem/inventory/inventory-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(getClass().getResource("/com/bmh/hotelmanagementsystem/css/styles.css").toExternalForm());
        primaryStage.setScene(scene);
        Utils utils = new Utils();
        utils.setControllerPrimaryStage(fxmlLoader, primaryStage);
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
