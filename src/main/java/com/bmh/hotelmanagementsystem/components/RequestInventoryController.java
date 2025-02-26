package com.bmh.hotelmanagementsystem.components;

import com.bmh.hotelmanagementsystem.BackendService.RestClient;
import com.bmh.hotelmanagementsystem.BackendService.entities.ApiResponse;
import com.bmh.hotelmanagementsystem.BackendService.entities.inventory.StockRequest;
import com.bmh.hotelmanagementsystem.BackendService.entities.inventory.StockRequestFilterRequest;
import com.bmh.hotelmanagementsystem.BackendService.enums.StockItemCategory;
import com.bmh.hotelmanagementsystem.BackendService.enums.StockRequestStatus;
import com.bmh.hotelmanagementsystem.Controller;
import com.bmh.hotelmanagementsystem.Utils;
import com.bmh.hotelmanagementsystem.dto.inventory.StockRequestRow;
import com.bmh.hotelmanagementsystem.inventory.CreateStockController;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

import static com.bmh.hotelmanagementsystem.Utils.showLoadingScreen;

public class RequestInventoryController extends Controller {

    private Stage primaryStage;
    private String previousLocation;

    private StockItemCategory data;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setData(Object data, String previousLocation){
        this.data = (StockItemCategory) data;
        this.previousLocation = previousLocation;
        apply(page, pageSize);
    }

    @FXML
    private TextField searchField;
    @FXML
    private Button search;
    @FXML
    private ComboBox<String> status_comboBox;

    @FXML
    private DatePicker start_datePicker;
    @FXML
    private DatePicker end_datePicker;

    @FXML
    private TableView<StockRequestRow> stockRequestTable;
    @FXML
    private TableColumn<StockRequestRow, String> item_name_column;
    @FXML
    private TableColumn<StockRequestRow, Integer> quantity_column;
    @FXML
    private TableColumn<StockRequestRow, StockRequestStatus> status_column;
    @FXML
    private TableColumn<StockRequestRow, String> department_column;
    @FXML
    private TableColumn<StockRequestRow, String> created_date_column;
    @FXML
    private TableColumn<StockRequestRow, String> retrieved_date_column;

    @FXML
    private Pagination pagination;
    @FXML
    private Button apply;

    @FXML
    private Button back;
    @FXML
    private Button create;

    private ObservableList<StockRequestRow> stockRequestData = FXCollections.observableArrayList();

    private int pageSize = 10;
    private int page = 0;

    String currentQuery = "";

