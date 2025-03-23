package com.bmh.hotelmanagementsystem.components;

import com.bmh.hotelmanagementsystem.*;
import com.bmh.hotelmanagementsystem.BackendService.utils.AuthFileCache;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
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
    private HBox guestLogHbox;
    @FXML
    private HBox roomsHbox;
    @FXML
    private HBox roomServiceHbox;


    @FXML
    private HBox restaurantHbox;
    @FXML
    private HBox houseKeepingHbox;
    @FXML
    private HBox refundHbox;

    @FXML
    private HBox invoiceHbox;
    @FXML
    private HBox inventoryHbox;
    @FXML
    private HBox discountHbox;

    @FXML
    private HBox usersHbox;
    @FXML
    private HBox settingsHbox;

    @FXML
    private Label guestLogLabel;
    @FXML
    private Label roomsLabel;
    @FXML
    private Label roomServiceLabel;

    @FXML
    private Label restaurantLabel;
    @FXML
    private Label houseKeepingLabel;
    @FXML
    private Label refundLabel;

    @FXML
    private Label invoiceLabel;
    @FXML
    private Label inventoryLabel;

    @FXML
    private Label discountLabel;
    @FXML
    private Label usersLabel;
    @FXML
    private Label settingsLabel;


    private HBox currentlySelectedHBox = guestLogHbox;
    private Label currentlySelectedLabel = guestLogLabel;

    public void onRoomSelected() {
        HBox selectedHBox = roomsHbox;
        Label selectedLabel = roomsLabel;
        if (currentlySelectedHBox != null) {
            currentlySelectedHBox.getStyleClass().add("side-bar-button");
            currentlySelectedHBox.getStyleClass().remove("active-side-bar-button");
            currentlySelectedLabel.getStyleClass().remove("active-button-label");
        }

        selectedHBox.getStyleClass().remove("side-bar-button");
        selectedHBox.getStyleClass().add("active-side-bar-button");
        selectedLabel.getStyleClass().add("active-button-label");

        currentlySelectedHBox = selectedHBox;
        currentlySelectedLabel = selectedLabel;

    }


    private void onOptionSelected(HBox selectedHBox, Label selectedLabel) {
        if (currentlySelectedHBox != null) {
            currentlySelectedHBox.getStyleClass().add("side-bar-button");
            currentlySelectedHBox.getStyleClass().remove("active-side-bar-button");
            currentlySelectedLabel.getStyleClass().remove("active-button-label");
        }

        selectedHBox.getStyleClass().remove("side-bar-button");
        selectedHBox.getStyleClass().add("active-side-bar-button");
        selectedLabel.getStyleClass().add("active-button-label");

        currentlySelectedHBox = selectedHBox;
        currentlySelectedLabel = selectedLabel;

    }

    @FXML
    protected void home() throws IOException {
        onOptionSelected(guestLogHbox, guestLogLabel);

        Stage primaryStage = (Stage) side_bar_rooms.getScene().getWindow() ;

        FXMLLoader fxmlLoader = new FXMLLoader(BMHApplication.class.getResource("/com/bmh/hotelmanagementsystem/room/admin-guest-logs-view.fxml"));
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
    protected void reservation(){
        Stage primaryStage = (Stage) side_bar_rooms.getScene().getWindow() ;
        try {
            Utils utils = new Utils();
            utils.switchScreen("/com/bmh/hotelmanagementsystem/room/reservation-log-view.fxml", primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void restaurant() throws IOException {
        onOptionSelected(restaurantHbox, restaurantLabel);

        Stage primaryStage = (Stage) side_bar_rooms.getScene().getWindow() ;

        FXMLLoader fxmlLoader = new FXMLLoader(BMHApplication.class.getResource("/com/bmh/hotelmanagementsystem/restaurant/admin-restaurant-view.fxml"));
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
        onOptionSelected(roomsHbox, roomsLabel);

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
        onOptionSelected(roomServiceHbox, roomServiceLabel);

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
        onOptionSelected(houseKeepingHbox, houseKeepingLabel);

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
        onOptionSelected(invoiceHbox, invoiceLabel);

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
        onOptionSelected(refundHbox, refundLabel);

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
        onOptionSelected(discountHbox, discountLabel);

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
        onOptionSelected(usersHbox, usersLabel);

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
        onOptionSelected(settingsHbox, settingsLabel);

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
        onOptionSelected(inventoryHbox, inventoryLabel);

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
