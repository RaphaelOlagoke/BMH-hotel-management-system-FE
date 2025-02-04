package com.bmh.hotelmanagementsystem;

import com.bmh.hotelmanagementsystem.components.SideBarController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class HomeController extends Controller {

    private Stage primaryStage;

    @FXML
    private Button checkIn;

    @FXML
    private Button checkOut;
    @FXML
    private Button guestLog;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    public void initialize()  {
        checkIn.setOnAction(event -> {
            checkIn();
        });
        checkOut.setOnAction(event -> {
            checkOut();
        });
        guestLog.setOnAction(event -> {
            guestLog();
        });
    }


    @FXML
    protected void checkIn(){
        try {
            Utils utils = new Utils();
            utils.switchScreen("/com/bmh/hotelmanagementsystem/room/checkIn-view.fxml", primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void checkOut(){
        try {
            Utils utils = new Utils();
            utils.switchScreen("/com/bmh/hotelmanagementsystem/room/checkOut-view.fxml", primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void guestLog(){
        try {
            Utils utils = new Utils();
            utils.switchScreen("/com/bmh/hotelmanagementsystem/room/guest-log-view.fxml", primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
