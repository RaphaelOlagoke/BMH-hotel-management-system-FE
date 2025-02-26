package com.bmh.hotelmanagementsystem.restaurant;

import com.bmh.hotelmanagementsystem.BackendService.RestClient;
import com.bmh.hotelmanagementsystem.BackendService.enums.GuestLogStatus;
import com.bmh.hotelmanagementsystem.dto.restaurant.OrderTableRow;
import com.bmh.hotelmanagementsystem.BackendService.entities.ApiResponse;
import com.bmh.hotelmanagementsystem.BackendService.entities.Restaurant.Order;
import com.bmh.hotelmanagementsystem.BackendService.entities.Restaurant.OrderFilterRequest;
import com.bmh.hotelmanagementsystem.BackendService.enums.RestaurantOrderStatus;
import com.bmh.hotelmanagementsystem.Controller;
import com.bmh.hotelmanagementsystem.Utils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static com.bmh.hotelmanagementsystem.Utils.showLoadingScreen;

public class OrderLogController extends Controller {

    private Stage primaryStage;

    private String previousLocation;

    private Object data;

    @FXML
    private ComboBox<String> status_comboBox;
    @FXML
    private DatePicker start_date;
    @FXML
    private DatePicker end_date;
    @FXML
    private Button apply;
    @FXML
    private TableView<OrderTableRow> ordersTable;
    @FXML
    private TableColumn<OrderTableRow, String> customerNameColumn;
    @FXML
    private TableColumn<OrderTableRow, String> orderRefColumn;
    @FXML
    private TableColumn<OrderTableRow, String> dateColumn;
    @FXML
    private TableColumn<OrderTableRow, RestaurantOrderStatus> statusColumn;
    @FXML
    private TableColumn<OrderTableRow, Void> viewMoreColumn;

    @FXML
    private Pagination pagination;

    private ObservableList<OrderTableRow> tableData = FXCollections.observableArrayList();

    private int pageSize = 10;
    private int page = 0;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setData(Object data, String previousLocation){
        this.data = data;
        this.previousLocation = previousLocation;
    }

    public void initialize() {

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

        end_date.setConverter(new javafx.util.StringConverter<LocalDate>() {
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

        ObservableList<String> order_status = FXCollections.observableArrayList();

        order_status.add(null);
        for (RestaurantOrderStatus orderStatus : RestaurantOrderStatus.values()) {
            order_status.add(orderStatus.toJson());
        }

        status_comboBox.setItems(order_status);

        customerNameColumn.setCellValueFactory(cellData -> cellData.getValue().customer_columnProperty());
        orderRefColumn.setCellValueFactory(cellData -> cellData.getValue().ref_columnProperty());
        dateColumn.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
        statusColumn.setCellValueFactory(cellData -> cellData.getValue().status_columnProperty());
        viewMoreColumn.setCellFactory(createViewMoreButtonCellFactory());

        ordersTable.setRowFactory(tableView -> {
            TableRow<OrderTableRow> row = new TableRow<>();

            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    OrderTableRow clickedRow = row.getItem();
                    showGuestDetails(clickedRow);
                }
            });

            return row;
        });

