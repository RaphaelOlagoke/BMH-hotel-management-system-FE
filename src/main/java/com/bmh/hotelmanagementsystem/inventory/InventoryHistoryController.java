package com.bmh.hotelmanagementsystem.inventory;

import com.bmh.hotelmanagementsystem.BackendService.RestClient;
import com.bmh.hotelmanagementsystem.BackendService.entities.ApiResponse;
import com.bmh.hotelmanagementsystem.BackendService.entities.inventory.StockHistory;
import com.bmh.hotelmanagementsystem.BackendService.entities.inventory.StockHistoryFilterRequest;
import com.bmh.hotelmanagementsystem.BackendService.entities.inventory.StockItem;
import com.bmh.hotelmanagementsystem.BackendService.entities.inventory.StockItemFilterRequest;
import com.bmh.hotelmanagementsystem.BackendService.enums.*;
import com.bmh.hotelmanagementsystem.Controller;
import com.bmh.hotelmanagementsystem.Utils;
import com.bmh.hotelmanagementsystem.dto.inventory.StockHistoryRow;
import com.bmh.hotelmanagementsystem.dto.inventory.StockRow;
import com.bmh.hotelmanagementsystem.dto.room.GuestReservation;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static com.bmh.hotelmanagementsystem.Utils.showLoadingScreen;

public class InventoryHistoryController extends Controller {

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
    private TextField searchField;
    @FXML
    private Button search;

    @FXML
    private ComboBox<String> department_comboBox;
    @FXML
    private ComboBox<String> reason_comboBox;
    @FXML
    private ComboBox<String> action_comboBox;

    @FXML
    private DatePicker start_datePicker;
    @FXML
    private DatePicker end_datePicker;

    @FXML
    private TableView<StockHistoryRow> stockHistoryTable;
    @FXML
    private TableColumn<StockHistoryRow, String> ref_column;
    @FXML
    private TableColumn<StockHistoryRow, String> item_name_column;
    @FXML
    private TableColumn<StockHistoryRow, Integer> quantity_column;
    @FXML
    private TableColumn<StockHistoryRow, String> unit_column;
    @FXML
    private TableColumn<StockHistoryRow, String> department_column;
    @FXML
    private TableColumn<StockHistoryRow, StockActionReason> reason_column;
    @FXML
    private TableColumn<StockHistoryRow, StockHistoryAction> action_column;
    @FXML
    private TableColumn<StockHistoryRow, String> created_date_column;

    @FXML
    private Pagination pagination;
    @FXML
    private Button apply;

    String currentQuery = "";

    private ObservableList<StockHistoryRow> stockHistoryData = FXCollections.observableArrayList();

    private int pageSize = 10;
    private int page = 0;


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

        ObservableList<String> department = FXCollections.observableArrayList();

        department.add(null);
        for (StockItemCategory category : StockItemCategory.values()) {
            department.add(category.toJson());
        }

        department_comboBox.setItems(department);

        ObservableList<String> reason = FXCollections.observableArrayList();

        reason.add(null);
        for (StockActionReason item : StockActionReason.values()) {
            reason.add(item.toJson());
        }

        reason_comboBox.setItems(reason);

        ObservableList<String> action = FXCollections.observableArrayList();

        action.add(null);
        for (StockHistoryAction item : StockHistoryAction.values()) {
            action.add(item.toJson());
        }

        action_comboBox.setItems(action);

        ref_column.setCellValueFactory(cellData -> cellData.getValue().ref_columnProperty());
        item_name_column.setCellValueFactory(cellData -> cellData.getValue().item_name_columnProperty());
        quantity_column.setCellValueFactory(cellData -> cellData.getValue().quantityMoved_columnProperty().asObject());
        unit_column.setCellValueFactory(cellData -> cellData.getValue().unit_columnProperty());
        department_column.setCellValueFactory(cellData -> cellData.getValue().department_columnProperty());
        reason_column.setCellValueFactory(cellData -> cellData.getValue().reason_columnProperty());
        action_column.setCellValueFactory(cellData -> cellData.getValue().action_columnProperty());
        created_date_column.setCellValueFactory(cellData -> cellData.getValue().timestamp_columnProperty());

