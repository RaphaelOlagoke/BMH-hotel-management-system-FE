package com.bmh.hotelmanagementsystem.HouseKeeping;

import com.bmh.hotelmanagementsystem.BackendService.RestClient;
import com.bmh.hotelmanagementsystem.BackendService.entities.*;
import com.bmh.hotelmanagementsystem.BackendService.entities.HouseKeeping.CleaningLog;
import com.bmh.hotelmanagementsystem.BackendService.entities.HouseKeeping.CreateCleaningLogRequest;
import com.bmh.hotelmanagementsystem.BackendService.entities.Room.Room;
import com.bmh.hotelmanagementsystem.Controller;
import com.bmh.hotelmanagementsystem.Utils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

import java.util.List;

public class CreateCleaningLogController extends Controller {

    private Stage primaryStage;
    private String previousLocation;
    private List<Room> data;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setData(Object data, String previousLocation){
        this.data = (List<Room>) data;
        this.previousLocation = previousLocation;

        ObservableList<Integer> roomNumber = FXCollections.observableArrayList();

        roomNumber.add(null);
        for (Room room : this.data) {
            roomNumber.add(room.getRoomNumber());
        }

        room_comboBox.setItems(roomNumber);
    }

    @FXML
    private ComboBox<Integer> room_comboBox;
    @FXML
    private ComboBox<String> staff_comboBox;
    @FXML
    private Button create;


    public void initialize() {
        create.setOnAction(event -> createCleaningLog());
    }


    public void createCleaningLog(){
        Stage loadingStage = Utils.showLoadingScreen(primaryStage);
        Platform.runLater(() -> loadingStage.show());

        new Thread(() -> {
            try {
                Integer selectedRoom = room_comboBox.getSelectionModel().getSelectedItem();

                CreateCleaningLogRequest request = new CreateCleaningLogRequest();
                try{
                    request.setRoomNumber(room_comboBox.getSelectionModel().getSelectedItem());
                }
                catch (NullPointerException e){
                    request.setRoomNumber(0);
                }

                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());

                String jsonString = objectMapper.writeValueAsString(request);

                String response = RestClient.post("/cleaningLog/", jsonString);
                ApiResponseSingleData<CleaningLog> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponseSingleData<CleaningLog>>() {});

                if (apiResponse.getResponseHeader().getResponseCode().equals("00")) {
                    Platform.runLater(() -> {
                        loadingStage.close();
                        Utils.showAlertDialog(Alert.AlertType.INFORMATION,apiResponse.getResponseHeader().getResponseMessage(),apiResponse.getError() );
                        primaryStage.close();

                    });
                } else {
                    Platform.runLater(() -> {
                        loadingStage.close();
                        Utils.showAlertDialog(Alert.AlertType.ERROR,apiResponse.getResponseHeader().getResponseMessage(),apiResponse.getError() );
                        primaryStage.close();
                    });

                }

            } catch (Exception e) {
                Platform.runLater(() -> {
                    loadingStage.close();
                    e.printStackTrace();
                    Utils.showGeneralErrorDialog();
                    primaryStage.close();
                });
            }
        }).start();

    }
}
