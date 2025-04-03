package com.bmh.hotelmanagementsystem.hall;

import com.bmh.hotelmanagementsystem.BackendService.RestClient;
import com.bmh.hotelmanagementsystem.BackendService.entities.*;
import com.bmh.hotelmanagementsystem.BackendService.entities.Room.*;
import com.bmh.hotelmanagementsystem.BackendService.entities.hall.BookHallRequest;
import com.bmh.hotelmanagementsystem.BackendService.entities.hall.HallLog;
import com.bmh.hotelmanagementsystem.BackendService.entities.hall.HallPrices;
import com.bmh.hotelmanagementsystem.BackendService.enums.HallType;
import com.bmh.hotelmanagementsystem.BackendService.enums.ID_TYPE;
import com.bmh.hotelmanagementsystem.BackendService.enums.PaymentMethod;
import com.bmh.hotelmanagementsystem.BackendService.enums.RoomType;
import com.bmh.hotelmanagementsystem.Controller;
import com.bmh.hotelmanagementsystem.RoomManagement.SelectedRoom;
import com.bmh.hotelmanagementsystem.Utils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.bmh.hotelmanagementsystem.Utils.showLoadingScreen;

public class BookHallController extends Controller {

    private Stage primaryStage;


    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    private Button book;

    @FXML
    private TextField guest_name;

    @FXML
    private ComboBox<String> hall_type;

    @FXML
    private ComboBox<String> id_type;

    @FXML
    private ComboBox<String> pay;
    @FXML
    private Label total_price;

    @FXML
    private Label hall_message;

    @FXML
    private TextField id_ref;

    @FXML
    private TextField phone_number;

    @FXML
    private TextField next_of_kin_name;

    @FXML
    private TextField next_of_kin_number;

    @FXML
    private DatePicker start_date;
    @FXML
    private TextField start_time;

    @FXML
    private TextArea description;

    @FXML
    private ComboBox<String> payment_method;


    private Double tax = 0.0;
    private Double vat = 0.0;

    @FXML
    public void initialize() throws IOException {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        start_date.setConverter(new javafx.util.StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                return date != null ? date.format(formatter) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                try {
                    return string != null && !string.isEmpty() ? LocalDate.parse(string, formatter) : null;
                } catch (DateTimeParseException e) {
                    // Invalid date input, show alert and return null

                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Invalid Date");
                    alert.setContentText("Invalid date entered. Please enter a valid date (yyyy-MM-dd).");
                    alert.showAndWait();
                    return null;
                }
            }
        });

        start_date.setDayCellFactory(dp -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (item.isBefore(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;"); // Highlight invalid dates
                }
            }
        });

        // Additional check: Ensure the entered time is a valid time
        start_time.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty() && !newValue.matches("([01]\\d|2[0-3]):[0-5]\\d")) {
                start_time.setStyle("-fx-border-color: red;");
            } else {
                start_time.setStyle(""); // Reset style for valid input
            }
        });


        book.setOnAction(event -> bookHall());

        ObservableList<String> payment_Methods = FXCollections.observableArrayList();

//        for (PaymentMethod paymentMethod : PaymentMethod.values()) {
//            payment_Methods.add(paymentMethod.toJson());
//        }
        payment_Methods.add(PaymentMethod.CARD.toJson());
        payment_Methods.add(PaymentMethod.TRANSFER.toJson());
        payment_Methods.add(PaymentMethod.CASH.toJson());

        payment_method.setItems(payment_Methods);