    @FXML
    public void initialize() throws IOException {
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

        ObservableList<String> status = FXCollections.observableArrayList();

        status.add(null);
        for (StockRequestStatus item : StockRequestStatus.values()) {
            status.add(item.toJson());
        }

        status_comboBox.setItems(status);


        item_name_column.setCellValueFactory(cellData -> cellData.getValue().item_name_columnProperty());
        quantity_column.setCellValueFactory(cellData -> cellData.getValue().quantityRequested_columnProperty().asObject());
        status_column.setCellValueFactory(cellData -> cellData.getValue().status_columnProperty());
        department_column.setCellValueFactory(cellData -> cellData.getValue().department_columnProperty());
        created_date_column.setCellValueFactory(cellData -> cellData.getValue().createdAt_columnProperty());
        retrieved_date_column.setCellValueFactory(cellData -> cellData.getValue().retrievedAt_columnProperty());

        status_column.setCellFactory(column -> {
            return new TableCell<StockRequestRow, StockRequestStatus>() {
                @Override
                protected void updateItem(StockRequestStatus item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);  // Reset cell content
                    } else {
                        // Create a Label for the status
                        Label label = new Label(item.toJson());
                        label.setStyle("-fx-font-weight: bold; -fx-padding: 5px;");

                        // Apply styles based on the status
                        if (item == StockRequestStatus.APPROVED) {
                            label.setStyle("-fx-text-fill: green; -fx-background-color: #e0f7e0; -fx-font-weight: bold; -fx-padding: 5px 10px; -fx-background-radius: 5;");
                        } else if (item == StockRequestStatus.DECLINED) {
                            label.setStyle("-fx-text-fill: red; -fx-background-color: #f7e0e0; -fx-font-weight: bold; -fx-padding: 5px 15px; -fx-background-radius: 5;");
                        }
                        else if (item == StockRequestStatus.PENDING) {
                            label.setStyle("-fx-text-fill: #8B8000; -fx-background-color: #fff9c4; -fx-font-weight: bold; -fx-padding: 5px 15px; -fx-background-radius: 5;");
                        }
                        else if (item == StockRequestStatus.RETRIEVED) {
                            label.setStyle("-fx-text-fill: green; -fx-background-color: #e0f7e0; -fx-font-weight: bold; -fx-padding: 5px 10px; -fx-background-radius: 5;");
                        }
                        else {
                            label.setStyle("-fx-padding: 5px;");  // Default padding if not ACTIVE or COMPLETE
                        }

                        setGraphic(label);  // Set the Label as the graphic for this cell
                    }
                }
            };
        });


        apply.setOnAction(event -> apply(page, pageSize));
        search.setOnAction(event -> apply(page, pageSize));
        searchField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                apply(page, pageSize);
            }
        });
        back.setOnAction(event -> goBack());
        create.setOnAction(event -> create());
        pagination.setPageFactory(this::createPage);
    }

    private void loadPage(int page, int size) {
        apply(page, size);
    }

    private Node createPage(int pageIndex) {
        loadPage(pageIndex, pageSize);
        return stockRequestTable;
    }

    public void apply(int page, int size){
        Stage loadingStage = showLoadingScreen(primaryStage);

        Platform.runLater(() -> loadingStage.show());

        new Thread(() -> {

            try {
                StockRequestFilterRequest request = new StockRequestFilterRequest();
                request.setDepartment(this.data != null ? String.valueOf(this.data) : null);
                request.setStatus(status_comboBox.getSelectionModel().getSelectedItem() != null ? StockRequestStatus.valueOf(status_comboBox.getSelectionModel().getSelectedItem()) : null);
                request.setStartDate(start_datePicker.getValue() != null ? start_datePicker.getValue().atStartOfDay() : null);
                request.setEndDate(end_datePicker.getValue() != null ? end_datePicker.getValue().atTime(23, 59, 0) : null);

                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());

                String jsonString = objectMapper.writeValueAsString(request);

                String response = RestClient.post("/inventory/filterStockRequest?page=" + page + "&size=" + size + "&query=" + searchField.getText(), jsonString);

                ApiResponse<StockRequest> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponse<StockRequest>>() {
                });

                if (apiResponse.getResponseHeader().getResponseCode().equals("00")) {
                    Platform.runLater(() -> {
                        loadingStage.close();

                        stockRequestData.clear();

                        for (StockRequest item : apiResponse.getData()) {

                            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                            String createdDate = item.getCreatedAt().format(dateTimeFormatter);
                            String retrievedDate = "";
                            if (item.getRetrievedAt() != null) {
                                retrievedDate = item.getRetrievedAt().format(dateTimeFormatter);
                            }

                            StockRequestRow stockRequestRow = new StockRequestRow(item.getItem().getName(),
                                    item.getDepartment(), item.getQuantityRequested(), item.getStatus(),
                                    retrievedDate, createdDate, item);

                            stockRequestData.add(stockRequestRow);
                        }

                        stockRequestTable.setItems(stockRequestData);
                        pagination.setPageCount(apiResponse.getTotalPages());
                        pagination.setCurrentPageIndex(page);

                    });
                }
                else {
                    Platform.runLater(() -> {
                        loadingStage.close();
                        Utils.showAlertDialog(Alert.AlertType.ERROR,apiResponse.getResponseHeader().getResponseMessage(),apiResponse.getError() );
                    });
                }

            } catch (Exception e) {
                Platform.runLater(() -> {
                    loadingStage.close();
                    System.out.println(e);
                    Utils.showGeneralErrorDialog();
                });
            }
        }).start();
    }

    public void search(String query){
        searchField.clear();
        Stage loadingStage = Utils.showLoadingScreen(primaryStage);
        Platform.runLater(loadingStage::show);

        new Thread(() -> {

            try {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());

                String response = RestClient.get("/inventory/searchStockRequestByCategory?query=" + query + "&category=" + this.data + "&page=" + page + "&size=" + pageSize);
                ApiResponse<StockRequest> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponse<StockRequest>>() {});


                Platform.runLater(() -> {
                    try {
                        if (apiResponse.getResponseHeader().getResponseCode().equals("00")) {
                            Platform.runLater(() -> {
                                loadingStage.close();

                                stockRequestData.clear();

                                for (StockRequest item : apiResponse.getData()) {

                                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                                    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                                    String createdDate = item.getCreatedAt().format(dateTimeFormatter);
                                    String retrievedDate = "";
                                    if (item.getRetrievedAt() != null) {
                                        retrievedDate = item.getRetrievedAt().format(dateTimeFormatter);
                                    }

                                    StockRequestRow stockRequestRow = new StockRequestRow(item.getItem().getName(),
                                            item.getDepartment(), item.getQuantityRequested(), item.getStatus(),
                                            retrievedDate, createdDate, item);

                                    stockRequestData.add(stockRequestRow);
                                }

                                stockRequestTable.setItems(stockRequestData);
                                pagination.setPageCount(apiResponse.getTotalPages());
                                pagination.setCurrentPageIndex(page);

                            });
                        }
                        else {
                            Platform.runLater(() -> {
                                loadingStage.close();
                                Utils.showAlertDialog(Alert.AlertType.ERROR,apiResponse.getResponseHeader().getResponseMessage(),apiResponse.getError() );
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        loadingStage.close();
                        Utils.showGeneralErrorDialog();
                    }
                });


            } catch (Exception e) {
                Platform.runLater(() -> {
                    loadingStage.close();
                    Utils.showGeneralErrorDialog();
                });
            }

        }).start();

    }

    public void create(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bmh/hotelmanagementsystem/components/create-inventory-request.fxml"));
            Region form = loader.load();

            Stage formStage = new Stage();
            formStage.initModality(Modality.APPLICATION_MODAL);
            formStage.setTitle("Fill out the Form");

            CreateInventoryRequestController controller = loader.getController();
            controller.setPrimaryStage(formStage);
            controller.setData(this.data, "/com/bmh/hotelmanagementsystem/components/request-inventory.fxml");

            Scene formScene = new Scene(form);
            formStage.setScene(formScene);
            formStage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            Utils.showGeneralErrorDialog();
        }
    }

    public void goBack(){
        try {
            Utils utils = new Utils();
            utils.switchScreen(previousLocation, primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