        statusColumn.setCellFactory(column -> {
            return new TableCell<OrderTableRow, RestaurantOrderStatus>() {
                @Override
                protected void updateItem(RestaurantOrderStatus item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        Label label = new Label(item.toJson());
                        label.setStyle("-fx-font-weight: bold; -fx-padding: 5px;");


                        if (item == RestaurantOrderStatus.READY) {
                            label.setStyle("-fx-text-fill: blue; -fx-background-color: #e0e0f7; -fx-font-weight: bold; -fx-padding: 5px; -fx-background-radius: 5;");
                        } else if (item == RestaurantOrderStatus.COMPLETED) {
                            label.setStyle("-fx-text-fill: green; -fx-background-color: #e0f7e0; -fx-font-weight: bold; -fx-padding: 5px 10px; -fx-background-radius: 5;");
                        }
                        else if (item == RestaurantOrderStatus.IN_PROGRESS) {
                            label.setStyle("-fx-text-fill: #8B8000; -fx-background-color: #fff9c4; -fx-font-weight: bold; -fx-padding: 5px 15px; -fx-background-radius: 5;");
                        }
                        else if (item == RestaurantOrderStatus.CANCELED) {
                            label.setStyle("-fx-text-fill: red; -fx-background-color: #f7e0e0; -fx-font-weight: bold; -fx-padding: 5px 15px; -fx-background-radius: 5;");
                        }
                        else {
                            label.setStyle("-fx-padding: 5px;");  // Default padding if not ACTIVE or COMPLETE
                        }

                        setGraphic(label);  // Set the Label as the graphic for this cell
                    }
                }
            };
        });


        pagination.setPageFactory(this::createPage);

        apply.setOnAction(event -> apply(page, pageSize));
    }

    private void loadPage(int page, int size) {
        apply(page,size);
    }

    private Node createPage(int pageIndex) {
        loadPage(pageIndex, pageSize);
        return ordersTable;
    }

    private Callback<TableColumn<OrderTableRow, Void>, TableCell<OrderTableRow, Void>> createViewMoreButtonCellFactory() {
        return new Callback<>() {
            @Override
            public TableCell<OrderTableRow, Void> call(TableColumn<OrderTableRow, Void> param) {
                final TableCell<OrderTableRow, Void> cell = new TableCell<OrderTableRow, Void>() {

                    final FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bmh/hotelmanagementsystem/components/view_more_button.fxml"));
                    final Button button;

                    {
                        try {
                            button = loader.load();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    private final Button btn = new Button("View More");

                    {
                        button.setOnAction(event -> {
                            OrderTableRow orderTableRow = getTableView().getItems().get(getIndex());
                            showGuestDetails(orderTableRow);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(button);
                        }
                    }
                };
                return cell;
            }
        };
    }

    private void showGuestDetails(OrderTableRow orderTableRow) {
        Order order  = orderTableRow.getOrder();
        try {
            Utils utils = new Utils();
            utils.switchScreenWithData("/com/bmh/hotelmanagementsystem/restaurant/single-order-log-view.fxml", primaryStage,order, "/com/bmh/hotelmanagementsystem/restaurant/order-log-view.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void apply(int page, int size) {
        Stage loadingStage = Utils.showLoadingScreen(primaryStage);
        loadingStage.show();

        new Thread(() -> {

            try {

                OrderFilterRequest request = new OrderFilterRequest();
                request.setStatus(status_comboBox.getSelectionModel().getSelectedItem() != null? RestaurantOrderStatus.valueOf(status_comboBox.getSelectionModel().getSelectedItem()) : null);
                request.setStartDate(start_date.getValue() != null ? start_date.getValue().atStartOfDay() : null);
                request.setEndDate(end_date.getValue() != null ? end_date.getValue().atTime(23, 59, 0) : null);


                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());

                String jsonString = objectMapper.writeValueAsString(request);

                String response = RestClient.post("/restaurant/order/filter?page=" + page + "&size=" + size, jsonString);
                ApiResponse<Order> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponse<Order>>() {});


                Platform.runLater(() -> {
                    try {
                        if (apiResponse.getResponseHeader().getResponseCode().equals("00")) {
                            loadingStage.close();

                            tableData.clear();
                            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                            for (Order order : apiResponse.getData()) {
                                String date = "";
                            if (order.getCreatedDateTime() != null) {
                                date = order.getCreatedDateTime().format(dateTimeFormatter);
                            }

                                OrderTableRow orderTableRow = new OrderTableRow(order.getCustomerName(), order.getRef(), order.getStatus(), date, order);
                                tableData.add(orderTableRow);
                            }

                            ordersTable.setItems(tableData);
                            pagination.setPageCount(apiResponse.getTotalPages());
                            pagination.setCurrentPageIndex(page);
                        } else {
                            loadingStage.close();
                            Utils.showAlertDialog(Alert.AlertType.ERROR, apiResponse.getResponseHeader().getResponseMessage(), apiResponse.getError());
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
