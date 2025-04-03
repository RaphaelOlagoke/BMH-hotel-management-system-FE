package com.bmh.hotelmanagementsystem.settings;

import com.bmh.hotelmanagementsystem.BackendService.RestClient;
import com.bmh.hotelmanagementsystem.BackendService.entities.AdditionalChargesSummary;
import com.bmh.hotelmanagementsystem.BackendService.entities.ApiResponseSingleData;
import com.bmh.hotelmanagementsystem.BackendService.entities.Room.GuestLog;
import com.bmh.hotelmanagementsystem.BackendService.entities.Room.GuestLogFilterRequest;
import com.bmh.hotelmanagementsystem.BackendService.entities.Room.RoomPrices;
import com.bmh.hotelmanagementsystem.BackendService.entities.Room.RoomPricesSummary;
import com.bmh.hotelmanagementsystem.BackendService.entities.hall.HallPrices;
import com.bmh.hotelmanagementsystem.BackendService.entities.hall.HallPricesSummary;
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

//    @FXML
//    private TextField standard_room_textfield;
//    @FXML
//    private TextField deluxe_room_textfield;
//    @FXML
//    private TextField executive_room_textfield;
//    @FXML
//    private TextField vip_room_textfield;


    @FXML
    private TextField executive_suite_textfield;
    @FXML
    private TextField business_suite_a_textfield;
    @FXML
    private TextField business_suite_b_textfield;
    @FXML
    private TextField executive_deluxe_textfield;
    @FXML
    private TextField deluxe_room_textfield;
    @FXML
    private TextField classic_room_textfield;



    @FXML
    private TextField coference_room_textfield;
    @FXML
    private TextField meeting_hall_textfield;
    @FXML
    private TextField meeting_room_textfield;


    @FXML
    private TextField vat_price_textfield;
    @FXML
    private TextField tax_price_textfield;


    @FXML
    private Button update;


    @FXML
    private Button updateHallPrices;

    @FXML
    private Button updateAdditionalPrices;

    @FXML
    public void initialize()  {

        update.setOnAction(event -> updatePrices());
        updateAdditionalPrices.setOnAction(event -> updateAdditionalPrices());
        updateHallPrices.setOnAction(event -> updateHallPrices());

        Stage loadingStage = showLoadingScreen(primaryStage);

        Platform.runLater(() -> loadingStage.show());


        new Thread(() -> {

            try {
                RoomPrices roomPrice;

                String response = RestClient.get("/roomPrices/all");

                ObjectMapper objectMapper = new ObjectMapper();

                ApiResponseSingleData<RoomPricesSummary> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponseSingleData<RoomPricesSummary>>() {
                });

                if (apiResponse.getResponseHeader().getResponseCode().equals("00")) {
                    Platform.runLater(() -> {
//                        loadingStage.close();
                        classic_room_textfield.setText(apiResponse.getData().getClassicPrice().toString());
                        deluxe_room_textfield.setText(apiResponse.getData().getDeluxePrice().toString());
                        executive_suite_textfield.setText(apiResponse.getData().getExecutiveSuitePrice().toString());
                        business_suite_a_textfield.setText(apiResponse.getData().getBusinessSuiteAPrice().toString());
                        business_suite_b_textfield.setText(apiResponse.getData().getBusinessSuiteBPrice().toString());
                        executive_deluxe_textfield.setText(apiResponse.getData().getExecutiveDeluxePrice().toString());


                    });
                }

            } catch (Exception e) {
                Platform.runLater(() -> {
                    System.out.println(e);
//                    loadingStage.close();
                    Utils.showGeneralErrorDialog();
                });
            }



            try {
                HallPrices hallPrices;

                String response = RestClient.get("/hallPrices/all");

                ObjectMapper objectMapper = new ObjectMapper();

                ApiResponseSingleData<HallPricesSummary> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponseSingleData<HallPricesSummary>>() {
                });

                if (apiResponse.getResponseHeader().getResponseCode().equals("00")) {
                    Platform.runLater(() -> {
//                        loadingStage.close();
                        coference_room_textfield.setText(apiResponse.getData().getConferenceHallPrice().toString());
                        meeting_hall_textfield.setText(apiResponse.getData().getMeetingHallPrice().toString());
                        meeting_room_textfield.setText(apiResponse.getData().getMeetingRoomPrice().toString());

                    });
                }

            } catch (Exception e) {
                Platform.runLater(() -> {
                    System.out.println(e);
//                    loadingStage.close();
                    Utils.showGeneralErrorDialog();
                });
            }

            try {
                String response = RestClient.get("/additionalCharge/");

                ObjectMapper objectMapper = new ObjectMapper();

                ApiResponseSingleData<AdditionalChargesSummary> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponseSingleData<AdditionalChargesSummary>>() {
                });

                if (apiResponse.getResponseHeader().getResponseCode().equals("00")) {
                    Platform.runLater(() -> {
                        loadingStage.close();
                        vat_price_textfield.setText(apiResponse.getData().getVatPrice().toString());
                        tax_price_textfield.setText(apiResponse.getData().getTaxPrice().toString());
                    });
                }
                else{
                    Platform.runLater(() -> {
                        loadingStage.close();
                        Utils.showAlertDialog(Alert.AlertType.INFORMATION, apiResponse.getResponseHeader().getResponseMessage(), apiResponse.getError());
                    });
                }

            } catch (Exception e) {
                Platform.runLater(() -> {
                    System.out.println(e);
                    loadingStage.close();
                    Utils.showGeneralErrorDialog();
                });
            }
        }).start();


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

            new Thread(() -> {

                try {
                    RoomPricesSummary request = new RoomPricesSummary();
                    try {
                        request.setClassicPrice(Double.valueOf(classic_room_textfield.getText()));
                        request.setDeluxePrice(Double.valueOf(deluxe_room_textfield.getText()));
                        request.setExecutiveSuitePrice(Double.valueOf(executive_suite_textfield.getText()));
                        request.setBusinessSuiteAPrice(Double.valueOf(business_suite_a_textfield.getText()));
                        request.setBusinessSuiteBPrice(Double.valueOf(business_suite_b_textfield.getText()));
                        request.setExecutiveDeluxePrice(Double.valueOf(executive_deluxe_textfield.getText()));

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
            }).start();
        }
        else {
            confirmationAlert.close();
        }
    }


    public void updateHallPrices(){
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirm Action");
        confirmationAlert.setHeaderText("Are you sure you want to update hall prices?");
        confirmationAlert.setContentText("Please confirm your action.");

        Optional<ButtonType> result = confirmationAlert.showAndWait();


        if (result.isPresent() && result.get() == ButtonType.OK) {
            Stage loadingStage = showLoadingScreen(primaryStage);

            Platform.runLater(() -> loadingStage.show());

            new Thread(() -> {

                try {
                    HallPricesSummary request = new HallPricesSummary();
                    try {
                        request.setConferenceHallPrice(Double.valueOf(coference_room_textfield.getText()));
                        request.setMeetingHallPrice(Double.valueOf(meeting_hall_textfield.getText()));
                        request.setMeetingRoomPrice(Double.valueOf(meeting_room_textfield.getText()));


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

                    String response = RestClient.post("/hallPrices/update", jsonString);

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
            }).start();
        }
        else {
            confirmationAlert.close();
        }
    }

    public void updateAdditionalPrices(){
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirm Action");
        confirmationAlert.setHeaderText("Are you sure you want to update additional charge prices?");
        confirmationAlert.setContentText("Please confirm your action.");

        Optional<ButtonType> result = confirmationAlert.showAndWait();


        if (result.isPresent() && result.get() == ButtonType.OK) {
            Stage loadingStage = showLoadingScreen(primaryStage);

            Platform.runLater(() -> loadingStage.show());

            new Thread(() -> {

                try {
                    AdditionalChargesSummary request = new AdditionalChargesSummary();
                    try {
                        request.setVatPrice(Double.valueOf(vat_price_textfield.getText()));
                        request.setTaxPrice(Double.valueOf(tax_price_textfield.getText()));

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

                    String response = RestClient.post("/additionalCharge/update", jsonString);

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
            }).start();
        }
        else {
            confirmationAlert.close();
        }
    }
}
