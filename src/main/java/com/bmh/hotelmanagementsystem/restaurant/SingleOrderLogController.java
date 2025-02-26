package com.bmh.hotelmanagementsystem.restaurant;

import com.bmh.hotelmanagementsystem.BackendService.RestClient;
import com.bmh.hotelmanagementsystem.BackendService.entities.ApiResponseSingleData;
import com.bmh.hotelmanagementsystem.BackendService.entities.Restaurant.BillItem;
import com.bmh.hotelmanagementsystem.BackendService.entities.Restaurant.Order;
import com.bmh.hotelmanagementsystem.BackendService.enums.GuestLogStatus;
import com.bmh.hotelmanagementsystem.BackendService.enums.RestaurantOrderStatus;
import com.bmh.hotelmanagementsystem.Controller;
import com.bmh.hotelmanagementsystem.Utils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class SingleOrderLogController extends Controller {

    private Stage primaryStage;

    private String previousLocation;

    private Order data;

    @FXML
    private Label orderRef;
    @FXML
    private Label customerName;
    @FXML
    private Label date;
    @FXML
    private Label status;
    @FXML
    private VBox items;
    @FXML
    private Label subtotal;
    @FXML
    private Label tax;
    @FXML
    private Label discount;
    @FXML
    private Label amountPaid;
    @FXML
    private Label change;
    @FXML
    private Button printReceipt;
    @FXML
    private Button update;
    @FXML
    private Button back;

//    @FXML
//    private Button cancelOrder;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    DecimalFormat formatter = new DecimalFormat("#,###.00");

    public void setData(Object data, String previousLocation){
        this.data = (Order) data;
        this.previousLocation = previousLocation;

        orderRef.setText("Order Ref:  " + this.data.getRef());
        customerName.setText("Customer's Name:  " + this.data.getCustomerName());
        date.setText("Date:  " + dateTimeFormatter.format(this.data.getCreatedDateTime()));
        status.setText(this.data.getStatus().toJson());
        if (this.data.getStatus() == RestaurantOrderStatus.COMPLETED) {
            status.setStyle("-fx-text-fill: green; -fx-background-color: #e0f7e0; -fx-font-weight: bold; -fx-padding: 5px 10px; -fx-background-radius: 5;");
        } else if (this.data.getStatus() == RestaurantOrderStatus.READY) {
            status.setStyle("-fx-text-fill: blue; -fx-background-color: #e0e0f7; -fx-font-weight: bold; -fx-padding: 5px; -fx-background-radius: 5;");
        }
        else if (this.data.getStatus() == RestaurantOrderStatus.IN_PROGRESS) {
            status.setStyle("-fx-text-fill: #8B8000; -fx-background-color: #fff9c4; -fx-font-weight: bold; -fx-padding: 5px 15px; -fx-background-radius: 5;");
        }
        else if (this.data.getStatus() == RestaurantOrderStatus.CANCELED) {
            status.setStyle("-fx-text-fill: red; -fx-background-color: #f7e0e0; -fx-font-weight: bold; -fx-padding: 5px 15px; -fx-background-radius: 5;");
        }
        amountPaid.setText("Amount Paid:  " + formatter.format(this.data.getInvoice().getTotalAmount()) );

        double totalPrice = 0.0;

        try {

            for (BillItem item : this.data.getItems()) {
                FXMLLoader itemLoader = new FXMLLoader(getClass().getResource("/com/bmh/hotelmanagementsystem/components/item.fxml"));
                AnchorPane itemAnchorPlane = itemLoader.load();
                Label name = (Label) itemAnchorPlane.lookup("#name");
                Label quantity = (Label) itemAnchorPlane.lookup("#quantity");
                Label price = (Label) itemAnchorPlane.lookup("#price");

                name.setText(item.getName());
                quantity.setText(String.valueOf(item.getQuantity()));
                totalPrice += item.getPrice() * item.getQuantity();
//                DecimalFormat formatter = new DecimalFormat("#,###.00");

                String formattedPrice = formatter.format(item.getPrice());
                price.setText(formattedPrice);

                items.getChildren().add(itemAnchorPlane);
            }
        }
        catch (Exception e){
            Utils.showGeneralErrorDialog();
        }

        subtotal.setText("Subtotal:  " + formatter.format(totalPrice));

        if(this.data.getStatus() == RestaurantOrderStatus.IN_PROGRESS){
            update.setText("Mark as ready");
            update.setOnAction(event -> markAsReady(this.data.getRef()));
        }
        else if(this.data.getStatus() == RestaurantOrderStatus.READY){
            update.setText("Mark as complete");
            update.setOnAction(event -> markAsComplete(this.data.getRef()));
        }
        else{
            update.setText("Mark as Complete");
            update.setDisable(true);
//            cancelOrder.setDisable(true);
        }

    }

    public void initialize() throws IOException {
        back.setOnAction(event -> goBack());
    }

    public void markAsReady(String ref){

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Mark as Ready" );
        confirmationAlert.setHeaderText("Are you sure you want to mark order as ready?");
        confirmationAlert.setContentText("Please confirm your action.");

        Optional<ButtonType> result = confirmationAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                String response = RestClient.post("/restaurant/order/markAsReady?ref=" + ref, "");

                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());

                ApiResponseSingleData<Order> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponseSingleData<Order>>() {
                });

                if (apiResponse.getResponseHeader().getResponseCode().equals("00")) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle(apiResponse.getResponseHeader().getResponseMessage());
                    alert.setContentText("Order has been successfully marked as ready");
                    alert.showAndWait();

                    try {
                        data.setStatus(RestaurantOrderStatus.READY);
                        Utils utils = new Utils();
                        utils.switchScreenWithData("/com/bmh/hotelmanagementsystem/restaurant/single-order-log-view.fxml", primaryStage,data, "/com/bmh/hotelmanagementsystem/restaurant/order-log-view.fxml");
                    } catch (Exception e) {
                        e.printStackTrace();
                        alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setContentText("Something went wrong");
                        alert.showAndWait();
                    }
                } else {
                    Utils.showAlertDialog(Alert.AlertType.ERROR, apiResponse.getResponseHeader().getResponseMessage(), apiResponse.getError());

                }

            } catch (Exception e) {
                e.printStackTrace();
                Utils.showGeneralErrorDialog();
            }
        }
        else {
            confirmationAlert.close();
        }

    }

    public void markAsComplete(String ref){

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Mark as Ready" );
        confirmationAlert.setHeaderText("Are you sure you want to mark order as complete?");
        confirmationAlert.setContentText("Please confirm your action.");

        Optional<ButtonType> result = confirmationAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                String response = RestClient.post("/restaurant/order/markAsComplete?ref=" + ref, "");

                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());

                ApiResponseSingleData<Order> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponseSingleData<Order>>() {
                });

                if (apiResponse.getResponseHeader().getResponseCode().equals("00")) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle(apiResponse.getResponseHeader().getResponseMessage());
                    alert.setContentText("Order has been successfully marked as complete");
                    alert.showAndWait();

                    try {
                        data.setStatus(RestaurantOrderStatus.COMPLETED);
                        Utils utils = new Utils();
                        utils.switchScreenWithData("/com/bmh/hotelmanagementsystem/restaurant/single-order-log-view.fxml", primaryStage,data, "/com/bmh/hotelmanagementsystem/restaurant/order-log-view.fxml");
                    } catch (Exception e) {
                        e.printStackTrace();
                        alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setContentText("Something went wrong");
                        alert.showAndWait();
                    }
                } else {
                    Utils.showAlertDialog(Alert.AlertType.ERROR, apiResponse.getResponseHeader().getResponseMessage(), apiResponse.getError());

                }

            } catch (Exception e) {
                e.printStackTrace();
                Utils.showGeneralErrorDialog();
            }
        }
        else {
            confirmationAlert.close();
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
