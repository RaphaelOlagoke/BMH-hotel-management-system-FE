package com.bmh.hotelmanagementsystem.RoomManagement;

import com.bmh.hotelmanagementsystem.BMHApplication;
import com.bmh.hotelmanagementsystem.BackendService.RestClient;
import com.bmh.hotelmanagementsystem.BackendService.entities.*;
import com.bmh.hotelmanagementsystem.BackendService.entities.Room.CheckIn;
import com.bmh.hotelmanagementsystem.BackendService.entities.Room.CreateGuestLogRequest;
import com.bmh.hotelmanagementsystem.BackendService.entities.discount.Discount;
import com.bmh.hotelmanagementsystem.BackendService.enums.LoginDepartment;
import com.bmh.hotelmanagementsystem.BackendService.enums.PaymentMethod;
import com.bmh.hotelmanagementsystem.Controller;
import com.bmh.hotelmanagementsystem.TokenStorage;
import com.bmh.hotelmanagementsystem.Utils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.bmh.hotelmanagementsystem.Utils.showLoadingScreen;

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
        room_charge.setText("Room Charge:  ₦" + formattedPrice);
        subTotal = checkInData.getTotalPrice();
        tax = 0.0;
        total = checkInData.getTotalPrice();
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
    private TextField discount_code;

    @FXML
    private Button checkIn;

    @FXML
    private ComboBox<String> payment_method;

    private Double discount = 0.0;

    private Double subTotal = 0.0;
    private Double tax = 0.0;
    private Double vat = 0.0;
    private Double total = 0.0;

    DecimalFormat formatter = new DecimalFormat("#,###.00");

    @FXML
    public void initialize() throws IOException {

        Stage loadingStage = showLoadingScreen(primaryStage);

        Platform.runLater(() -> loadingStage.show());

        new Thread(() -> {
            try {
                String response = RestClient.get("/additionalCharge/");

                ObjectMapper objectMapper = new ObjectMapper();

                ApiResponseSingleData<AdditionalChargesSummary> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponseSingleData<AdditionalChargesSummary>>() {
                });

                if (apiResponse.getResponseHeader().getResponseCode().equals("00")) {
                    Platform.runLater(() -> {
                        loadingStage.close();
                        tax = apiResponse.getData().getTaxPrice();
                        vat = apiResponse.getData().getVatPrice();
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
        discount_code.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                getDiscount();
            }
        });
//        System.out.println(checkInData.getGuestName());

    }

    public void getDiscount(){
        Stage loadingStage = Utils.showLoadingScreen(primaryStage);
        Platform.runLater(() -> loadingStage.show());

        new Thread(() -> {
            try {

                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());

                String response = RestClient.get("/discount/code?code=" + discount_code.getText());
                ApiResponseSingleData<Discount> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponseSingleData<Discount>>() {
                });

                if (apiResponse.getResponseHeader().getResponseCode().equals("00")) {
                    Platform.runLater(() -> {
                        loadingStage.close();
                        total = checkInData.getTotalPrice();
                        subTotal = checkInData.getTotalPrice();

                        discount = checkInData.getTotalPriceWithoutAdditionalCharges() * apiResponse.getData().getPercentage()/100.0;
                        total = (subTotal - discount);

                        DecimalFormat formatter = new DecimalFormat("#,###.00");

                        String formattedPrice = formatter.format(total);
                        room_charge.setText("Room Charge:  ₦" + formattedPrice);

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
                    request.setNoOfDays(checkInData.getNoOfDays());
                    request.setIdType(checkInData.getIdType());
                    request.setIdRef(checkInData.getIdRef());
                    request.setPhoneNumber(checkInData.getPhoneNumber());
                    request.setNextOfKinName(checkInData.getNextOfKinName());
                    request.setNextOfKinNumber(checkInData.getNextOfKinNumber());
                    if(discount_code.getText() != null || !discount_code.getText().equals("")){
                        request.setDiscountCode(discount_code.getText());
                    }

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
                                String[] credentials = TokenStorage.loadCredentials();
                                String department = "";
                                if (credentials != null) {
                                    department = credentials[2];
                                }
                                if(LoginDepartment.valueOf(department) == LoginDepartment.SUPER_ADMIN){
                                    utils.switchScreen("/com/bmh/hotelmanagementsystem/room/admin-guest-logs-view.fxml", primaryStage);
                                }
                                else if(LoginDepartment.valueOf(department) == LoginDepartment.ADMIN){
                                    utils.switchScreen("/com/bmh/hotelmanagementsystem/room/general-admin-guest-logs-view.fxml", primaryStage);
                                }
                                else if(LoginDepartment.valueOf(department) == LoginDepartment.RESTAURANT_BAR){
                                    utils.switchScreen("/com/bmh/hotelmanagementsystem/restaurant/restaurant-view.fxml", primaryStage);
                                }
                                else {
                                    utils.switchScreen("/com/bmh/hotelmanagementsystem/home-view.fxml", primaryStage);
                                }

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
