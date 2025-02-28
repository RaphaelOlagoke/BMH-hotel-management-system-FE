package com.bmh.hotelmanagementsystem.settings;

import com.bmh.hotelmanagementsystem.BackendService.RestClient;
import com.bmh.hotelmanagementsystem.BackendService.entities.ApiResponseSingleData;
import com.bmh.hotelmanagementsystem.BackendService.entities.Room.GuestLog;
import com.bmh.hotelmanagementsystem.BackendService.entities.Room.GuestLogFilterRequest;
import com.bmh.hotelmanagementsystem.BackendService.entities.Room.RoomPrices;
import com.bmh.hotelmanagementsystem.BackendService.entities.Room.RoomPricesSummary;
import com.bmh.hotelmanagementsystem.BackendService.enums.GuestLogStatus;
import com.bmh.hotelmanagementsystem.BackendService.enums.PaymentStatus;
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

import static com.bmh.hotelmanagementsystem.Utils.showLoadingScreen;

public class SettingsController extends Controller {

    private Stage primaryStage;

    private String previousLocation;

    private Object data;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setData(Object data, String previousLocation){
        this.data = data;
        this.previousLocation = previousLocation;
    }

    @FXML
    private TextField standard_room_textfield;
    @FXML
    private TextField deluxe_room_textfield;
    @FXML
    private TextField executive_room_textfield;
    @FXML
    private TextField vip_room_textfield;

    @FXML
    private Button update;

    @FXML
    public void initialize()  {

        Stage loadingStage = showLoadingScreen(primaryStage);

        Platform.runLater(() -> loadingStage.show());


        try {
            RoomPrices roomPrice;

            String response = RestClient.get("/roomPrices/all");

            ObjectMapper objectMapper = new ObjectMapper();

            ApiResponseSingleData<RoomPricesSummary> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponseSingleData<RoomPricesSummary>>() {
            });

            if (apiResponse.getResponseHeader().getResponseCode().equals("00")){
                Platform.runLater(() -> {
                    loadingStage.close();
                    standard_room_textfield.setText(apiResponse.getData().getStandardPrice().toString());
                    deluxe_room_textfield.setText(apiResponse.getData().getDeluxePrice().toString());
                    executive_room_textfield.setText(apiResponse.getData().getExecutivePrice().toString());
                    vip_room_textfield.setText(apiResponse.getData().getVipPrice().toString());

                });
            }

        } catch (Exception e) {
            Platform.runLater(() -> {
                System.out.println(e);
                loadingStage.close();
                Utils.showGeneralErrorDialog();
            });
        }

        update.setOnAction(event -> updatePrices());

    }

    public void updatePrices(){
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirm Action");
        confirmationAlert.setHeaderText("Are you sure you want to update room prices?");
        confirmationAlert.setContentText("Please confirm your action.");

        Optional<ButtonType> result = confirmationAlert.showAndWait();


        if (result.isPresent() && result.get() == ButtonType.OK) {
            Stage loadingStage = showLoadingScreen(primaryStage);

            Platform.runLater(() -> loadingStage.show());

            try {
                RoomPricesSummary request = new RoomPricesSummary();
                try {
                    request.setStandardPrice(Double.valueOf(standard_room_textfield.getText()));
                    request.setDeluxePrice(Double.valueOf(deluxe_room_textfield.getText()));
                    request.setExecutivePrice(Double.valueOf(executive_room_textfield.getText()));
                    request.setVipPrice(Double.valueOf(vip_room_textfield.getText()));
                } catch (Exception e) {
                    Platform.runLater(() -> {
                        loadingStage.close();
                        Utils.showAlertDialog(Alert.AlertType.ERROR, "Invalid Request", "Invalid Price");
                    });
                    return;
                }

                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());

                String jsonString = objectMapper.writeValueAsString(request);

                String response = RestClient.post("/roomPrices/update", jsonString);

                ApiResponseSingleData<?> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponseSingleData<?>>() {
                });

                if (apiResponse.getResponseHeader().getResponseCode().equals("00")) {
                    Platform.runLater(() -> {
                        loadingStage.close();
                        initialize();
                    });
                }

            } catch (Exception e) {
                Platform.runLater(() -> {
                    System.out.println(e);
                    loadingStage.close();
                    Utils.showGeneralErrorDialog();
                });
            }
        }
        else {
            confirmationAlert.close();
        }
    }
}
