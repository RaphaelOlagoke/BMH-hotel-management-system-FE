package com.bmh.hotelmanagementsystem.inventory;

import com.bmh.hotelmanagementsystem.BackendService.RestClient;
import com.bmh.hotelmanagementsystem.BackendService.entities.ApiResponseSingleData;
import com.bmh.hotelmanagementsystem.BackendService.entities.Restaurant.MenuItemDto;
import com.bmh.hotelmanagementsystem.BackendService.entities.Restaurant.UpdateMenuItemRequest;
import com.bmh.hotelmanagementsystem.BackendService.entities.inventory.StockItem;
import com.bmh.hotelmanagementsystem.BackendService.entities.inventory.UpdateStockItemRequest;
import com.bmh.hotelmanagementsystem.BackendService.enums.LoginDepartment;
import com.bmh.hotelmanagementsystem.BackendService.enums.MenuItemType;
import com.bmh.hotelmanagementsystem.BackendService.enums.StockActionReason;
import com.bmh.hotelmanagementsystem.BackendService.enums.StockItemCategory;
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
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class UpdateStockController extends Controller {

    private Stage primaryStage;
    private String previousLocation;

    private StockItem data;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setData(Object data, String previousLocation){
        this.data = (StockItem) data;
        stock_item_ref.setText("Ref: " + this.data.getRef());
        name.setText(this.data.getName());
        quantity.setText(String.valueOf(this.data.getQuantity()));
        unit.setText(this.data.getUnit());
        category_comboBox.setValue(this.data.getCategory().toJson());
        this.previousLocation = previousLocation;
    }

    @FXML
    private Label stock_item_ref;
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
    private ComboBox<String> reason_comboBox;
    @FXML
    private Button update;

    public void initialize() {
        String[] credentials = TokenStorage.loadCredentials();
        String username = "";
        String department = "";
        if (credentials != null) {
            username = credentials[0];
            department = credentials[2];
        }

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

        ObservableList<String> reasons = FXCollections.observableArrayList();

        for (StockActionReason status : StockActionReason.values()) {
            reasons.add(status.toJson());
        }

        reason_comboBox.setItems(reasons);

        if(LoginDepartment.valueOf(department) == LoginDepartment.SUPER_ADMIN || LoginDepartment.valueOf(department) == LoginDepartment.ACCOUNTS) {
            update.setOnAction(event -> updateStockItem());
        }
        else{
            update.setDisable(true);
            update.setVisible(false);
        }

//        update.setOnAction(event -> updateStockItem());
    }

    public void updateStockItem(){
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

                if(reason_comboBox.getSelectionModel().getSelectedItem() == null){
                    Platform.runLater(() -> {
                        loadingStage.close();
                        Utils.showAlertDialog(Alert.AlertType.ERROR,"Invalid request", "Reason cannot be empty" );
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

                UpdateStockItemRequest request = new UpdateStockItemRequest();
                request.setRef(data.getRef());
                request.setName(name.getText());
                request.setUnit(unit.getText());
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
                request.setCategory(StockItemCategory.valueOf(category_comboBox.getSelectionModel().getSelectedItem()));
                request.setReason(StockActionReason.valueOf(reason_comboBox.getSelectionModel().getSelectedItem()));
                request.setExpiryDate(expiration_datePicker.getValue() != null ? expiration_datePicker.getValue() : null);

                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());

                String jsonString = objectMapper.writeValueAsString(request);

                String response = RestClient.post("/inventory/stockItem/update", jsonString);
                ApiResponseSingleData<StockItem> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponseSingleData<StockItem>>() {});

                if (apiResponse.getResponseHeader().getResponseCode().equals("00")) {
                    Platform.runLater(() -> {
                        loadingStage.close();
                        Utils.showAlertDialog(Alert.AlertType.INFORMATION,"Updated Successfully","Stock Item updated successfully" );
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
