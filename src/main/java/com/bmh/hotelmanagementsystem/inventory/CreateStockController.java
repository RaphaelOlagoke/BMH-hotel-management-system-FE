package com.bmh.hotelmanagementsystem.inventory;

import com.bmh.hotelmanagementsystem.BackendService.RestClient;
import com.bmh.hotelmanagementsystem.BackendService.entities.ApiResponseSingleData;
import com.bmh.hotelmanagementsystem.BackendService.entities.inventory.CreateStockItemRequest;
import com.bmh.hotelmanagementsystem.BackendService.entities.inventory.StockItem;
import com.bmh.hotelmanagementsystem.BackendService.enums.ServiceType;
import com.bmh.hotelmanagementsystem.BackendService.enums.StockItemCategory;
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class CreateStockController extends Controller {

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
    private TextField name;
    @FXML
    private TextField quantity;
    @FXML
    private TextField unit;
    @FXML
    private ComboBox<String> category_comboBox;
    @FXML
    private DatePicker expiration_datePicker;
    @FXML
    private Button create;

    public void initialize() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        expiration_datePicker.setConverter(new javafx.util.StringConverter<LocalDate>() {
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

        ObservableList<String> categories = FXCollections.observableArrayList();

        for (StockItemCategory status : StockItemCategory.values()) {
            categories.add(status.toJson());
        }

        category_comboBox.setItems(categories);

        create.setOnAction(event -> createStockItem());
    }

    public void createStockItem(){
        Stage loadingStage = Utils.showLoadingScreen(primaryStage);
        Platform.runLater(() -> loadingStage.show());

        new Thread(() -> {
            try {
                if(category_comboBox.getSelectionModel().getSelectedItem() == null){
                    Platform.runLater(() -> {
                        loadingStage.close();
                        Utils.showAlertDialog(Alert.AlertType.ERROR,"Invalid request", "Category cannot be empty" );
                    });
                    return;
                }

                if (name.getText() == null || name.getText().equals("")){
                    Platform.runLater(() -> {
                        loadingStage.close();
                        Utils.showAlertDialog(Alert.AlertType.ERROR,"Invalid request", "Name cannot be empty" );
                    });
                    return;
                }

                CreateStockItemRequest request = new CreateStockItemRequest();
                request.setName(name.getText());
                request.setUnit(unit.getText() != null? unit.getText() : "");
                try{
                    request.setQuantity(Integer.valueOf(quantity.getText()));
                }
                catch (Exception e){
                    Platform.runLater(() -> {
                        loadingStage.close();
                        Utils.showAlertDialog(Alert.AlertType.ERROR,"Invalid request", "Invalid Quantity " );
                    });
                    return;
                }
                request.setExpiryDate(expiration_datePicker.getValue() != null ? expiration_datePicker.getValue() : null);
                request.setCategory(StockItemCategory.valueOf(category_comboBox.getSelectionModel().getSelectedItem()));

                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());

                String jsonString = objectMapper.writeValueAsString(request);

                String response = RestClient.post("/inventory/stockItem/", jsonString);
                ApiResponseSingleData<StockItem> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponseSingleData<StockItem>>() {});

                if (apiResponse.getResponseHeader().getResponseCode().equals("00")) {
                    Platform.runLater(() -> {
                        loadingStage.close();
                        Utils.showAlertDialog(Alert.AlertType.INFORMATION,"Created Successfully","Stock Item created successfully" );
                        primaryStage.close();

                    });
                } else {
                    Platform.runLater(() -> {
                        loadingStage.close();
                        Utils.showAlertDialog(Alert.AlertType.ERROR,apiResponse.getResponseHeader().getResponseMessage(),apiResponse.getError() );
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

}
