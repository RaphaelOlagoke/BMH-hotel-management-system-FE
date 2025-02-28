package com.bmh.hotelmanagementsystem.refund;

import com.bmh.hotelmanagementsystem.BackendService.RestClient;
import com.bmh.hotelmanagementsystem.BackendService.entities.ApiResponse;
import com.bmh.hotelmanagementsystem.BackendService.entities.ApiResponseSingleData;
import com.bmh.hotelmanagementsystem.BackendService.entities.Invoice.CreateInvoiceRequest;
import com.bmh.hotelmanagementsystem.BackendService.entities.Invoice.Invoice;
import com.bmh.hotelmanagementsystem.BackendService.entities.Invoice.Item;
import com.bmh.hotelmanagementsystem.BackendService.entities.refund.CreateRefundRequest;
import com.bmh.hotelmanagementsystem.BackendService.entities.refund.Refund;
import com.bmh.hotelmanagementsystem.BackendService.entities.refund.RefundLogFilterRequest;
import com.bmh.hotelmanagementsystem.BackendService.enums.PaymentMethod;
import com.bmh.hotelmanagementsystem.BackendService.enums.PaymentStatus;
import com.bmh.hotelmanagementsystem.BackendService.enums.ServiceType;
import com.bmh.hotelmanagementsystem.Controller;
import com.bmh.hotelmanagementsystem.Utils;
import com.bmh.hotelmanagementsystem.dto.invoice.ItemRow;
import com.bmh.hotelmanagementsystem.dto.refund.RefundRow;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CreateRefundController  extends Controller {

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
    private TextField invoice_ref_searchField;
    @FXML
    private Button search;

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
    private Label discount_code;
    //    @FXML
//    private Label discount_percentage;
    @FXML
    private Label discount_amount;
    @FXML
    private Label total_amount;

    @FXML
    private Label amount_paid;

    @FXML
    private FlowPane payments;
    @FXML
    private ScrollPane scroll_pane_payments;

    @FXML
    private TextField reasonTextField;

    @FXML
    private Button create;


    DecimalFormat formatter = new DecimalFormat("#,###.00");

    public void initialize() {
        create.setOnAction(event -> createRefund());

        search.setOnAction(event -> findInvoice());
        invoice_ref_searchField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                findInvoice();
            }
        });
    }

    public void findInvoice(){
        Stage loadingStage = Utils.showLoadingScreen(primaryStage);
        loadingStage.show();

        new Thread(() -> {

            try {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());


                String response = RestClient.get("/invoice/ref?ref=" + invoice_ref_searchField.getText() );
                ApiResponseSingleData<Invoice> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponseSingleData<Invoice>>() {});


                Platform.runLater(() -> {
                    try {
                        if (apiResponse.getResponseHeader().getResponseCode().equals("00")) {
                            // Close the loading screen
                            loadingStage.close();

                            Invoice invoice = apiResponse.getData();

                            invoice_ref.setText("Invoice Ref:  " + invoice.getRef());
                            payment_status.setText(invoice.getPaymentStatus().toJson());
                            if (invoice.getPaymentStatus() == PaymentStatus.PAID) {
                                payment_status.setStyle("-fx-text-fill: green; -fx-background-color: #e0f7e0; -fx-font-weight: bold; -fx-padding: 5px 10px; -fx-background-radius: 5;");
                            } else if (invoice.getPaymentStatus() == PaymentStatus.UNPAID) {
                                payment_status.setStyle("-fx-text-fill: #8B8000; -fx-background-color: #fff9c4; -fx-font-weight: bold; -fx-padding: 5px 15px; -fx-background-radius: 5;");
                            }
                            else if (invoice.getPaymentStatus() == PaymentStatus.DEBIT) {
                                payment_status.setStyle("-fx-text-fill: red; -fx-background-color: #f7e0e0; -fx-font-weight: bold; -fx-padding: 5px 15px; -fx-background-radius: 5;");
                            }
                            else if (invoice.getPaymentStatus() == PaymentStatus.REFUNDED) {
                                payment_status.setStyle("-fx-text-fill: #0059AC; -fx-background-color: #e0e0f7; -fx-font-weight: bold; -fx-padding: 5px 15px; -fx-background-radius: 5;");
                            }
                            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                            payment_method.setText("Payment Method:   " + invoice.getPaymentMethod());
                            service_type.setText("Service Type:   " + invoice.getService());
                            issue_date.setText("Issue Date:   " + invoice.getIssueDate().format(dateTimeFormatter));
                            if (invoice.getPaymentDate() != null) {
                                payment_date.setText("Payment Date:   " + invoice.getPaymentDate().format(dateTimeFormatter));
                            }
                            total_amount.setText("Total Amount:   ₦" + formatter.format(invoice.getTotalAmount()));
                            if(invoice.getDiscountAmount() != null && invoice.getDiscountAmount() != 0){
                                discount_amount.setText("Discount Amount:   ₦" + formatter.format(invoice.getDiscountAmount()));
                            }
                            amount_paid.setText("Amount Paid:   ₦" + formatter.format(invoice.getAmountPaid()));
                            if(invoice.getDiscountCode() != null && !invoice.getDiscountCode().equals("")){
                                discount_code.setText("Discount code:   " + invoice.getDiscountCode());
                            }
                            double totalPrice = 0.0;
                            DecimalFormat formatter = new DecimalFormat("#,###.00");


                            try {
                                for (Item item : invoice.getItems()) {
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

                                if (invoice.getItems().size() == 0){
                                    payments.setVisible(false);
                                    scroll_pane_payments.setVisible(false);
                                }
                            }
                            catch (Exception e){
                                Utils.showGeneralErrorDialog();
                            }

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
                    e.printStackTrace();
                    loadingStage.close();
                    Utils.showGeneralErrorDialog();
                });
            }

        }).start();
    }

    public void createRefund(){
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirm Payment");
        confirmationAlert.setHeaderText("Are you sure you want to create this refund?");
        confirmationAlert.setContentText("Please confirm your action.");

        Optional<ButtonType> result = confirmationAlert.showAndWait();


        if (result.isPresent() && result.get() == ButtonType.OK) {

            Stage loadingStage = Utils.showLoadingScreen(primaryStage);
            Platform.runLater(() -> loadingStage.show());

            new Thread(() -> {
                try {
                    if (invoice_ref_searchField.getText().isEmpty()) {
                        Platform.runLater(() -> {
                            loadingStage.close();
                            Utils.showAlertDialog(Alert.AlertType.INFORMATION, "Invalid request", "Invoice ref cannot be empty");
                        });
                        return;
                    }

                    CreateRefundRequest request = new CreateRefundRequest();
                    request.setInvoiceRef(invoice_ref_searchField.getText());
                    request.setReason(reasonTextField.getText());

                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.registerModule(new JavaTimeModule());

                    String jsonString = objectMapper.writeValueAsString(request);

                    String response = RestClient.post("/refund/", jsonString);
                    ApiResponseSingleData<Refund> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponseSingleData<Refund>>() {
                    });

                    if (apiResponse.getResponseHeader().getResponseCode().equals("00")) {
                        Platform.runLater(() -> {
                            loadingStage.close();
                            Utils.showAlertDialog(Alert.AlertType.INFORMATION, apiResponse.getResponseHeader().getResponseMessage(), apiResponse.getError());
                            primaryStage.close();

                        });
                    } else {
                        Platform.runLater(() -> {
                            loadingStage.close();
                            Utils.showAlertDialog(Alert.AlertType.ERROR, apiResponse.getResponseHeader().getResponseMessage(), apiResponse.getError());
                            primaryStage.close();
                        });

                    }

                } catch (Exception e) {
                    Platform.runLater(() -> {
                        loadingStage.close();
                        e.printStackTrace();
                        Utils.showGeneralErrorDialog();
                        primaryStage.close();
                    });
                }
            }).start();
        }
        else {
            confirmationAlert.close();
        }

    }
}
