package com.bmh.hotelmanagementsystem.invoice;

import com.bmh.hotelmanagementsystem.BackendService.RestClient;
import com.bmh.hotelmanagementsystem.BackendService.entities.*;
import com.bmh.hotelmanagementsystem.BackendService.entities.Invoice.Invoice;
import com.bmh.hotelmanagementsystem.BackendService.entities.Invoice.Item;
import com.bmh.hotelmanagementsystem.BackendService.entities.Invoice.ResolveInvoiceRequest;
import com.bmh.hotelmanagementsystem.BackendService.entities.Room.GuestLog;
import com.bmh.hotelmanagementsystem.BackendService.entities.Room.GuestLogRoom;
import com.bmh.hotelmanagementsystem.BackendService.enums.PaymentMethod;
import com.bmh.hotelmanagementsystem.BackendService.enums.PaymentStatus;
import com.bmh.hotelmanagementsystem.Controller;
import com.bmh.hotelmanagementsystem.PDFDownloader;
import com.bmh.hotelmanagementsystem.PDFPrinter;
import com.bmh.hotelmanagementsystem.Utils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InvoiceController extends Controller {

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
    private String previousLocation;

    @FXML
    private ComboBox<String> payment_method;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");

    public void setGuestLog(GuestLog guestLog, String previousLocation) throws IOException {
        this.previousLocation = previousLocation;
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

        if(guestLog.getTotalAmountDue() <= guestLog.getCreditAmount()){
            formattedOutstandingPrice = formatter.format(0.0);
        } else if (guestLog.getTotalAmountDue() > guestLog.getCreditAmount()) {
            formattedOutstandingPrice = formatter.format(guestLog.getTotalAmountDue() - guestLog.getCreditAmount());
        }

        total_outstanding_payment.setText("Outstanding Payments:   " + formattedOutstandingPrice);


        for(Invoice invoice : guestLog.getInvoices()){
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/bmh/hotelmanagementsystem/invoice/payment_item.fxml"));
            VBox anchorPane = fxmlLoader.load();
            PaymentItemController controller = fxmlLoader.getController();

            Label service = (Label) anchorPane.lookup("#service");
            Label status = (Label) anchorPane.lookup("#status");
            Label date = (Label) anchorPane.lookup("#date");
            Label total = (Label) anchorPane.lookup("#total_price");
            Label subtotal = (Label) anchorPane.lookup("#subtotal_label");
            Label tax = (Label) anchorPane.lookup("#tax_label");
            Label discount = (Label) anchorPane.lookup("#discount_label");
            Button print = (Button) anchorPane.lookup("#print");
            Button download = (Button) anchorPane.lookup("#download");
            print.setOnAction(event -> print(invoice));
            download.setOnAction(event -> downloadInvoicePdf(invoice));
//            ScrollPane scroll_items = (ScrollPane) anchorPane.lookup("#scroll_items");
//            FlowPane items = (FlowPane) scroll_items.lookup("#items");
            FlowPane itemsFlowPane = controller.getItems();

            service.setText(invoice.getService().toJson());
            date.setText(invoice.getIssueDate().format(dateTimeFormatter));
            status.setText(invoice.getPaymentStatus().toJson());
            if (invoice.getPaymentStatus() == PaymentStatus.PAID) {
                status.setStyle("-fx-text-fill: green; -fx-background-color: #e0f7e0; -fx-font-weight: bold; -fx-padding: 5px 10px; -fx-background-radius: 5;");
            } else if (invoice.getPaymentStatus() == PaymentStatus.UNPAID) {
                status.setStyle("-fx-text-fill: red; -fx-background-color: #f7e0e0; -fx-font-weight: bold; -fx-padding: 5px 15px; -fx-background-radius: 5;");
            }

            for(Item item : invoice.getItems()){
                FXMLLoader itemLoader = new FXMLLoader(getClass().getResource("/com/bmh/hotelmanagementsystem/components/item.fxml"));
                AnchorPane itemAnchorPlane = itemLoader.load();
                Label name = (Label) itemAnchorPlane.lookup("#name");
                Label quantity = (Label) itemAnchorPlane.lookup("#quantity");
                Label price = (Label) itemAnchorPlane.lookup("#price");

                name.setText(item.getName());
                quantity.setText(String.valueOf(item.getQuantity()));
//                totalPrice += item.getPrice() * item.getQuantity();
//                DecimalFormat formatter = new DecimalFormat("#,###.00");

                String formattedPrice = formatter.format(item.getPrice());
                price.setText(formattedPrice);

                itemsFlowPane.getChildren().add(itemAnchorPlane);
            }
            subtotal.setText("Subtotal:  ₦" + formatter.format(invoice.getTotalAmount()));
            if(invoice.getDiscountCode() != null && !invoice.getDiscountCode().equals("")){
                total.setText("Amount Paid:  ₦" + formatter.format(invoice.getAmountPaid()) );
                discount.setText("Discount:  ₦" + invoice.getDiscountAmount());
            }
            else {
                total.setText("Amount Paid:  ₦" + formatter.format(invoice.getAmountPaid()));
                discount.setText("Discount:  ₦" + 0.0);
            }

//            if(invoice.getItems().isEmpty()){
//                totalPrice = invoice.getTotalAmount();
//            }
//            total.setText("Total:  " + String.valueOf(totalPrice));
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
        payment_Methods.add(PaymentMethod.NONE.toJson());

        payment_method.setItems(payment_Methods);
    }

    public void goBack(){
        try {
            Utils utils = new Utils();
            utils.switchScreen(previousLocation, primaryStage);
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

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirm Payment");
        confirmationAlert.setHeaderText("Are you sure you want to resolve the outstanding invoice?");
        confirmationAlert.setContentText("Please confirm your action.");

        Optional<ButtonType> result = confirmationAlert.showAndWait();

        List<String> invoiceRefs = new ArrayList<>();

        if (result.isPresent() && result.get() == ButtonType.OK) {

            try {
                for (Invoice invoice : guestLog.getInvoices()) {
                    if (invoice.getPaymentStatus().equals(PaymentStatus.UNPAID)) {
                        invoiceRefs.add(invoice.getRef());
                    }
                }

                if (invoiceRefs.isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Invalid request");
//                alert.setHeaderText("This is a header");
                    alert.setContentText("There are no outstanding payments");
                    alert.showAndWait();
                    return;
                }

                Stage loadingStage = new Stage();
                ProgressIndicator progressIndicator = new ProgressIndicator();
                StackPane loadingRoot = new StackPane();
                loadingRoot.getChildren().add(progressIndicator);
                Scene loadingScene = new Scene(loadingRoot, 200, 200);
                loadingStage.setScene(loadingScene);
                loadingStage.setTitle("Processing...");
                loadingStage.initOwner(primaryStage);
                loadingStage.initModality(Modality.APPLICATION_MODAL);
                loadingStage.show();

                new Thread(() -> {
                    try {
                        ResolveInvoiceRequest request = new ResolveInvoiceRequest();
                        request.setPaymentMethod(PaymentMethod.valueOf(payment_method.getSelectionModel().getSelectedItem()));
                        request.setInvoiceRefs(invoiceRefs);

                        ObjectMapper objectMapper = new ObjectMapper();
                        String jsonString = objectMapper.writeValueAsString(request);

                        // Make the API call to resolve the invoice
                        String response = RestClient.post("/invoice/resolve", jsonString);
                        ApiResponse<?> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponse<?>>() {});

                        Platform.runLater(() -> {
                            try {
                                loadingStage.close();

                                if (apiResponse.getResponseHeader().getResponseCode().equals("00")) {
                                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                    alert.setTitle("Payment Complete");
                                    alert.setContentText("Payment Successful");
                                    alert.showAndWait();

                                    goBack();
                                } else {
                                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                                    errorAlert.setTitle(apiResponse.getResponseHeader().getResponseMessage());
                                    errorAlert.setContentText(apiResponse.getError());
                                    errorAlert.showAndWait();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                loadingStage.close();

                                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                                errorAlert.setTitle("Error");
                                errorAlert.setContentText("Something went wrong. Please try again.");
                                errorAlert.showAndWait();
                            }
                        });

                    } catch (Exception e) {
                        Platform.runLater(() -> {
                            loadingStage.close();
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error");
                            alert.setContentText("Something went wrong. Please try again.");
                            alert.showAndWait();
                        });
                    }
                }).start();

            } catch (Exception e) {
                System.out.println(e);
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
//                alert.setHeaderText("This is a header");
                alert.setContentText("Something went wrong");
                alert.showAndWait();
            }
        }
        else {
            confirmationAlert.close();
        }
    }

    public void print(Invoice invoice){
        try {
            File pdfFile = PDFDownloader.downloadPDF("/invoice/download?ref=" + invoice.getRef());
            boolean hasPrinted = PDFPrinter.printPDF(pdfFile);
            if(hasPrinted) {
                System.out.println("Invoice with ref" +invoice.getRef()+" printed successfully!");
//                Utils.showAlertDialog(Alert.AlertType.INFORMATION, "Printer", "Invoice printed successfully!");
            }
            else {
                Utils.showAlertDialog(Alert.AlertType.ERROR, "Printer", "No printer found!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utils.showGeneralErrorDialog();
        }
    }

    public void downloadInvoicePdf(Invoice invoice){
        Utils.downloadPDF(invoice.getRef());
    }
}
