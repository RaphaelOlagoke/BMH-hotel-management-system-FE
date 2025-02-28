package com.bmh.hotelmanagementsystem.discount;

import com.bmh.hotelmanagementsystem.BackendService.RestClient;
import com.bmh.hotelmanagementsystem.BackendService.entities.ApiResponseSingleData;
import com.bmh.hotelmanagementsystem.BackendService.entities.discount.Discount;
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

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

public class CreateDiscountController  extends Controller {

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
    private TextField discount_percentage;

    @FXML
    private DatePicker start_datePicker;
    @FXML
    private DatePicker end_datePicker;

    @FXML
    private ComboBox<Boolean> isActive_comboBox;
    @FXML
    private ComboBox<Boolean> isOneTimeUse_comboBox;

    @FXML
    private Button create;


    DecimalFormat formatter = new DecimalFormat("#,###.00");

    public void initialize() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        start_datePicker.setConverter(new javafx.util.StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                return date != null ? date.format(formatter) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                try {
                    return string != null && !string.isEmpty() ? LocalDate.parse(string, formatter) : null;
                } catch (DateTimeParseException e) {

                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Invalid Date");
                    alert.setContentText("Invalid date entered. Please enter a valid date (yyyy-MM-dd).");
                    alert.showAndWait();
                    return null;
                }
            }
        });

        end_datePicker.setConverter(new javafx.util.StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                return date != null ? date.format(formatter) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                try {
                    return string != null && !string.isEmpty() ? LocalDate.parse(string, formatter) : null;
                } catch (DateTimeParseException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Invalid Date");
                    alert.setContentText("Invalid date entered. Please enter a valid date (yyyy-MM-dd).");
                    alert.showAndWait();
                    return null;
                }
            }
        });

        ObservableList<Boolean> booleanObservableList = FXCollections.observableArrayList();

        booleanObservableList.add(true);
        booleanObservableList.add(false);


        isActive_comboBox.setItems(booleanObservableList);
        isOneTimeUse_comboBox.setItems(booleanObservableList);

        create.setOnAction(event -> createDiscount());
    }


    public void createDiscount(){
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirm Payment");
        confirmationAlert.setHeaderText("Are you sure you want to create this discount code?");
        confirmationAlert.setContentText("Please confirm your action.");

        Optional<ButtonType> result = confirmationAlert.showAndWait();


        if (result.isPresent() && result.get() == ButtonType.OK) {

            Stage loadingStage = Utils.showLoadingScreen(primaryStage);
            Platform.runLater(() -> loadingStage.show());

            new Thread(() -> {
                try {
                    if (discount_percentage.getText().isEmpty()) {
                        Platform.runLater(() -> {
                            loadingStage.close();
                            Utils.showAlertDialog(Alert.AlertType.INFORMATION, "Invalid request", "Discount Percentage cannot be empty");
                        });
                        return;
                    }
                    if (start_datePicker.getValue() == null || end_datePicker.getValue() == null) {
                        Platform.runLater(() -> {
                            loadingStage.close();
                            Utils.showAlertDialog(Alert.AlertType.INFORMATION, "Invalid request", "start date and end date cannot be empty");
                        });
                        return;
                    }
                    if (isActive_comboBox.getSelectionModel().getSelectedItem() == null || isOneTimeUse_comboBox.getSelectionModel().getSelectedItem() == null) {
                        Platform.runLater(() -> {
                            loadingStage.close();
                            Utils.showAlertDialog(Alert.AlertType.INFORMATION, "Invalid request", "Fields can not be empty");
                        });
                        return;
                    }

                    int discountPercentage = 0;
                    try{
                        discountPercentage = Integer.parseInt(discount_percentage.getText());
                    }
                    catch (Exception e){
                        Platform.runLater(() -> {
                            loadingStage.close();
                            Utils.showAlertDialog(Alert.AlertType.INFORMATION, "Invalid request", "Discount Percentage must be an integer");
                        });
                        return;
                    }

                    Discount request = new Discount();
                    request.setPercentage(discountPercentage);
                    request.setStartDate(start_datePicker.getValue().atStartOfDay());
                    request.setEndDate(end_datePicker.getValue().atTime(23, 59, 0));
                    request.setActive(isActive_comboBox.getSelectionModel().getSelectedItem());
                    request.setOneTimeUse(isOneTimeUse_comboBox.getSelectionModel().getSelectedItem());

                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.registerModule(new JavaTimeModule());

                    String jsonString = objectMapper.writeValueAsString(request);

                    String response = RestClient.post("/discount/", jsonString);
                    ApiResponseSingleData<Discount> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponseSingleData<Discount>>() {
                    });

                    if (apiResponse.getResponseHeader().getResponseCode().equals("00")) {
                        Platform.runLater(() -> {
                            loadingStage.close();
                            Utils.showAlertDialog(Alert.AlertType.INFORMATION, apiResponse.getResponseHeader().getResponseMessage(), apiResponse.getError());
                            primaryStage.close();

                        });
                    } else {
                        Platform.runLater(() -> {
                            loadingStage.close();
                            Utils.showAlertDialog(Alert.AlertType.ERROR, apiResponse.getResponseHeader().getResponseMessage(), apiResponse.getError());
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
        else {
            confirmationAlert.close();
        }

    }
}
