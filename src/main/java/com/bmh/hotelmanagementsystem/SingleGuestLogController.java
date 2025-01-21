package com.bmh.hotelmanagementsystem;

import com.bmh.hotelmanagementsystem.BackendService.entities.GuestLog;
import com.bmh.hotelmanagementsystem.BackendService.entities.GuestLogRoom;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;

public class SingleGuestLogController extends Controller{

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
        payment_status.setText(guestLog.getStatus().toJson());
        status.setText("Status:   " + guestLog.getStatus().toJson());
        DecimalFormat amountFormatter = new DecimalFormat("#,###.00");

        String formattedOutstandingPrice = amountFormatter.format(guestLog.getTotalAmountDue());
        String formattedAmountPaid = amountFormatter.format(guestLog.getAmountPaid());

        total_amount_due.setText("Outstanding Payments:   " + formattedOutstandingPrice);
        total_amount_paid.setText("Amount Paid:   " + formattedAmountPaid);

    }

    public void initialize() throws IOException {
        back.setOnAction(event -> goBack());
        generate_invoice.setOnAction(event -> generateInvoice());
    }

    public void generateInvoice(){
        try {
            Utils utils = new Utils();
            utils.switchScreenWithGuestLog("/com/bmh/hotelmanagementsystem/invoice-view.fxml", primaryStage,guestLog, "guest-log-view.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void goBack(){
        try {
            Utils utils = new Utils();
            utils.switchScreen("/com/bmh/hotelmanagementsystem/guest-log-view.fxml", primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
