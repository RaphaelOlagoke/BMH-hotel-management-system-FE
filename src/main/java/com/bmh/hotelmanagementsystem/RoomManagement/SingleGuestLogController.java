package com.bmh.hotelmanagementsystem.RoomManagement;

import com.bmh.hotelmanagementsystem.BMHApplication;
import com.bmh.hotelmanagementsystem.BackendService.entities.Room.GuestLog;
import com.bmh.hotelmanagementsystem.BackendService.entities.Room.GuestLogRoom;
import com.bmh.hotelmanagementsystem.BackendService.enums.GuestLogStatus;
import com.bmh.hotelmanagementsystem.BackendService.enums.LoginDepartment;
import com.bmh.hotelmanagementsystem.BackendService.enums.PaymentStatus;
import com.bmh.hotelmanagementsystem.Controller;
import com.bmh.hotelmanagementsystem.HouseKeeping.UpdateCleaningLogController;
import com.bmh.hotelmanagementsystem.TokenStorage;
import com.bmh.hotelmanagementsystem.Utils;
import com.bmh.hotelmanagementsystem.dto.HouseKeeping.CleaningRow;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;

public class SingleGuestLogController extends Controller {

    private Stage primaryStage;
    private GuestLog guestLog;
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    private Label guest_name;
    @FXML
    private Label check_in_date;
    @FXML
    private Label check_out_date;

    @FXML
    private Label expected_check_out_date;
    @FXML
    private Label rooms;
    @FXML
    private Label room_types;
    @FXML
    private Label payment_status;
    @FXML
    private Label total_amount_paid;
    @FXML
    private Label total_amount_due;

    @FXML
    private Label credit_amount;

    @FXML
    private Label amount_due;
    @FXML
    private Label status;

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
    private Button generate_invoice;

    @FXML
    private Button room_service;

    @FXML
    private MenuItem extend;

    @FXML
    private MenuItem change_room;

    @FXML
    private MenuItem add_room;

    @FXML
    private Button back;

    public void setGuestLog(GuestLog guestLog, String previousLocation) throws IOException {
        this.guestLog = guestLog;

        StringBuilder room = new StringBuilder();
        StringBuilder types = new StringBuilder();

        int size = guestLog.getGuestLogRooms().size();
        int index = 0;

        for (GuestLogRoom guestLogRoom : guestLog.getGuestLogRooms()) {
            room.append(guestLogRoom.getRoom().getRoomNumber() + "(" + guestLogRoom.getGuestLogStatus() +")");
            types.append(guestLogRoom.getRoom().getRoomType());

            if (index < size - 1) {
                room.append(", ");
                types.append(", ");
            }
            index++;
        }

        guest_name.setText("Guest Name:   " + guestLog.getGuestName());
        rooms.setText("Room(s):   " + room);
        room_types.setText("Room type(s):   " + types);

        id_ref.setText("ID Ref:   " + guestLog.getIdRef());
        id_type.setText("ID Type:   " + guestLog.getIdType());
        phone_number.setText("Phone Number:   " + guestLog.getPhoneNumber());
        next_of_kin_name.setText("Next of kin:   " + guestLog.getNextOfKinName());
        next_of_kin_number.setText("Next of kin Number:   " + guestLog.getNextOfKinNumber());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        check_in_date.setText("Check-in date:   " + guestLog.getCheckInDate().format(formatter));
        if (guestLog.getCheckOutDate() != null) {
            check_out_date.setText("Check-out Date:   " + guestLog.getCheckOutDate().format(formatter));
        }
        expected_check_out_date.setText("Expected Check-out date:   " + guestLog.getExpectedCheckOutDate().format(formatter));
        payment_status.setText(guestLog.getPaymentStatus().toJson());
        if (guestLog.getPaymentStatus() == PaymentStatus.PAID) {
            payment_status.setStyle("-fx-text-fill: green; -fx-background-color: #e0f7e0; -fx-font-weight: bold; -fx-padding: 5px 10px; -fx-background-radius: 5;");
        } else if (guestLog.getPaymentStatus() == PaymentStatus.UNPAID) {
            payment_status.setStyle("-fx-text-fill: red; -fx-background-color: #f7e0e0; -fx-font-weight: bold; -fx-padding: 5px 15px; -fx-background-radius: 5;");
        }
        status.setText(guestLog.getStatus().toJson());
        if (guestLog.getStatus() == GuestLogStatus.COMPLETE) {
            status.setStyle("-fx-text-fill: green; -fx-background-color: #e0f7e0; -fx-font-weight: bold; -fx-padding: 5px 10px; -fx-background-radius: 5;");
        } else if (guestLog.getStatus() == GuestLogStatus.ACTIVE) {
            status.setStyle("-fx-text-fill: blue; -fx-background-color: #e0e0f7; -fx-font-weight: bold; -fx-padding: 5px; -fx-background-radius: 5;");
        }
        DecimalFormat amountFormatter = new DecimalFormat("#,###.00");

        String formattedOutstandingPrice = amountFormatter.format(guestLog.getTotalAmountDue());

        if(guestLog.getTotalAmountDue() <= guestLog.getCreditAmount()){
            formattedOutstandingPrice = amountFormatter.format(0.0);
        } else if (guestLog.getTotalAmountDue() > guestLog.getCreditAmount()) {
            formattedOutstandingPrice = amountFormatter.format(guestLog.getTotalAmountDue() - guestLog.getCreditAmount());
        }
//        String formattedOutstandingPrice = amountFormatter.format(Math.abs(guestLog.getTotalAmountDue() - guestLog.getCreditAmount()));
        String formattedAmountDue = amountFormatter.format(guestLog.getTotalAmountDue());
        String formattedCreditAmount = amountFormatter.format(guestLog.getCreditAmount());
        String formattedAmountPaid = amountFormatter.format(guestLog.getAmountPaid());

        total_amount_due.setText("Outstanding Payments:   ₦" + formattedOutstandingPrice);
        amount_due.setText("Amount Due:   ₦" + formattedAmountDue);
        credit_amount.setText("Credit Amount:   ₦" + formattedCreditAmount);
        total_amount_paid.setText("Amount Paid:   ₦" + formattedAmountPaid);

        if(this.guestLog.getStatus() == GuestLogStatus.COMPLETE){
            room_service.setDisable(true);
            change_room.setDisable(true);
            extend.setDisable(true);
            add_room.setDisable(true);
        }
    }

