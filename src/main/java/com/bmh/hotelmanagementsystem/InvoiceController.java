package com.bmh.hotelmanagementsystem;

import com.bmh.hotelmanagementsystem.BackendService.RestClient;
import com.bmh.hotelmanagementsystem.BackendService.entities.*;
import com.bmh.hotelmanagementsystem.BackendService.enums.PaymentMethod;
import com.bmh.hotelmanagementsystem.BackendService.enums.PaymentStatus;
import com.bmh.hotelmanagementsystem.components.PaymentItemController;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class InvoiceController extends Controller{

    private Stage primaryStage;
    private GuestLog guestLog;

    @FXML
    private Label guest_name;
    @FXML
    private Label room;
    @FXML
    private Label room_type;

    @FXML
    private Label total_outstanding_payment;

    @FXML
    private FlowPane outstanding_payments;

    @FXML
    private Button back;
    @FXML
    private Button  complete;

    @FXML
    private ComboBox<String> payment_method;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setGuestLog(GuestLog guestLog) throws IOException {
        StringBuilder rooms = new StringBuilder();
        StringBuilder types = new StringBuilder();

        int size = guestLog.getGuestLogRooms().size();
        int index = 0;

        for (GuestLogRoom guestLogRoom : guestLog.getGuestLogRooms()) {
            rooms.append(guestLogRoom.getRoom().getRoomNumber());
            types.append(guestLogRoom.getRoom().getRoomType());

            if (index < size - 1) {
                rooms.append(", ");
                types.append(", ");
            }
            index++;
        }

        this.guestLog = guestLog;
        guest_name.setText("Guest Name:   " + guestLog.getGuestName());
        room.setText("Room(s):   " + rooms);
        room_type.setText("Room type(s):   " + types);
        DecimalFormat formatter = new DecimalFormat("#,###.00");

        String formattedOutstandingPrice = formatter.format(guestLog.getTotalAmountDue());
        total_outstanding_payment.setText("Outstanding Payments:   " + formattedOutstandingPrice);


        for(Invoice invoice : guestLog.getInvoices()){
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/bmh/hotelmanagementsystem/components/payment_item.fxml"));
            AnchorPane anchorPane = fxmlLoader.load();
            PaymentItemController controller = fxmlLoader.getController();

            Label service = (Label) anchorPane.lookup("#service");
            Label status = (Label) anchorPane.lookup("#status");
            Label date = (Label) anchorPane.lookup("#date");
            Label total = (Label) anchorPane.lookup("#total_price");
//            ScrollPane scroll_items = (ScrollPane) anchorPane.lookup("#scroll_items");
//            FlowPane items = (FlowPane) scroll_items.lookup("#items");
            FlowPane itemsFlowPane = controller.getItems();

            service.setText(invoice.getService());
            date.setText(String.valueOf(invoice.getIssueDate()));
            status.setText(invoice.getPaymentStatus().toJson());
            double totalPrice = 0.0;

            for(Item item : invoice.getItems()){
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

                itemsFlowPane.getChildren().add(itemAnchorPlane);
            }
            if(invoice.getItems().isEmpty()){
                totalPrice = invoice.getTotalAmount();
            }
            total.setText("Total:  " + String.valueOf(totalPrice));
            outstanding_payments.getChildren().add(anchorPane);
        }
    }

    @FXML
    public void initialize() throws IOException {
        back.setOnAction(event -> goBack());
        complete.setOnAction(event -> resolveInvoice());
        ObservableList<String> payment_Methods = FXCollections.observableArrayList();

        payment_Methods.add(PaymentMethod.CARD.toJson());
        payment_Methods.add(PaymentMethod.TRANSFER.toJson());
        payment_Methods.add(PaymentMethod.CASH.toJson());

        payment_method.setItems(payment_Methods);
    }

    public void goBack(){
        try {
            Utils utils = new Utils();
            utils.switchScreen("checkOut-view.fxml", primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void resolveInvoice(){
        if (payment_method.getSelectionModel().getSelectedItem() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid request");
//                alert.setHeaderText("This is a header");
            alert.setContentText("Payment method can not be empty");
            alert.showAndWait();
            return;
        }
        try {
            List<String> invoiveRefs = new ArrayList<>();
            for (Invoice invoice : guestLog.getInvoices()){
                if (invoice.getPaymentStatus().equals(PaymentStatus.DUE)){
                    invoiveRefs.add(invoice.getRef());
                }
            }

            if (invoiveRefs.isEmpty()){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Invalid request");
//                alert.setHeaderText("This is a header");
                alert.setContentText("There are no outstanding payments");
                alert.showAndWait();
                return;
            }

            ResolveInvoiceRequest request = new ResolveInvoiceRequest();
            request.setPaymentMethod(PaymentMethod.valueOf(payment_method.getSelectionModel().getSelectedItem()));
            request.setInvoiceRefs(invoiveRefs);

            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString = objectMapper.writeValueAsString(request);

            String response = RestClient.post("/invoice/resolve",jsonString);
            ApiResponse<?> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponse<?>>() {
            });

            if(apiResponse.getResponseHeader().getResponseCode().equals("00")){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Payment Complete");
                alert.setContentText("Payment Successful");
                alert.showAndWait();
                goBack();
            }
            else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle(apiResponse.getResponseHeader().getResponseMessage());
                alert.setContentText(apiResponse.getError());
                alert.showAndWait();
            }

        } catch (Exception e) {
            System.out.println(e);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
//                alert.setHeaderText("This is a header");
            alert.setContentText("Something went wrong");
            alert.showAndWait();
        }
    }
}
