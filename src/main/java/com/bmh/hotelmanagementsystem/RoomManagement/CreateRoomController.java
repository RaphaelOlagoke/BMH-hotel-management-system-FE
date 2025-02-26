package com.bmh.hotelmanagementsystem.RoomManagement;

import com.bmh.hotelmanagementsystem.BackendService.RestClient;
import com.bmh.hotelmanagementsystem.BackendService.entities.ApiResponseSingleData;
import com.bmh.hotelmanagementsystem.BackendService.entities.Restaurant.CreateOrderRequest;
import com.bmh.hotelmanagementsystem.BackendService.entities.Restaurant.Order;
import com.bmh.hotelmanagementsystem.BackendService.entities.Room.Room;
import com.bmh.hotelmanagementsystem.BackendService.enums.RoomStatus;
import com.bmh.hotelmanagementsystem.BackendService.enums.RoomType;
import com.bmh.hotelmanagementsystem.Controller;
import com.bmh.hotelmanagementsystem.Utils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.Optional;

public class CreateRoomController extends Controller {

    private Stage primaryStage;

    private String previousLocation;

    private Object data;

    @FXML
    private ComboBox<String> room_type;
    @FXML
    private ComboBox<String> room_status;
    @FXML
    private TextField room_number;

    @FXML
    private Button create;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setData(Object data, String previousLocation){
        this.data = data;
        this.previousLocation = previousLocation;
    }

    public void initialize() {
        ObservableList<String> roomStatus = FXCollections.observableArrayList();

        roomStatus.add(null);
        for (RoomStatus status : RoomStatus.values()) {
            roomStatus.add(status.toJson());
        }

        room_status.setItems(roomStatus);

        ObservableList<String> roomType = FXCollections.observableArrayList();

        roomType.add(null);
        for (RoomType type : RoomType.values()) {
            roomType.add(type.toJson());
        }

        room_type.setItems(roomType);

        create.setOnAction(event -> createRoom());
    }

    public void createRoom(){
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirm action");
        confirmationAlert.setHeaderText("Are you sure you want to create this room?");
        confirmationAlert.setContentText("Please confirm your action.");

        Optional<ButtonType> result = confirmationAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            Stage loadingStage = Utils.showLoadingScreen(primaryStage);
            Platform.runLater(() -> loadingStage.show());

            new Thread(() -> {
                try {

                    Room request = new Room();
                    try {
                        request.setRoomNumber(Integer.parseInt(room_number.getText()));
                    }
                    catch (Exception e){
                        Platform.runLater(() -> {
                            loadingStage.close();
                            Utils.showAlertDialog(Alert.AlertType.ERROR, "Invalid request", "Invalid room number");
                        });
                        return;
                    }
                    if(room_type.getSelectionModel().getSelectedItem() != null){
                        request.setRoomType(RoomType.valueOf(room_type.getSelectionModel().getSelectedItem()));
                    }
                    else{
                        Platform.runLater(() -> {
                            loadingStage.close();
                            Utils.showAlertDialog(Alert.AlertType.ERROR, "Invalid request", "Room type cannot be null");
                        });
                        return;
                    }
                    if(room_status.getSelectionModel().getSelectedItem() != null){
                        request.setRoomStatus(RoomStatus.valueOf(room_status.getSelectionModel().getSelectedItem()));
                    }
                    else{
                        Platform.runLater(() -> {
                            loadingStage.close();
                            Utils.showAlertDialog(Alert.AlertType.ERROR, "Invalid request", "Room status cannot be null");
                        });
                        return;
                    }

                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.registerModule(new JavaTimeModule());

                    String jsonString = objectMapper.writeValueAsString(request);

                    String response = RestClient.post("/room/", jsonString);
                    ApiResponseSingleData<Room> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponseSingleData<Room>>() {
                    });

                    if (apiResponse.getResponseHeader().getResponseCode().equals("00")) {
                        Platform.runLater(() -> {
                            loadingStage.close();
                            Utils.showAlertDialog(Alert.AlertType.INFORMATION, "Created Successfully", "Room created successfully");
                            primaryStage.close();

                        });
                    } else {
                        Platform.runLater(() -> {
                            loadingStage.close();
                            Utils.showAlertDialog(Alert.AlertType.ERROR, apiResponse.getResponseHeader().getResponseMessage(), apiResponse.getError());
                        });

                    }

                } catch (Exception e) {
                    Platform.runLater(() -> {
                        loadingStage.close();
                        e.printStackTrace();
                        Utils.showGeneralErrorDialog();
                    });
                }
            }).start();
        }
        else {
            confirmationAlert.close();
        }

    }
}