    public void initialize() throws IOException {
        back.setOnAction(event -> goBack());
        generate_invoice.setOnAction(event -> generateInvoice());
        room_service.setOnAction(event -> roomService());
        change_room.setOnAction(event -> changeRoom());
        extend.setOnAction(event -> extend());
        add_room.setOnAction(event -> addRoom());
    }

    public void generateInvoice(){
        try {
            String[] credentials = TokenStorage.loadCredentials();
            String department = "";
            if (credentials != null) {
                department = credentials[2];
            }
            if(LoginDepartment.valueOf(department) == LoginDepartment.SUPER_ADMIN){
                Utils utils = new Utils();
                utils.switchScreenWithGuestLog("/com/bmh/hotelmanagementsystem/invoice/invoice-view.fxml", primaryStage,guestLog, "/com/bmh/hotelmanagementsystem/room/admin-guest-logs-view.fxml");
            }
            else if(LoginDepartment.valueOf(department) == LoginDepartment.ADMIN){
                Utils utils = new Utils();
                utils.switchScreenWithGuestLog("/com/bmh/hotelmanagementsystem/invoice/invoice-view.fxml", primaryStage,guestLog, "/com/bmh/hotelmanagementsystem/room/general-admin-guest-logs-view.fxml");
            }
            else {
                Utils utils = new Utils();
                utils.switchScreenWithGuestLog("/com/bmh/hotelmanagementsystem/invoice/invoice-view.fxml", primaryStage,guestLog, "/com/bmh/hotelmanagementsystem/home-view.fxml");
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void roomService(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bmh/hotelmanagementsystem/room/room_service.fxml"));
            Region form = loader.load();

            Stage formStage = new Stage();
            formStage.initModality(Modality.APPLICATION_MODAL);
            formStage.setTitle("Fill out the Form");

            Controller controller = loader.getController();
            controller.setPrimaryStage(formStage);

            String[] credentials = TokenStorage.loadCredentials();
            String department = "";
            if (credentials != null) {
                department = credentials[2];
            }
            if(LoginDepartment.valueOf(department) == LoginDepartment.SUPER_ADMIN){
                controller.setData(guestLog.getGuestLogRooms().get(0).getRoom(), "/com/bmh/hotelmanagementsystem/room/admin-guest-logs-view.fxml");
            }
            else if(LoginDepartment.valueOf(department) == LoginDepartment.ADMIN){
                controller.setData(guestLog.getGuestLogRooms().get(0).getRoom(), "/com/bmh/hotelmanagementsystem/room/general-admin-guest-logs-view.fxml");
            }
            else {
                controller.setData(guestLog.getGuestLogRooms().get(0).getRoom(), "/com/bmh/hotelmanagementsystem/home-view.fxml");
            }

//            controller.setData(guestLog.getGuestLogRooms().get(0).getRoom(), "/com/bmh/hotelmanagementsystem/home-view.fxml");

            Scene formScene = new Scene(form);
            formStage.setScene(formScene);
            formStage.showAndWait();

            goBack();

        } catch (Exception e) {
            e.printStackTrace();
            Utils.showGeneralErrorDialog();
        }
    }

    public void changeRoom(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bmh/hotelmanagementsystem/room/change_room.fxml"));
            Region form = loader.load();

            Stage formStage = new Stage();
            formStage.initModality(Modality.APPLICATION_MODAL);
            formStage.setTitle("Fill out the Form");

            Controller controller = loader.getController();
            controller.setPrimaryStage(formStage);

            String[] credentials = TokenStorage.loadCredentials();
            String department = "";
            if (credentials != null) {
                department = credentials[2];
            }
            if(LoginDepartment.valueOf(department) == LoginDepartment.SUPER_ADMIN){
                controller.setData(this.guestLog, "/com/bmh/hotelmanagementsystem/room/admin-guest-logs-view.fxml");
            }
            else if(LoginDepartment.valueOf(department) == LoginDepartment.ADMIN){
                controller.setData(this.guestLog, "/com/bmh/hotelmanagementsystem/room/general-admin-guest-logs-view.fxml");
            }
            else {
                controller.setData(this.guestLog, "/com/bmh/hotelmanagementsystem/home-view.fxml");
            }

//            controller.setData(this.guestLog, "/com/bmh/hotelmanagementsystem/home-view.fxml");

            Scene formScene = new Scene(form);
            formStage.setScene(formScene);
            formStage.showAndWait();

            goBack();

        } catch (Exception e) {
            e.printStackTrace();
            Utils.showGeneralErrorDialog();
        }

    }

    public void extend(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bmh/hotelmanagementsystem/room/extend_guest_log.fxml"));
            Region form = loader.load();

            Stage formStage = new Stage();
            formStage.initModality(Modality.APPLICATION_MODAL);
            formStage.setTitle("Fill out the Form");

            Controller controller = loader.getController();
            controller.setPrimaryStage(formStage);

            String[] credentials = TokenStorage.loadCredentials();
            String department = "";
            if (credentials != null) {
                department = credentials[2];
            }
            if(LoginDepartment.valueOf(department) == LoginDepartment.SUPER_ADMIN){
                controller.setData(guestLog, "/com/bmh/hotelmanagementsystem/room/admin-guest-logs-view.fxml");
            }
            else if(LoginDepartment.valueOf(department) == LoginDepartment.ADMIN){
                controller.setData(guestLog, "/com/bmh/hotelmanagementsystem/room/general-admin-guest-logs-view.fxml");
            }
            else {
                controller.setData(guestLog, "/com/bmh/hotelmanagementsystem/home-view.fxml");
            }
//            controller.setData(guestLog, "/com/bmh/hotelmanagementsystem/home-view.fxml");

            Scene formScene = new Scene(form);
            formStage.setScene(formScene);
            formStage.showAndWait();

            goBack();

        } catch (Exception e) {
            e.printStackTrace();
            Utils.showGeneralErrorDialog();
        }
    }