        action_column.setCellFactory(column -> {
            return new TableCell<StockHistoryRow, StockHistoryAction>() {
                @Override
                protected void updateItem(StockHistoryAction item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);  // Reset cell content
                    } else {
                        // Create a Label for the status
                        Label label = new Label(item.toJson());
                        label.setStyle("-fx-font-weight: bold; -fx-padding: 5px;");

                        // Apply styles based on the status
                        if (item == StockHistoryAction.ADDED) {
                            label.setStyle("-fx-text-fill: green; -fx-background-color: #e0f7e0; -fx-font-weight: bold; -fx-padding: 5px 10px; -fx-background-radius: 5;");
                        }
                        else {
                            label.setStyle("-fx-text-fill: red; -fx-background-color: #f7e0e0; -fx-font-weight: bold; -fx-padding: 5px 15px; -fx-background-radius: 5;");
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
        pagination.setPageFactory(this::createPage);
    }

    private void loadPage(int page, int size) {
        apply(page, size);
    }

    private Node createPage(int pageIndex) {
        loadPage(pageIndex, pageSize);
        return stockHistoryTable;
    }

    public void apply(int page, int size){
        Stage loadingStage = showLoadingScreen(primaryStage);

        Platform.runLater(() -> loadingStage.show());

        new Thread(() -> {

            try {
                StockHistoryFilterRequest request = new StockHistoryFilterRequest();
                request.setDepartment(department_comboBox.getSelectionModel().getSelectedItem() != null ? department_comboBox.getSelectionModel().getSelectedItem() : null);
                request.setReason(reason_comboBox.getSelectionModel().getSelectedItem() != null ? StockActionReason.valueOf(reason_comboBox.getSelectionModel().getSelectedItem()) : null);
                request.setAction(action_comboBox.getSelectionModel().getSelectedItem() != null ? StockHistoryAction.valueOf(action_comboBox.getSelectionModel().getSelectedItem()) : null);
                request.setStartDate(start_datePicker.getValue() != null ? start_datePicker.getValue().atStartOfDay() : null);
                request.setEndDate(end_datePicker.getValue() != null ? end_datePicker.getValue().atTime(23, 59, 0) : null);

                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());

                String jsonString = objectMapper.writeValueAsString(request);

                String response = RestClient.post("/inventory/filterStockHistory?page=" + page + "&size=" + size + "&query=" + searchField.getText(), jsonString);

                ApiResponse<StockHistory> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponse<StockHistory>>() {
                });

                if (apiResponse.getResponseHeader().getResponseCode().equals("00")) {
                    Platform.runLater(() -> {
                        loadingStage.close();

                        stockHistoryData.clear();

                        for (StockHistory item : apiResponse.getData()) {

                            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                            String createdDate = "";
                            if (item.getTimestamp() != null) {
                                createdDate = item.getTimestamp().format(dateTimeFormatter);
                            }

                            StockHistoryRow stockHistoryRow = new StockHistoryRow(item.getRef(), item.getItem().getName(),
                                    item.getDepartment(), item.getQuantityMoved(), item.getUnit(), item.getReason(), item.getAction(), createdDate);

                            stockHistoryData.add(stockHistoryRow);
                        }

                        stockHistoryTable.setItems(stockHistoryData);
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
//        searchField.clear();
        Stage loadingStage = Utils.showLoadingScreen(primaryStage);
        Platform.runLater(loadingStage::show);

        new Thread(() -> {

            try {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());

                String response = RestClient.get("/inventory/searchStockHistory?query=" + query +"&page=" + page + "&size=" + pageSize);
                ApiResponse<StockHistory> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponse<StockHistory>>() {});


                Platform.runLater(() -> {
                    try {
                        if (apiResponse.getResponseHeader().getResponseCode().equals("00")) {
                            Platform.runLater(() -> {
                                loadingStage.close();

                                stockHistoryData.clear();

                                for (StockHistory item : apiResponse.getData()) {

                                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                                    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                                    String createdDate = "";
                                    if (item.getTimestamp() != null) {
                                        createdDate = item.getTimestamp().format(dateTimeFormatter);
                                    }

                                    StockHistoryRow stockHistoryRow = new StockHistoryRow(item.getRef(), item.getItem().getName(),
                                            item.getDepartment(), item.getQuantityMoved(), item.getUnit(), item.getReason(), item.getAction(), createdDate);

                                    stockHistoryData.add(stockHistoryRow);
                                }

                                stockHistoryTable.setItems(stockHistoryData);
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
}
