package com.bmh.hotelmanagementsystem.inventory;

import com.bmh.hotelmanagementsystem.*;
import com.bmh.hotelmanagementsystem.BackendService.enums.LoginDepartment;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class InventorySideBarController {

    @FXML
    private VBox side_bar;

    @FXML
    private Button side_bar_requests;


    @FXML
    protected void home() throws IOException {
        Stage primaryStage = (Stage) side_bar_requests.getScene().getWindow() ;

        FXMLLoader fxmlLoader;

        String[] credentials = TokenStorage.loadCredentials();
        String department = "";
        if (credentials != null) {
            department = credentials[2];
        }
        if(LoginDepartment.valueOf(department) == LoginDepartment.SUPER_ADMIN){
            fxmlLoader = new FXMLLoader(BMHApplication.class.getResource("/com/bmh/hotelmanagementsystem/room/room-management-view.fxml"));
        }
        else if(LoginDepartment.valueOf(department) == LoginDepartment.ADMIN){
            fxmlLoader = new FXMLLoader(BMHApplication.class.getResource("/com/bmh/hotelmanagementsystem/room/general-admin-room-management-view.fxml"));
        }
        else {
            fxmlLoader = new FXMLLoader(BMHApplication.class.getResource("/com/bmh/hotelmanagementsystem/room/general-admin-room-management-view.fxml"));
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
    protected void inventory() throws IOException {
        Stage primaryStage = (Stage) side_bar_requests.getScene().getWindow() ;

        FXMLLoader fxmlLoader = new FXMLLoader(BMHApplication.class.getResource("/com/bmh/hotelmanagementsystem/inventory/inventory-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(getClass().getResource("/com/bmh/hotelmanagementsystem/css/styles.css").toExternalForm());
        primaryStage.setScene(scene);
        Utils utils = new Utils();
        utils.setControllerPrimaryStage(fxmlLoader, primaryStage);
        primaryStage.show();
    }

    @FXML
    protected void request() throws IOException {
        Stage primaryStage = (Stage) side_bar_requests.getScene().getWindow() ;

        FXMLLoader fxmlLoader = new FXMLLoader(BMHApplication.class.getResource("/com/bmh/hotelmanagementsystem/inventory/inventory_request.fxml"));
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
    protected void history() throws IOException {
        Stage primaryStage = (Stage) side_bar_requests.getScene().getWindow() ;

        FXMLLoader fxmlLoader = new FXMLLoader(BMHApplication.class.getResource("/com/bmh/hotelmanagementsystem/inventory/inventory_history.fxml"));
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
}