    public void addRoom(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bmh/hotelmanagementsystem/room/add_room_to_guest_log.fxml"));
            Region form = loader.load();

            Stage formStage = new Stage();
            formStage.initModality(Modality.APPLICATION_MODAL);
            formStage.setTitle("Fill out the Form");

            Controller controller = loader.getController();
            controller.setPrimaryStage(formStage);

            String[] credentials = TokenStorage.loadCredentials();
            String department = "";
            if (credentials != null) {
                department = credentials[2];
            }
            if(LoginDepartment.valueOf(department) == LoginDepartment.SUPER_ADMIN){
                controller.setData(this.guestLog, "/com/bmh/hotelmanagementsystem/room/admin-guest-logs-view.fxml");
            }
            else if(LoginDepartment.valueOf(department) == LoginDepartment.ADMIN){
                controller.setData(this.guestLog, "/com/bmh/hotelmanagementsystem/room/general-admin-guest-logs-view.fxml");
            }
            else {
                controller.setData(this.guestLog, "/com/bmh/hotelmanagementsystem/home-view.fxml");
            }

//            controller.setData(this.guestLog, "/com/bmh/hotelmanagementsystem/home-view.fxml");

            Scene formScene = new Scene(form);
            formStage.setScene(formScene);
            formStage.showAndWait();

            goBack();

        } catch (Exception e) {
            e.printStackTrace();
            Utils.showGeneralErrorDialog();
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
            else if(LoginDepartment.valueOf(department) == LoginDepartment.ADMIN){
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

