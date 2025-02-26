package com.bmh.hotelmanagementsystem.components;

import com.bmh.hotelmanagementsystem.BackendService.RestClient;
import com.bmh.hotelmanagementsystem.BackendService.entities.ApiResponse;
import com.bmh.hotelmanagementsystem.BackendService.entities.ApiResponseSingleData;
import com.bmh.hotelmanagementsystem.BackendService.entities.inventory.CreateStockRequest;
import com.bmh.hotelmanagementsystem.BackendService.entities.inventory.StockItem;
import com.bmh.hotelmanagementsystem.BackendService.entities.inventory.StockRequest;
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
import javafx.util.StringConverter;

import static com.bmh.hotelmanagementsystem.Utils.showLoadingScreen;

public class CreateInventoryRequestController extends Controller {

    private Stage primaryStage;
    private String previousLocation;

    private StockItemCategory data;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setData(Object data, String previousLocation){
        this.data = (StockItemCategory) data;
        this.previousLocation = previousLocation;

        Stage loadingStage = showLoadingScreen(primaryStage);

        stockItem_comboBox.setCellFactory(param -> new ListCell<StockItem>() {
            @Override
            protected void updateItem(StockItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName());
                }
            }
        });

        stockItem_comboBox.setConverter(new StringConverter<StockItem>() {
            @Override
            public String toString(StockItem item) {
                return item != null ? item.getName() : "";
            }

            @Override
            public StockItem fromString(String string) {
                return null;
            }
        });

        Platform.runLater(() -> loadingStage.show());

        new Thread(() -> {
            try {
                String response = RestClient.get("/inventory/stockItem/category?category=" + this.data);

                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());

                ApiResponse<StockItem> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponse<StockItem>>() {});

                if (apiResponse.getResponseHeader().getResponseCode().equals("00")) {
                    Platform.runLater(() -> {
                        loadingStage.close();

                        ObservableList<StockItem> stockItems = FXCollections.observableArrayList();

                        stockItems.add(null);
                        stockItems.addAll(apiResponse.getData());

                        stockItem_comboBox.setItems(stockItems);

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


        create.setOnAction(event -> createRequest());
    }

    @FXML
    private TextField quantity;
    @FXML
    private ComboBox<StockItem> stockItem_comboBox;
    @FXML
    private Button create;

    public void createRequest(){
        Stage loadingStage = Utils.showLoadingScreen(primaryStage);
        Platform.runLater(() -> loadingStage.show());

        new Thread(() -> {
            try {
                if(stockItem_comboBox.getSelectionModel().getSelectedItem() == null){
                    Platform.runLater(() -> {
                        loadingStage.close();
                        Utils.showAlertDialog(Alert.AlertType.ERROR,"Invalid request", "Item cannot be empty" );
                    });
                    return;
                }

                CreateStockRequest request = new CreateStockRequest();
                request.setItem(stockItem_comboBox.getSelectionModel().getSelectedItem().getRef());
                request.setDepartment(this.data);
                try{
                    request.setQuantityRequested(Integer.valueOf(quantity.getText()));
                }
                catch (Exception e){
                    Platform.runLater(() -> {
                        loadingStage.close();
                        Utils.showAlertDialog(Alert.AlertType.ERROR,"Invalid request", "Invalid Quantity " );
                    });
                    return;
                }

                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());

                String jsonString = objectMapper.writeValueAsString(request);

                String response = RestClient.post("/inventory/stockRequest", jsonString);
                ApiResponseSingleData<StockRequest> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponseSingleData<StockRequest>>() {});

                if (apiResponse.getResponseHeader().getResponseCode().equals("00")) {
                    Platform.runLater(() -> {
                        loadingStage.close();
                        Utils.showAlertDialog(Alert.AlertType.INFORMATION,"Created Successfully","Stock Request created successfully" );
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
