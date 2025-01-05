package com.bmh.hotelmanagementsystem;

import com.bmh.hotelmanagementsystem.BackendService.entities.GuestLog;
import com.bmh.hotelmanagementsystem.BackendService.entities.Invoice;
import com.bmh.hotelmanagementsystem.BackendService.entities.Item;
import com.bmh.hotelmanagementsystem.components.PaymentItemController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.DecimalFormat;

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
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setGuestLog(GuestLog guestLog) throws IOException {
        this.guestLog = guestLog;
        guest_name.setText("Guest Name:   " + guestLog.getGuestName());
        room.setText("Room:   " + guestLog.getRoom().getRoomNumber());
        room_type.setText("Room type:   " + guestLog.getRoom().getRoomType());
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

    }
}
