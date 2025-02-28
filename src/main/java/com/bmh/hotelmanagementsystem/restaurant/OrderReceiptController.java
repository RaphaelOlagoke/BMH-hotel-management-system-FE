package com.bmh.hotelmanagementsystem.restaurant;

import com.bmh.hotelmanagementsystem.BackendService.entities.Restaurant.BillItem;
import com.bmh.hotelmanagementsystem.BackendService.entities.Restaurant.Order;
import com.bmh.hotelmanagementsystem.Controller;
import com.bmh.hotelmanagementsystem.Utils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;

public class OrderReceiptController extends Controller {

    private Stage primaryStage;
    private String previousLocation;

    private Order data;

    @FXML
    private Label invoice;
    @FXML
    private Label date;
    @FXML
    private Label customerName;
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
    private Button print;

    DecimalFormat formatter = new DecimalFormat("#,###.00");
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setData(Object data, String previousLocation){
        this.data = (Order) data;
        this.previousLocation = previousLocation;
        invoice.setText("Invoice:  " + this.data.getInvoice().getRef() );
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        date.setText("Date:  " + dateTimeFormatter.format(this.data.getInvoice().getIssueDate()) );
        customerName.setText("Customer's name:  " + this.data.getCustomerName() );
        subtotal.setText("Subtotal:  ₦" + this.data.getInvoice().getTotalAmount());
        if(this.data.getInvoice().getDiscountCode() != null && !this.data.getInvoice().getDiscountCode().equals("")){
            amountPaid.setText("Amount Paid:  ₦" + formatter.format(this.data.getInvoice().getAmountPaid()) );
            discount.setText("Discount:  ₦" + this.data.getInvoice().getDiscountAmount());
        }
        else {
            amountPaid.setText("Amount Paid:  ₦" + formatter.format(this.data.getInvoice().getAmountPaid()));
            discount.setText("Discount:  ₦" + 0.0);
        }

        double totalPrice = 0.0;
        DecimalFormat formatter = new DecimalFormat("#,###.00");

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
    }
}
