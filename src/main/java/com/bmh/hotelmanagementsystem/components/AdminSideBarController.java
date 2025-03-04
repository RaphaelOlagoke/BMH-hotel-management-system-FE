package com.bmh.hotelmanagementsystem.components;

import com.bmh.hotelmanagementsystem.*;
import com.bmh.hotelmanagementsystem.BackendService.utils.AuthFileCache;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminSideBarController {

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

        FXMLLoader fxmlLoader = new FXMLLoader(BMHApplication.class.getResource("/com/bmh/hotelmanagementsystem/room/room-management-view.fxml"));
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
    protected void roomService() throws IOException {
        Stage primaryStage = (Stage) side_bar_rooms.getScene().getWindow() ;

        FXMLLoader fxmlLoader = new FXMLLoader(BMHApplication.class.getResource("/com/bmh/hotelmanagementsystem/room/admin-room-service-view.fxml"));
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
    protected void refund() throws IOException {
        Stage primaryStage = (Stage) side_bar_rooms.getScene().getWindow() ;

        FXMLLoader fxmlLoader = new FXMLLoader(BMHApplication.class.getResource("/com/bmh/hotelmanagementsystem/refund/refund-log-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(getClass().getResource("/com/bmh/hotelmanagementsystem/css/styles.css").toExternalForm());
        primaryStage.setScene(scene);
        Utils utils = new Utils();
        utils.setControllerPrimaryStage(fxmlLoader, primaryStage);
        primaryStage.show();
    }

    @FXML
    protected void discount() throws IOException {
        Stage primaryStage = (Stage) side_bar_rooms.getScene().getWindow() ;

        FXMLLoader fxmlLoader = new FXMLLoader(BMHApplication.class.getResource("/com/bmh/hotelmanagementsystem/discount/discount-log-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(getClass().getResource("/com/bmh/hotelmanagementsystem/css/styles.css").toExternalForm());
        primaryStage.setScene(scene);
        Utils utils = new Utils();
        utils.setControllerPrimaryStage(fxmlLoader, primaryStage);
        primaryStage.show();
    }

    @FXML
    protected void employee() throws IOException {
        Stage primaryStage = (Stage) side_bar_rooms.getScene().getWindow() ;

        FXMLLoader fxmlLoader = new FXMLLoader(BMHApplication.class.getResource("/com/bmh/hotelmanagementsystem/employee/employee-log-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(getClass().getResource("/com/bmh/hotelmanagementsystem/css/styles.css").toExternalForm());
        primaryStage.setScene(scene);
        Utils utils = new Utils();
        utils.setControllerPrimaryStage(fxmlLoader, primaryStage);
        primaryStage.show();
    }

    @FXML
    protected void settings() throws IOException {
        Stage primaryStage = (Stage) side_bar_rooms.getScene().getWindow() ;

        FXMLLoader fxmlLoader = new FXMLLoader(BMHApplication.class.getResource("/com/bmh/hotelmanagementsystem/settings/settings-view.fxml"));
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
        Stage primaryStage = (Stage) side_bar_rooms.getScene().getWindow() ;

        FXMLLoader fxmlLoader = new FXMLLoader(BMHApplication.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(getClass().getResource("/com/bmh/hotelmanagementsystem/css/styles.css").toExternalForm());
        primaryStage.setScene(scene);
        Utils utils = new Utils();
        utils.setControllerPrimaryStage(fxmlLoader, primaryStage);
        primaryStage.show();
    }

}
