package com.bmh.hotelmanagementsystem.restaurant;

import com.bmh.hotelmanagementsystem.BackendService.RestClient;
import com.bmh.hotelmanagementsystem.BackendService.entities.discount.Discount;
import com.bmh.hotelmanagementsystem.dto.restaurant.OrderDetails;
import com.bmh.hotelmanagementsystem.dto.restaurant.OrderItem;
import com.bmh.hotelmanagementsystem.BackendService.entities.ApiResponseSingleData;
import com.bmh.hotelmanagementsystem.BackendService.entities.Restaurant.*;
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
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Optional;

public class CreateOrderController extends Controller {

    private Stage primaryStage;
    private String previousLocation;

    private OrderDetails data;

    @FXML
    private TextField customerName;
    @FXML
    private Label paymentMethodLabel;
    @FXML
    private Label subTotalLabel;
    @FXML
    private Label taxLabel;
    @FXML
    private Label discountLabel;
    @FXML
    private Label totalLabel;
    @FXML
    private Button chargeToRoom;
    @FXML
    private Button confirmPayment;
    @FXML
    private TableView<OrderItem> itemsTable;
    @FXML
    private TableColumn<OrderItem, Integer> quantityColumn;
    @FXML
    private TableColumn<OrderItem, String> nameColumn;
    @FXML
    private TableColumn<OrderItem, Double> priceColumn;

//    @FXML
//    private TextField discount_code;

    private ObservableList<OrderItem> tableData = FXCollections.observableArrayList();

    private Double subTotal = 0.0;
    private Double discount = 0.0;
    private Double tax = 0.0;
    private Double total = 0.0;

    DecimalFormat formatter = new DecimalFormat("#,###.00");

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setData(Object data, String previousLocation){
        this.data = (OrderDetails) data;
        this.previousLocation = previousLocation;

        paymentMethodLabel.setText("Payment Method:  " + this.data.getPaymentMethod().toJson());
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        quantityColumn.setCellValueFactory(cellData -> cellData.getValue().quantityProperty().asObject());
        priceColumn.setCellValueFactory(cellData -> cellData.getValue().priceProperty().asObject());

        priceColumn.setCellValueFactory(cellData -> cellData.getValue().priceProperty().asObject());
        priceColumn.setCellFactory(param -> new TableCell<OrderItem, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(formatter.format(price));
                }
            }
        });


        for (BillItem billItem : this.data.getBillItems()) {

            OrderItem orderItem = new OrderItem(billItem.getName(), billItem.getQuantity(), billItem.getPrice());

            tableData.add(orderItem);

            double itemTotal = billItem.getPrice() * billItem.getQuantity();
            subTotal += itemTotal;
        }

        itemsTable.setItems(tableData);
        total = (subTotal) + tax;

        subTotalLabel.setText("Subtotal:  ₦" + formatter.format(subTotal));
        totalLabel.setText("Total:  ₦" + formatter.format(total));
        discountLabel.setText("Discount:  ₦0.00");
        taxLabel.setText("");


        for (TableColumn<OrderItem, ?> column : itemsTable.getColumns()) {
            column.setStyle("-fx-pref-height: 70px;");
        }
    }


    @FXML
    public void initialize() throws IOException {
//        discount_code.setOnKeyPressed(event -> {
//            if (event.getCode() == KeyCode.ENTER) {
//                getDiscount();
//            }
//        });
        confirmPayment.setOnAction(event -> createOrder());
    }

//    public void getDiscount(){
//        Stage loadingStage = Utils.showLoadingScreen(primaryStage);
//        Platform.runLater(() -> loadingStage.show());
//
//        new Thread(() -> {
//            try {
//
//                ObjectMapper objectMapper = new ObjectMapper();
//                objectMapper.registerModule(new JavaTimeModule());
//
//                String response = RestClient.get("/discount/code?code=" + discount_code.getText());
//                ApiResponseSingleData<Discount> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponseSingleData<Discount>>() {
//                });
//
//                if (apiResponse.getResponseHeader().getResponseCode().equals("00")) {
//                    Platform.runLater(() -> {
//                        loadingStage.close();
//                        subTotal = 0.0;
//                        discount = 0.0;
//                        total = 0.0;
//                        for (BillItem billItem : this.data.getBillItems()) {
//
//
//                            double itemTotal = billItem.getPrice() * billItem.getQuantity();
//                            subTotal += itemTotal;
//                        }
//
//                        total = (subTotal) + tax;
//
//                        discount = total * apiResponse.getData().getPercentage()/100.0;
//                        total = (subTotal - discount) + tax;
//
//                        discountLabel.setText("Discount:  ₦" + formatter.format(discount));
//                        subTotalLabel.setText("Subtotal:  ₦" + formatter.format(subTotal));
//                        totalLabel.setText("Total:  ₦" + formatter.format(total));
//
//                    });
//                } else {
//                    Platform.runLater(() -> {
//                        loadingStage.close();
//                        Utils.showAlertDialog(Alert.AlertType.ERROR, apiResponse.getResponseHeader().getResponseMessage(), apiResponse.getError());
//                    });
//
//                }
//
//            } catch (Exception e) {
//                Platform.runLater(() -> {
//                    loadingStage.close();
//                    e.printStackTrace();
//                    Utils.showGeneralErrorDialog();
//                });
//            }
//        }).start();
//    }

    public void createOrder(){
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirm Payment");
        confirmationAlert.setHeaderText("Are you sure you want to mark this payment as successful?");
        confirmationAlert.setContentText("Please confirm your action.");

        Optional<ButtonType> result = confirmationAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            Stage loadingStage = Utils.showLoadingScreen(primaryStage);
            Platform.runLater(() -> loadingStage.show());

            new Thread(() -> {
                try {

                    CreateOrderRequest request = new CreateOrderRequest();
                    request.setCustomerName(customerName.getText());
                    request.setItems(data.getBillItems());
                    request.setPaymentMethod(data.getPaymentMethod());
//                    if(discount_code.getText() != null || !discount_code.getText().equals("")){
//                        request.setDiscountCode(discount_code.getText());
//                    }

                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.registerModule(new JavaTimeModule());

                    String jsonString = objectMapper.writeValueAsString(request);

                    String response = RestClient.post("/restaurant/order/", jsonString);
                    ApiResponseSingleData<Order> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponseSingleData<Order>>() {
                    });

                    if (apiResponse.getResponseHeader().getResponseCode().equals("00")) {
                        Platform.runLater(() -> {
                            loadingStage.close();
                            Utils.showAlertDialog(Alert.AlertType.INFORMATION, "Created Successfully", "Order created successfully");

                            try {
                                Utils utils = new Utils();
                                utils.switchScreenWithData("/com/bmh/hotelmanagementsystem/restaurant/receipt.fxml", primaryStage, apiResponse.getData(), previousLocation);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

//                            primaryStage.close();

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
        else {
            confirmationAlert.close();
        }

    }
}
