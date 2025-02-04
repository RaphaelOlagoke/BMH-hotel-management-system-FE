package com.bmh.hotelmanagementsystem.RoomManagement;

import com.bmh.hotelmanagementsystem.BackendService.RestClient;
import com.bmh.hotelmanagementsystem.BackendService.entities.ApiResponse;
import com.bmh.hotelmanagementsystem.BackendService.entities.Room.Room;
import com.bmh.hotelmanagementsystem.Controller;
import com.bmh.hotelmanagementsystem.Utils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OccupiedRoomsController extends Controller {

    private Stage primaryStage;


    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    private FlowPane occupied_rooms;

    @FXML
    private Button back;

    @FXML
    public void initialize(){
        List<Room> rooms;

        try {
            String response = RestClient.get("/room/status?roomStatus=Occupied");

            ObjectMapper objectMapper = new ObjectMapper();

            // Convert JSON string to ApiResponse
            ApiResponse<Room> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponse<Room>>() {});

            // Extract the list of rooms
            if (apiResponse.getData() == null){
                rooms = new ArrayList<>();
            }
            else {
                rooms = apiResponse.getData();
            }

        } catch (Exception e) {
            System.out.println(e);
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Error");
            errorAlert.setContentText("Something went wrong. Please try again.");
            errorAlert.showAndWait();
            rooms = null;
        }

//        List<String> itemsFromDatabase = List.of("Item 1", "Item 2", "Item 3");

        if(rooms == null){
            Label message = (Label) occupied_rooms.lookup("#message");
            message.setText("Error");
        }
        else if (rooms.isEmpty()){
            Label message = (Label) occupied_rooms.lookup("#message");
            message.setText("No rooms Occupied");
        }
        else {
            Label message = (Label) occupied_rooms.lookup("#message");
            message.setText("");

            for (Room room : rooms) {
                addButtonToContainer(room);
            }
        }

        back.setOnAction(event -> goBack());
    }

    private void addButtonToContainer(Room room) {
        try {
            FXMLLoader buttonLoader = new FXMLLoader(getClass().getResource("/com/bmh/hotelmanagementsystem/room/room_button.fxml"));
            Button button = buttonLoader.load();

            AnchorPane graphic = (AnchorPane) button.getGraphic();

            Label roomNumberLabel = (Label) graphic.lookup("#room_number");
            Label roomTypeLabel = (Label) graphic.lookup("#room_type");

            roomNumberLabel.setText(String.valueOf(room.getRoomNumber()));
            roomTypeLabel.setText(String.valueOf(room.getRoomType()));

            // Set an action for the button
            button.setOnAction(event -> onButtonClick(room));

            // Add the button to the FlowPane container
            occupied_rooms.getChildren().add(button);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onButtonClick(Room room){
        try {
            Utils utils = new Utils();
            utils.switchScreenWithRoom("/com/bmh/hotelmanagementsystem/room/singleRoom-view.fxml", primaryStage, room);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void goBack(){
        try {
            Utils utils = new Utils();
            utils.switchScreen("/com/bmh/hotelmanagementsystem/room/room-view.fxml", primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
