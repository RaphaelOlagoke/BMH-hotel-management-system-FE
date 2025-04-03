package com.bmh.hotelmanagementsystem.dto.hall;

import com.bmh.hotelmanagementsystem.BackendService.entities.hall.HallLog;
import com.bmh.hotelmanagementsystem.BackendService.enums.GuestLogStatus;
import com.bmh.hotelmanagementsystem.BackendService.enums.HallLogStatus;
import com.bmh.hotelmanagementsystem.BackendService.enums.PaymentStatus;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class HallReservation {

    private final StringProperty guestName;
    private final StringProperty hall;
    private final StringProperty startDate;
    private final StringProperty endDate;
    private final ObjectProperty<PaymentStatus> paymentStatus;
    private final ObjectProperty<HallLogStatus> status;

    private HallLog hallLog;


    public HallReservation(String guestName, String hall, String startDate,
                           String endDate, PaymentStatus paymentStatus,
                           HallLogStatus status, HallLog hallLog) {
        this.guestName = new SimpleStringProperty(guestName);
        this.hall = new SimpleStringProperty(hall);
        this.startDate = new SimpleStringProperty(startDate);
        this.endDate = new SimpleStringProperty(endDate);
        this.paymentStatus =  new SimpleObjectProperty<>(paymentStatus);
        this.status =  new SimpleObjectProperty<>(status);
        this.hallLog = hallLog;
    }


    public String getGuestName() {
        return guestName.get();
    }

    public StringProperty guestNameProperty() {
        return guestName;
    }

    public String getHall() {
        return hall.get();
    }

    public StringProperty hallProperty() {
        return hall;
    }

    public String getStartDate() {
        return startDate.get();
    }

    public StringProperty startDateProperty() {
        return startDate;
    }

    public String getEndDate() {
        return endDate.get();
    }

    public StringProperty endDateProperty() {
        return endDate;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus.get();
    }

    public ObjectProperty<PaymentStatus> paymentStatusProperty() {
        return paymentStatus;
    }

    public HallLogStatus getStatus() {
        return status.get();
    }

    public ObjectProperty<HallLogStatus> statusProperty() {
        return status;
    }

    public HallLog getHallLog() {
        return hallLog;
    }
}
