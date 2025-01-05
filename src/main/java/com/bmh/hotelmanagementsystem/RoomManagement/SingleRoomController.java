package com.bmh.hotelmanagementsystem.RoomManagement;

import com.bmh.hotelmanagementsystem.BackendService.RestClient;
import com.bmh.hotelmanagementsystem.BackendService.entities.*;
import com.bmh.hotelmanagementsystem.Controller;
import com.bmh.hotelmanagementsystem.Utils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SingleRoomController extends Controller {

    private Stage primaryStage;

    private Room currentRoom;

    @FXML
    private Button back;

    @FXML
    private Label room_number;
    @FXML
    private Label room_type;
//    @FXML
//    private Label room_price;
    @FXML
    private Label room_status;
    @FXML
    private Label guest_name;
    @FXML
    private Label check_in_date;
    @FXML
    private Label payment_status;
    @FXML
    private Label cleaning_assigned_to;
    @FXML
    private Label cleaning_last_assigned_to;
    @FXML
    private Label cleaning_assigned_on;
    @FXML
    private Label maintenance_description;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setRoom(Room room) {
        this.currentRoom = room;
        updateRoomDetails();
    }

    private void updateRoomDetails() {
        GuestLog guestLog;

        try {
            String response = RestClient.get("/guestLog/find?roomNumber=" + currentRoom.getRoomNumber());

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());

            // Convert JSON string to ApiResponse
            ApiResponseSingleData<GuestLog> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponseSingleData<GuestLog>>() {});

            // Extract the list of rooms
            guestLog = apiResponse.getData();

        } catch (Exception e) {
            System.out.println(e);
            guestLog = null;
        }
        if (currentRoom != null) {
            room_number.setText("Room " + currentRoom.getRoomNumber());
            room_type.setText("Room Type: " + currentRoom.getRoomType());
            room_status.setText("Status: " + currentRoom.getRoomStatus());
            if(guestLog == null){
                guest_name.setText("Name: None");
            }
            else {
                guest_name.setText("Name:   " + guestLog.getGuestName());
                check_in_date.setText("Check in Date:   " + guestLog.getCheckInDate());
                payment_status.setText("Payment Status:   " + guestLog.getPaymentStatus());
            }
        }
    }

    @FXML
    public void initialize() throws IOException {

        back.setOnAction(event -> goBack());
    }




    public void goBack(){
        try {
            Utils utils = new Utils();
            utils.switchScreen("/com/bmh/hotelmanagementsystem/room-view.fxml", primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


