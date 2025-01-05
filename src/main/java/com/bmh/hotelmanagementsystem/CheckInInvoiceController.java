package com.bmh.hotelmanagementsystem;

import com.bmh.hotelmanagementsystem.BackendService.RestClient;
import com.bmh.hotelmanagementsystem.BackendService.entities.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.http.HttpResponse;
import java.text.DecimalFormat;
import java.util.List;

public class CheckInInvoiceController extends Controller {

    private Stage primaryStage;
    private CheckIn checkInData;


    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setCheckInData(CheckIn checkInData) {
        this.checkInData = checkInData;
        guest_name.setText("Guest Name:  " + checkInData.getGuestName());
        room_number.setText("Room Number:  " + checkInData.getRoomNumber());
        room_type.setText("Room Type:  " + checkInData.getRoomType());
        DecimalFormat formatter = new DecimalFormat("#,###.00");

        String formattedPrice = formatter.format(checkInData.getRoomPrice());
        room_charge.setText("Room Charge:  " + formattedPrice);
    }

    public CheckIn getCheckInData() {
        return checkInData;
    }

    @FXML
    private Button back;

    @FXML
    private Label guest_name;

    @FXML
    private Label room_number;

    @FXML
    private Label room_type;

    @FXML
    private Label room_charge;

    @FXML
    private Button checkIn;

    @FXML
    public void initialize() throws IOException {
        back.setOnAction(event -> goBack());
        checkIn.setOnAction(event -> checkIn());
//        System.out.println(checkInData.getGuestName());

    }

    public void goBack(){
        try {
            Utils utils = new Utils();
            utils.switchScreen("/com/bmh/hotelmanagementsystem/checkIn-view.fxml", primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void checkIn() {
        Stage loadingStage = new Stage();
        ProgressIndicator progressIndicator = new ProgressIndicator();
        StackPane loadingRoot = new StackPane();
        loadingRoot.getChildren().add(progressIndicator);
        Scene loadingScene = new Scene(loadingRoot, 200, 200);
        loadingStage.setScene(loadingScene);
        loadingStage.setTitle("Processing...");
        loadingStage.initOwner(primaryStage);
        loadingStage.initModality(Modality.APPLICATION_MODAL);
        loadingStage.show();

        new Thread(() -> {

            try {
                CreateGuestLogRequest request = new CreateGuestLogRequest();
                request.setGuestName(checkInData.getGuestName());
                request.setRoomNumber(checkInData.getRoomNumber());
                request.setPaymentMethod(checkInData.getPaymentMethod());

                ObjectMapper objectMapper = new ObjectMapper();

                String jsonString = objectMapper.writeValueAsString(request);

                String response = RestClient.post("/guestLog/", jsonString);
                ApiResponseSingleData<CreateGuestLogRequest> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponseSingleData<CreateGuestLogRequest>>() {
                });

                Platform.runLater(() -> {
                    try {
                        if (apiResponse.getResponseHeader().getResponseCode().equals("00")) {
                            // Close the loading screen
                            loadingStage.close();

                            // Show success message and navigate to home
                            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                            successAlert.setTitle("Check-In Successful");
                            successAlert.setContentText("The check-in was successful.");
                            successAlert.showAndWait();

                            // Switch screen to home view
                            Utils utils = new Utils();
                            utils.switchScreen("/com/bmh/hotelmanagementsystem/home-view.fxml", primaryStage);
                        } else {
                            // Close the loading screen
                            loadingStage.close();

                            // Show error message from API response
                            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                            errorAlert.setTitle(apiResponse.getResponseHeader().getResponseMessage());
                            errorAlert.setContentText(apiResponse.getError());
                            errorAlert.showAndWait();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        loadingStage.close();  // Close the loading screen in case of error

                        // Show general error message
                        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                        errorAlert.setTitle("Error");
                        errorAlert.setContentText("Something went wrong. Please try again.");
                        errorAlert.showAndWait();
                    }
                });


            } catch (Exception e) {
                Platform.runLater(() -> {
                    loadingStage.close();
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Server Error");
                    alert.setContentText("Something went wrong. Please try again.");
                    alert.showAndWait();
                });
            }
        }).start();
    }

}
