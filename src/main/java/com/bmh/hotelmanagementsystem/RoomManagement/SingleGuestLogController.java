package com.bmh.hotelmanagementsystem.RoomManagement;

import com.bmh.hotelmanagementsystem.BackendService.entities.Room.GuestLog;
import com.bmh.hotelmanagementsystem.BackendService.entities.Room.GuestLogRoom;
import com.bmh.hotelmanagementsystem.BackendService.enums.GuestLogStatus;
import com.bmh.hotelmanagementsystem.BackendService.enums.PaymentStatus;
import com.bmh.hotelmanagementsystem.Controller;
import com.bmh.hotelmanagementsystem.Utils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
    private Label status;

    @FXML
    private Button generate_invoice;

    @FXML
    private Button room_service;

    @FXML
    private Button back;

    public void setGuestLog(GuestLog guestLog, String previousLocation) throws IOException {
        this.guestLog = guestLog;

        StringBuilder room = new StringBuilder();
        StringBuilder types = new StringBuilder();

        int size = guestLog.getGuestLogRooms().size();
        int index = 0;

        for (GuestLogRoom guestLogRoom : guestLog.getGuestLogRooms()) {
            room.append(guestLogRoom.getRoom().getRoomNumber());
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

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        check_in_date.setText("Check-in date:   " + guestLog.getCheckInDate().format(formatter));
        if (guestLog.getCheckOutDate() != null) {
            check_out_date.setText("Check-out Date:   " + guestLog.getCheckOutDate().format(formatter));
        }
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
        String formattedAmountPaid = amountFormatter.format(guestLog.getAmountPaid());

        total_amount_due.setText("Outstanding Payments:   " + formattedOutstandingPrice);
        total_amount_paid.setText("Amount Paid:   " + formattedAmountPaid);

        if(this.guestLog.getStatus() == GuestLogStatus.COMPLETE){
            room_service.setDisable(true);
        }
    }

    public void initialize() throws IOException {
        back.setOnAction(event -> goBack());
        generate_invoice.setOnAction(event -> generateInvoice());
        room_service.setOnAction(event -> roomService());
    }

    public void generateInvoice(){
        try {
            Utils utils = new Utils();
            utils.switchScreenWithGuestLog("/com/bmh/hotelmanagementsystem/invoice/invoice-view.fxml", primaryStage,guestLog, "/com/bmh/hotelmanagementsystem/home-view.fxml");
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
            controller.setData(guestLog.getGuestLogRooms().get(0).getRoom(), "/com/bmh/hotelmanagementsystem/home-view.fxml");

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
            Utils utils = new Utils();
            utils.switchScreen("/com/bmh/hotelmanagementsystem/home-view.fxml", primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
