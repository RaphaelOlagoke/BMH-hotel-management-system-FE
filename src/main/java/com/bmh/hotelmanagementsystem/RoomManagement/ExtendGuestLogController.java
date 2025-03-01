package com.bmh.hotelmanagementsystem.RoomManagement;

import com.bmh.hotelmanagementsystem.BackendService.RestClient;
import com.bmh.hotelmanagementsystem.BackendService.entities.ApiResponseSingleData;
import com.bmh.hotelmanagementsystem.BackendService.entities.Room.GuestLog;
import com.bmh.hotelmanagementsystem.BackendService.entities.Room.Room;
import com.bmh.hotelmanagementsystem.BackendService.enums.RoomStatus;
import com.bmh.hotelmanagementsystem.BackendService.enums.RoomType;
import com.bmh.hotelmanagementsystem.Controller;
import com.bmh.hotelmanagementsystem.Utils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Optional;

public class ExtendGuestLogController extends Controller {

    private Stage primaryStage;

    private String previousLocation;

    private GuestLog data;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }


    public void setData(Object data, String previousLocation){
        this.data = (GuestLog) data;
        this.previousLocation = previousLocation;
    }

    @FXML
    private TextField no_of_days;

    @FXML
    private Button submit;

    public void initialize() throws IOException {
        submit.setOnAction(event -> submit());
    }

    public void submit(){
        int noOfDays = 0;
        try{
            noOfDays = Integer.parseInt(no_of_days.getText());
            if (noOfDays > 15 || noOfDays < 1){
                Utils.showAlertDialog(Alert.AlertType.ERROR, "Invalid request", "No of days must be in the range of 1-15");
                return;
            }
        }
        catch (Exception e){
            Utils.showAlertDialog(Alert.AlertType.INFORMATION, "Invalid request", "No of days must be in the range of 1-15");
            return;
        }

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirm action");
        confirmationAlert.setHeaderText("Are you sure you want to extend stay by " + no_of_days.getText() +" day(s) ?");
        confirmationAlert.setContentText("Please confirm your action.");

        Optional<ButtonType> result = confirmationAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            Stage loadingStage = Utils.showLoadingScreen(primaryStage);
            Platform.runLater(() -> loadingStage.show());

            new Thread(() -> {

                try{
                    int days = Integer.parseInt(no_of_days.getText());

                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.registerModule(new JavaTimeModule());

                    String response = RestClient.post("/guestLog/extend?roomNumber=" +data.getGuestLogRooms().get(0).getRoom().getRoomNumber()+ "&noOfDays=" + days,"");
                    ApiResponseSingleData<?> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponseSingleData<?>>() {
                    });

                    if (apiResponse.getResponseHeader().getResponseCode().equals("00")) {
                        Platform.runLater(() -> {
                            loadingStage.close();
                            Utils.showAlertDialog(Alert.AlertType.INFORMATION, "Updated Successfully", "Guest log updated successfully");
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
