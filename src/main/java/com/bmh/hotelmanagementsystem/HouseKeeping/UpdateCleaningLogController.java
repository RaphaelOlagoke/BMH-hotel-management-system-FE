package com.bmh.hotelmanagementsystem.HouseKeeping;

import com.bmh.hotelmanagementsystem.BackendService.RestClient;
import com.bmh.hotelmanagementsystem.BackendService.entities.*;
import com.bmh.hotelmanagementsystem.BackendService.entities.HouseKeeping.CleaningLog;
import com.bmh.hotelmanagementsystem.BackendService.entities.HouseKeeping.UpdateCleaningLogRequest;
import com.bmh.hotelmanagementsystem.BackendService.enums.CleaningStatus;
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
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class UpdateCleaningLogController extends Controller {

    private Stage primaryStage;
    private String previousLocation;
    private CleaningLog data;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setData(Object data, String previousLocation){
        this.data = (CleaningLog) data;
        this.previousLocation = previousLocation;

        ObservableList<String> cleaningStatus = FXCollections.observableArrayList();

        for (CleaningStatus status : CleaningStatus.values()) {
            cleaningStatus.add(status.toJson());
        }

        status_comboBox.setItems(cleaningStatus);
        status_comboBox.setValue(this.data.getStatus().toJson());

        room_number.setText("Room:  " + this.data.getRoom().getRoomNumber());
    }

    @FXML
    private Label room_number;
    @FXML
    private ComboBox<String> status_comboBox;
    @FXML
    private ComboBox<String> staff_comboBox;
    @FXML
    private Button update;


    public void initialize() {
        update.setOnAction(event -> updateCleaningLog());
    }


    public void updateCleaningLog(){
        Stage loadingStage = Utils.showLoadingScreen(primaryStage);
        Platform.runLater(() -> loadingStage.show());

        new Thread(() -> {
            try {

                UpdateCleaningLogRequest request = new UpdateCleaningLogRequest();
                request.setRef(data.getRef());
                request.setStatus(status_comboBox.getSelectionModel().getSelectedItem() != null? CleaningStatus.valueOf(status_comboBox.getSelectionModel().getSelectedItem()) : null);

                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());

                String jsonString = objectMapper.writeValueAsString(request);

                String response = RestClient.post("/cleaningLog/update", jsonString);
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
