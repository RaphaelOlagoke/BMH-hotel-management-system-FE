package com.bmh.hotelmanagementsystem.RoomManagement;

import com.bmh.hotelmanagementsystem.Controller;
import com.bmh.hotelmanagementsystem.Utils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class RoomController extends Controller {

    private Stage primaryStage;

    @FXML
    private Button availableRooms;
    @FXML
    private Button occupiedRooms;
    @FXML
    private Button cleaningRooms;
    @FXML
    private Button maintenanceRooms;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    public void initialize()  {
        availableRooms.setOnAction(event -> {
            availableRooms();
        });
        occupiedRooms.setOnAction(event -> {
            occupiedRooms();
        });
        cleaningRooms.setOnAction(event -> {
            cleaningRooms();
        });
        maintenanceRooms.setOnAction(event -> {
            maintenanceRooms();
        });
    }


    @FXML
    protected void availableRooms(){
        try {
            Utils utils = new Utils();
            utils.switchScreen("/com/bmh/hotelmanagementsystem/room/availableRooms-view.fxml", primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void occupiedRooms(){
        try {
            Utils utils = new Utils();
            utils.switchScreen("/com/bmh/hotelmanagementsystem/room/occupiedRooms-view.fxml", primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void cleaningRooms(){
        try {
            Utils utils = new Utils();
            utils.switchScreen("/com/bmh/hotelmanagementsystem/room/cleaningRooms-view.fxml", primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void maintenanceRooms(){
        try {
            Utils utils = new Utils();
            utils.switchScreen("/com/bmh/hotelmanagementsystem/room/maintenanceRooms-view.fxml", primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
