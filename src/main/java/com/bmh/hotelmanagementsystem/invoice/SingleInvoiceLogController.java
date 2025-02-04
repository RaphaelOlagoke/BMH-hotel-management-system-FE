package com.bmh.hotelmanagementsystem.invoice;

import com.bmh.hotelmanagementsystem.BackendService.entities.Invoice.Invoice;
import com.bmh.hotelmanagementsystem.BackendService.entities.Invoice.Item;
import com.bmh.hotelmanagementsystem.Controller;
import com.bmh.hotelmanagementsystem.Utils;
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
import java.time.format.DateTimeFormatter;


public class SingleInvoiceLogController extends Controller {

    private Stage primaryStage;
    private String previousLocation;

    private Invoice data;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setData(Object data, String previousLocation){
        this.data = (Invoice) data;
        this.previousLocation = previousLocation;

        invoice_ref.setText("Invoice Ref:  " + this.data.getRef());
        payment_status.setText("Payment Status:   " + this.data.getPaymentStatus());
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        payment_method.setText("Payment Method:   " + this.data.getPaymentMethod());
        service_type.setText("Service Type:   " + this.data.getService());
        issue_date.setText("Issue Date:   " + this.data.getIssueDate().format(dateTimeFormatter));
        if (this.data.getPaymentDate() != null) {
            payment_date.setText("Payment Date:   " + this.data.getPaymentDate().format(dateTimeFormatter));
        }
        total_amount.setText("Total Amount:   " + this.data.getTotalAmount());
        double totalPrice = 0.0;
        DecimalFormat formatter = new DecimalFormat("#,###.00");


        try {
            for (Item item : this.data.getItems()) {
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

                payments.getChildren().add(itemAnchorPlane);
            }

            if (this.data.getItems().size() == 0){
                payments.setVisible(false);
                scroll_pane_payments.setVisible(false);
            }
        }
        catch (Exception e){
            Utils.showGeneralErrorDialog();
        }
    }

    @FXML
    private Label invoice_ref;
    @FXML
    private Label payment_status;
    @FXML
    private Label issue_date;
    @FXML
    private Label payment_date;
    @FXML
    private Label payment_method;
    @FXML
    private Label service_type;
    @FXML
    private Label total_amount;

    @FXML
    private FlowPane payments;
    @FXML
    private ScrollPane scroll_pane_payments;

    @FXML
    private Button back;

    public void initialize() throws IOException {
        back.setOnAction(event -> goBack());
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