//        Stage loadingStage = showLoadingScreen(primaryStage);
//
//        Platform.runLater(() -> loadingStage.show());
//
//        new Thread(() -> {
//            try {
//                String response = RestClient.get("/additionalCharge/");
//
//                ObjectMapper objectMapper = new ObjectMapper();
//
//                ApiResponseSingleData<AdditionalChargesSummary> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponseSingleData<AdditionalChargesSummary>>() {
//                });
//
//                if (apiResponse.getResponseHeader().getResponseCode().equals("00")) {
//                    Platform.runLater(() -> {
//                        loadingStage.close();
//                        tax = apiResponse.getData().getTaxPrice();
//                        vat = apiResponse.getData().getVatPrice();
//                    });
//                }
//
//            } catch (Exception e) {
//                Platform.runLater(() -> {
//                    System.out.println(e);
//                    loadingStage.close();
//                    Utils.showGeneralErrorDialog();
//                });
//            }
//        }).start();



        ObservableList<String> hallTypes = FXCollections.observableArrayList();

        for (HallType hallType : HallType.values()) {
            hallTypes.add(hallType.toJson());
        }

        hall_type.setItems(hallTypes);

        ObservableList<String> idTypes = FXCollections.observableArrayList();

        for (ID_TYPE idType : ID_TYPE.values()) {
            idTypes.add(idType.toJson());
        }

        id_type.setItems(idTypes);

        hall_type.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

                Stage loadingStage = showLoadingScreen(primaryStage);

                Platform.runLater(() -> loadingStage.show());

                new Thread(() -> {
                    try {
                        String response = RestClient.get("/hallPrices/?hallType=" + newValue);

                        ObjectMapper objectMapper = new ObjectMapper();

                        // Convert JSON string to ApiResponse
                        ApiResponseSingleData<HallPrices> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponseSingleData<HallPrices>>() {
                        });

                        Platform.runLater(() -> {
                            loadingStage.close();
                            HallPrices hallPrices;
                            hallPrices = apiResponse.getData();
//                    checkInData.setRoomPrice(roomPrice.getRoomPrice());
                            DecimalFormat formatter = new DecimalFormat("#,###.00");

//                            String formattedPrice = formatter.format(roomPrice.getRoomPrice() + ((vat/100) * roomPrice.getRoomPrice()) + tax);
//                            total_price.setText("₦" + formattedPrice);

                            String formattedPrice = formatter.format(hallPrices.getHallPrice());
                            total_price.setText("₦" + formattedPrice);


                        });


                    } catch (Exception e) {
                        Platform.runLater(() -> {
                            loadingStage.close();
                            System.out.println(e);
                            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                            errorAlert.setTitle("Sever Error");
                            errorAlert.setContentText("Something went wrong");
                            errorAlert.showAndWait();
                        });
                    }
                }).start();
            }
        });

    }

    public void bookHall() {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirm action");
        confirmationAlert.setHeaderText("Are you sure you want to book this hall?");
        confirmationAlert.setContentText("Please confirm your action.");

        Optional<ButtonType> result = confirmationAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            Stage loadingStage = Utils.showLoadingScreen(primaryStage);
            Platform.runLater(() -> loadingStage.show());

            new Thread(() -> {
                try {

                    if (guest_name.getText() == null || guest_name.getText().equals("")) {
                        Platform.runLater(() -> {
                            loadingStage.close();
                            Utils.showAlertDialog(Alert.AlertType.INFORMATION, "Invalid Request", "Guest name cannot be null");
                        });
                        return;
                    }

                    if (phone_number.getText() == null || phone_number.getText().equals("")) {
                        Platform.runLater(() -> {
                            loadingStage.close();
                            Utils.showAlertDialog(Alert.AlertType.INFORMATION, "Invalid Request", "Guest Phone number cannot be null");
                        });
                        return;

                    }

                    if (hall_type.getSelectionModel().getSelectedItem() == null) {
                        Platform.runLater(() -> {
                            loadingStage.close();
                            Utils.showAlertDialog(Alert.AlertType.INFORMATION, "Invalid Request", "No Hall selected");
                        });
                        return;
                    }

                    if (payment_method.getSelectionModel().getSelectedItem() == null) {
                        Platform.runLater(() -> {
                            loadingStage.close();
                            Utils.showAlertDialog(Alert.AlertType.INFORMATION, "Invalid Request", "Payment Method cannot be null");
                        });
                        return;

                    }

                    if (start_date.getValue() == null) {
                        Platform.runLater(() -> {
                            loadingStage.close();
                            Utils.showAlertDialog(Alert.AlertType.INFORMATION, "Invalid Request", "Start date cannot be null");
                        });
                        return;

                    }

//                    TextFormatter<String> timeFormatter = new TextFormatter<>(change -> {
//                        String newText = change.getControlNewText();
//                        // Validate if the text matches HH:mm format using regex
//                        if (newText.matches("([01]\\d|2[0-3]):[0-5]\\d") || newText.isEmpty()) {
//                            return change; // Acceptable change
//                        } else {
//                            return null; // Reject the change
//                        }
//                    });
                    if (!start_time.getText().matches("([01]\\d|2[0-3]):[0-5]\\d")) {
                        Platform.runLater(() -> {
                            loadingStage.close();
                            Utils.showAlertDialog(Alert.AlertType.INFORMATION, "Invalid Request", "Invalid Start time");
                        });
                        return;
                    }


                    if (start_time.getText() == null) {
                        Platform.runLater(() -> {
                            loadingStage.close();
                            Utils.showAlertDialog(Alert.AlertType.INFORMATION, "Invalid Request", "Invalid Start time");
                        });
                        return;
                    }


                    BookHallRequest request = new BookHallRequest();
                    request.setGuestName(guest_name.getText());
                    request.setPaymentMethod(PaymentMethod.valueOf(payment_method.getSelectionModel().getSelectedItem()));
                    request.setIdType(ID_TYPE.valueOf(id_type.getSelectionModel().getSelectedItem()));
                    request.setIdRef(id_ref.getText());
                    request.setPhoneNumber(phone_number.getText());
                    request.setNextOfKinName(next_of_kin_name.getText());
                    request.setNextOfKinNumber(next_of_kin_number.getText());
                    request.setDescription(description.getText());

                    request.setHallType(HallType.valueOf(hall_type.getSelectionModel().getSelectedItem()));

                    LocalDate startDate = start_date.getValue();

                    // Get the time from TextField and validate the format
                    String timeText = start_time.getText();
                    DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("HH:mm");
                    LocalTime startTime = LocalTime.parse(timeText, formatterTime);

                    // Combine the date and time into a LocalDateTime
                    LocalDateTime startDateTime = LocalDateTime.of(startDate, startTime);
                    request.setStartDate(startDateTime);


                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.registerModule(new JavaTimeModule());

                    String jsonString = objectMapper.writeValueAsString(request);

                    String response = RestClient.post("/hallLog/", jsonString);
                    ApiResponseSingleData<HallLog> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponseSingleData<HallLog>>() {
                    });

                    if (apiResponse.getResponseHeader().getResponseCode().equals("00")) {
                        Platform.runLater(() -> {
                            loadingStage.close();
                            Utils.showAlertDialog(Alert.AlertType.INFORMATION, "Created Successfully", "Hall reservation created successfully");
                            Utils utils = new Utils();
                            try {
                                utils.switchScreen("/com/bmh/hotelmanagementsystem/hall/hall-logs-view.fxml", primaryStage);
                            } catch (IOException e) {
                                Utils.showGeneralErrorDialog();
                            }

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
        } else {
            confirmationAlert.close();
        }
    }

    }
