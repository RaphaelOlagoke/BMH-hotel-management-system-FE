package com.bmh.hotelmanagementsystem.hall;

import com.bmh.hotelmanagementsystem.*;
import com.bmh.hotelmanagementsystem.BackendService.RestClient;
import com.bmh.hotelmanagementsystem.BackendService.entities.ApiResponseSingleData;
import com.bmh.hotelmanagementsystem.BackendService.entities.HouseKeeping.CleaningLog;
import com.bmh.hotelmanagementsystem.BackendService.entities.HouseKeeping.UpdateCleaningLogRequest;
import com.bmh.hotelmanagementsystem.BackendService.entities.Invoice.Invoice;
import com.bmh.hotelmanagementsystem.BackendService.entities.Room.GuestLog;
import com.bmh.hotelmanagementsystem.BackendService.entities.Room.GuestLogRoom;
import com.bmh.hotelmanagementsystem.BackendService.entities.hall.HallLog;
import com.bmh.hotelmanagementsystem.BackendService.entities.hall.UpdateHallLogStatusRequest;
import com.bmh.hotelmanagementsystem.BackendService.enums.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class SingleHallLogController extends Controller {

    private Stage primaryStage;
    private HallLog data;
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    private Label guest_name;

    @FXML
    private Label id_ref;

    @FXML
    private Label id_type;

    @FXML
    private Label phone_number;

    @FXML
    private Label next_of_kin_name;

    @FXML
    private Label next_of_kin_number;


    @FXML
    private Label start_date;
    @FXML
    private Label end_date;
    @FXML
    private Label hallType;
    @FXML
    private Label payment_method;
    @FXML
    private Label payment_status;
    @FXML
    private Label total_amount_paid;
    @FXML
    private Label status;

    @FXML
    private Label description;

    @FXML
    private Button print_receipt;

    @FXML
    private MenuItem mark_as_complete;

    @FXML
    private MenuItem cancel;

    @FXML
    private MenuItem mark_as_active;

    @FXML
    private Button back;

    public void setData(Object data, String previousLocation) {
        this.data = (HallLog) data;

        guest_name.setText("Guest Name:   " + this.data.getGuestName());
        id_ref.setText("ID Ref:   " + this.data.getIdRef());
        id_type.setText("ID Type:   " + this.data.getIdType());
        phone_number.setText("Phone Number:   " + this.data.getPhoneNumber());
        next_of_kin_name.setText("Next of kin:   " + this.data.getNextOfKinName());
        next_of_kin_number.setText("Next of kin Number:   " + this.data.getNextOfKinNumber());

        hallType.setText("Hall Type:   " + this.data.getHall());
        description.setText("Event Description:   " + this.data.getDescription());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        start_date.setText("Start date:   " + this.data.getStartDate().format(formatter));
        if (this.data.getEndDate() != null) {
            end_date.setText("End Date:   " + this.data.getEndDate().format(formatter));
        }
        payment_method.setText("Payment Method  " + this.data.getInvoices().getPaymentMethod().toJson());
        payment_status.setText(this.data.getPaymentStatus().toJson());
        if (this.data.getPaymentStatus() == PaymentStatus.PAID) {
            payment_status.setStyle("-fx-text-fill: green; -fx-background-color: #e0f7e0; -fx-font-weight: bold; -fx-padding: 5px 10px; -fx-background-radius: 5;");
        } else if (this.data.getPaymentStatus() == PaymentStatus.UNPAID) {
            payment_status.setStyle("-fx-text-fill: red; -fx-background-color: #f7e0e0; -fx-font-weight: bold; -fx-padding: 5px 15px; -fx-background-radius: 5;");
        }
        status.setText(this.data.getStatus().toJson());
        if (this.data.getStatus() == HallLogStatus.COMPLETE) {
            status.setStyle("-fx-text-fill: green; -fx-background-color: #e0f7e0; -fx-font-weight: bold; -fx-padding: 5px 10px; -fx-background-radius: 5;");
        } else if (this.data.getStatus() == HallLogStatus.ACTIVE) {
            status.setStyle("-fx-text-fill: blue; -fx-background-color: #e0e0f7; -fx-font-weight: bold; -fx-padding: 5px; -fx-background-radius: 5;");
        }
        else if (this.data.getStatus() == HallLogStatus.UPCOMING) {
            status.setStyle("-fx-text-fill: #8B8000; -fx-background-color: #fff9c4; -fx-font-weight: bold; -fx-padding: 5px 15px; -fx-background-radius: 5;");
        }
        else if (this.data.getStatus() == HallLogStatus.CANCELED) {
            status.setStyle("-fx-text-fill: red; -fx-background-color: #f7e0e0; -fx-font-weight: bold; -fx-padding: 5px; -fx-background-radius: 5;");
        }
        DecimalFormat amountFormatter = new DecimalFormat("#,###.00");

        Double amountPaid = this.data.getInvoices().getAmountPaid();

//        for(Invoice invoice : this.data.getInvoices()){
//            amountPaid += invoice.getAmountPaid();
//        }

        String formattedAmountPaid = amountFormatter.format(amountPaid);

        total_amount_paid.setText("Amount Paid:   ₦" + formattedAmountPaid);

        if(this.data.getStatus() == HallLogStatus.COMPLETE || this.data.getStatus() == HallLogStatus.CANCELED){
            mark_as_active.setDisable(true);
            mark_as_complete.setDisable(true);
            cancel.setDisable(true);
//            add_room.setDisable(true);
        }
    }

    public void initialize() throws IOException {
        back.setOnAction(event -> goBack());
//        generate_invoice.setOnAction(event -> generateInvoice());
//        room_service.setOnAction(event -> roomService());
        mark_as_complete.setOnAction(event -> changeStatus(HallLogStatus.COMPLETE));
        mark_as_active.setOnAction(event -> changeStatus(HallLogStatus.ACTIVE));
        cancel.setOnAction(event -> changeStatus(HallLogStatus.CANCELED));
        print_receipt.setOnAction(event -> print(this.data.getInvoices()));
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


    public void changeStatus(HallLogStatus hallLogStatus){
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirm action");
        if(hallLogStatus == HallLogStatus.ACTIVE) {
            confirmationAlert.setHeaderText("Are you sure you want to mark as active?");
        }
        if(hallLogStatus == HallLogStatus.COMPLETE) {
            confirmationAlert.setHeaderText("Are you sure you want to mark as complete?");
        }
        if(hallLogStatus == HallLogStatus.CANCELED) {
            confirmationAlert.setHeaderText("Are you sure you want to cancel hall reservation?");
        }
        confirmationAlert.setContentText("Please confirm your action.");

        Optional<ButtonType> result = confirmationAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            Stage loadingStage = Utils.showLoadingScreen(primaryStage);
            Platform.runLater(() -> loadingStage.show());

            new Thread(() -> {
                try {

                    UpdateHallLogStatusRequest request = new UpdateHallLogStatusRequest();
                    request.setRef(data.getRef());
                    request.setStatus(hallLogStatus);

                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.registerModule(new JavaTimeModule());

                    String jsonString = objectMapper.writeValueAsString(request);

                    String response = RestClient.post("/hallLog/update", jsonString);
                    ApiResponseSingleData<HallLog> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponseSingleData<HallLog>>() {});

                    if (apiResponse.getResponseHeader().getResponseCode().equals("00")) {
                        Platform.runLater(() -> {
                            loadingStage.close();
                            Utils.showAlertDialog(Alert.AlertType.INFORMATION,apiResponse.getResponseHeader().getResponseMessage(),apiResponse.getError() );
                            Utils utils = new Utils();
                            try {
                                utils.switchScreen("/com/bmh/hotelmanagementsystem/hall/hall-logs-view.fxml", primaryStage);
                            } catch (IOException e) {
                                Utils.showGeneralErrorDialog();
                            }

                        });
                    } else {
                        Platform.runLater(() -> {
                            loadingStage.close();
                            Utils.showAlertDialog(Alert.AlertType.ERROR,apiResponse.getResponseHeader().getResponseMessage(),apiResponse.getError() );
                            Utils utils = new Utils();
                            try {
                                utils.switchScreen("/com/bmh/hotelmanagementsystem/hall/hall-logs-view.fxml", primaryStage);
                            } catch (IOException e) {
                                Utils.showGeneralErrorDialog();
                            }
                        });

                    }

                } catch (Exception e) {
                    Platform.runLater(() -> {
                        loadingStage.close();
                        e.printStackTrace();
                        Utils.showGeneralErrorDialog();
                        Utils utils = new Utils();
                        try {
                            utils.switchScreen("/com/bmh/hotelmanagementsystem/hall/hall-logs-view.fxml", primaryStage);
                        } catch (IOException ex) {
                            Utils.showGeneralErrorDialog();
                        }
                    });
                }
            }).start();
        } else {
            confirmationAlert.close();
        }
    }


    public void goBack(){
        try {
            String[] credentials = TokenStorage.loadCredentials();
            String department = "";
            if (credentials != null) {
                department = credentials[2];
            }

            if(LoginDepartment.valueOf(department) == LoginDepartment.SUPER_ADMIN){
                Utils utils = new Utils();
                utils.switchScreen("/com/bmh/hotelmanagementsystem/room/admin-guest-logs-view.fxml", primaryStage);
            }
            else if(LoginDepartment.valueOf(department) == LoginDepartment.ACCOUNTS || LoginDepartment.valueOf(department) == LoginDepartment.MANAGER){
                Utils utils = new Utils();
                utils.switchScreen("/com/bmh/hotelmanagementsystem/room/general-admin-guest-logs-view.fxml", primaryStage);
            }
            else {
                Utils utils = new Utils();
                utils.switchScreen("/com/bmh/hotelmanagementsystem/home-view.fxml", primaryStage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

