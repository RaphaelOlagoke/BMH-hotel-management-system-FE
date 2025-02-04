package com.bmh.hotelmanagementsystem.RoomManagement;

import com.bmh.hotelmanagementsystem.BackendService.RestClient;
import com.bmh.hotelmanagementsystem.BackendService.entities.*;
import com.bmh.hotelmanagementsystem.BackendService.entities.Room.CheckIn;
import com.bmh.hotelmanagementsystem.BackendService.entities.Room.CreateGuestLogRequest;
import com.bmh.hotelmanagementsystem.BackendService.enums.PaymentMethod;
import com.bmh.hotelmanagementsystem.Controller;
import com.bmh.hotelmanagementsystem.Utils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CheckInInvoiceController extends Controller {

    private Stage primaryStage;
    private CheckIn checkInData;

    private List<Integer> roomNumbers = new ArrayList<>();


    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setCheckInData(CheckIn checkInData) {
        this.checkInData = checkInData;
        guest_name.setText("Guest Name:  " + checkInData.getGuestName());

        StringBuilder rooms = new StringBuilder();
        StringBuilder types = new StringBuilder();

        int size = checkInData.getSelectedRooms().size();
        int index = 0;

        for (SelectedRoom selectedRoom : checkInData.getSelectedRooms()) {
            rooms.append(selectedRoom.getRoomNumber());
            types.append(selectedRoom.getRoomType());
            roomNumbers.add(selectedRoom.getRoomNumber());
            if (index < size - 1) {
                rooms.append(", ");
                types.append(", ");
            }
            index++;
        }

        room_number.setText("Room Number(s):  " + rooms);
        room_type.setText("Room Type(s):  " + types);
        DecimalFormat formatter = new DecimalFormat("#,###.00");

        String formattedPrice = formatter.format(checkInData.getTotalPrice());
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
    private ComboBox<String> payment_method;

    @FXML
    public void initialize() throws IOException {
        ObservableList<String> payment_Methods = FXCollections.observableArrayList();

//        for (PaymentMethod paymentMethod : PaymentMethod.values()) {
//            payment_Methods.add(paymentMethod.toJson());
//        }
        payment_Methods.add(PaymentMethod.CARD.toJson());
        payment_Methods.add(PaymentMethod.TRANSFER.toJson());
        payment_Methods.add(PaymentMethod.CASH.toJson());

        payment_method.setItems(payment_Methods);

        back.setOnAction(event -> goBack());
        checkIn.setOnAction(event -> checkIn());
//        System.out.println(checkInData.getGuestName());

    }

    public void goBack(){
        try {
            Utils utils = new Utils();
            utils.switchScreen("/com/bmh/hotelmanagementsystem/room/checkIn-view.fxml", primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void checkIn() {
        checkInData.setPaymentMethod(payment_method.getSelectionModel().getSelectedItem() != null ? PaymentMethod.valueOf(payment_method.getSelectionModel().getSelectedItem()) : null);
        if( checkInData.getPaymentMethod() == null){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid request");
//                alert.setHeaderText("This is a header");
            alert.setContentText("Payment method can not be empty");
            alert.showAndWait();
            return;
        }

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirm Check-In");
        confirmationAlert.setHeaderText("Are you sure you want to proceed with the check-in?");
        confirmationAlert.setContentText("Please confirm your action.");

        Optional<ButtonType> result = confirmationAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
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
                    request.setRoomNumbers(roomNumbers);
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
        else {
            confirmationAlert.close();
        }
    }

}
