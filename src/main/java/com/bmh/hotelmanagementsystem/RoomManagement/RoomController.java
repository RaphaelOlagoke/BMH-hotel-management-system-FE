package com.bmh.hotelmanagementsystem.RoomManagement;

import com.bmh.hotelmanagementsystem.BackendService.RestClient;
import com.bmh.hotelmanagementsystem.BackendService.entities.ApiResponse;
import com.bmh.hotelmanagementsystem.BackendService.entities.Room;
import com.bmh.hotelmanagementsystem.Controller;
import com.bmh.hotelmanagementsystem.Utils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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

    @FXML
    private Label availableRoomsCount;
    @FXML
    private Label occupiedRoomsCount;
    @FXML
    private Label cleaningRoomsCount;
    @FXML
    private Label maintenanceRoomsCount;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    public void initialize()  {
        try {
            String responseAvailable = RestClient.get("/room/status?roomStatus=Available");
            String responseOccupied = RestClient.get("/room/status?roomStatus=Occupied");
            String responseCleaning = RestClient.get("/room/status?roomStatus=Cleaning");
            String responseMaintenance = RestClient.get("/room/status?roomStatus=Maintenance");

            ObjectMapper objectMapper = new ObjectMapper();

            // Convert JSON string to ApiResponse
            ApiResponse<Room> apiResponseAvailable = objectMapper.readValue(responseAvailable, new TypeReference<ApiResponse<Room>>() {});
            ApiResponse<Room> apiResponseOccupied = objectMapper.readValue(responseOccupied, new TypeReference<ApiResponse<Room>>() {});
            ApiResponse<Room> apiResponseCleaning = objectMapper.readValue(responseCleaning, new TypeReference<ApiResponse<Room>>() {});
            ApiResponse<Room> apiResponseMaintenance = objectMapper.readValue(responseMaintenance, new TypeReference<ApiResponse<Room>>() {});

            // Extract the list of rooms
            if (apiResponseAvailable.getData() != null) {
                availableRoomsCount.setText(String.valueOf(apiResponseAvailable.getData().size()));
            }
            if (apiResponseOccupied.getData() != null) {
                occupiedRoomsCount.setText(String.valueOf(apiResponseOccupied.getData().size()));
            }
            if (apiResponseCleaning.getData() != null) {
                cleaningRoomsCount.setText(String.valueOf(apiResponseCleaning.getData().size()));
            }
            if (apiResponseMaintenance.getData() != null) {
                maintenanceRoomsCount.setText(String.valueOf(apiResponseMaintenance.getData().size()));
            }

        } catch (Exception e) {
            System.out.println(e);
        }

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
