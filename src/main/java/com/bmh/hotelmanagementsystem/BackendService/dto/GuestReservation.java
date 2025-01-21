package com.bmh.hotelmanagementsystem.BackendService.dto;

import com.bmh.hotelmanagementsystem.BackendService.entities.GuestLog;
import com.bmh.hotelmanagementsystem.BackendService.enums.GuestLogStatus;
import com.bmh.hotelmanagementsystem.BackendService.enums.PaymentStatus;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDateTime;

import javafx.beans.property.*;

public class GuestReservation {

    private final StringProperty guestName;
    private final StringProperty rooms;
    private final StringProperty checkInDate;
    private final StringProperty checkOutDate;
    private final ObjectProperty<PaymentStatus> paymentStatus;
    private final ObjectProperty<GuestLogStatus> status;

    private GuestLog guestLog;

    // Constructor
    public GuestReservation(String guestName, String rooms, String checkInDate,
                            String checkOutDate, PaymentStatus paymentStatus,
                            GuestLogStatus status, GuestLog guestLog) {
        this.guestName = new SimpleStringProperty(guestName);
        this.rooms = new SimpleStringProperty(rooms);
        this.checkInDate = new SimpleStringProperty(checkInDate);
        this.checkOutDate = new SimpleStringProperty(checkOutDate);
        this.paymentStatus = new SimpleObjectProperty<>(paymentStatus);
        this.status = new SimpleObjectProperty<>(status);
        this.guestLog = guestLog;
    }

    // Getters and Setters (for convenience)
    public String getGuestName() {
        return guestName.get();
    }

    public void setGuestName(String guestName) {
        this.guestName.set(guestName);
    }

    public StringProperty guestNameProperty() {
        return guestName;
    }

    public String getRooms() {
        return rooms.get();
    }

    public void setRooms(String rooms) {
        this.rooms.set(rooms);
    }

    public StringProperty roomsProperty() {
        return rooms;
    }

    public String getCheckInDate() {
        return checkInDate.get();
    }

    public void setCheckInDate(String checkInDate) {
        this.checkInDate.set(checkInDate);
    }

    public StringProperty checkInDateProperty() {
        return checkInDate;
    }

    public String getCheckOutDate() {
        return checkOutDate.get();
    }

    public void setCheckOutDate(String checkOutDate) {
        this.checkOutDate.set(checkOutDate);
    }

    public StringProperty checkOutDateProperty() {
        return checkOutDate;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus.get();
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus.set(paymentStatus);
    }

    public ObjectProperty<PaymentStatus> paymentStatusProperty() {
        return paymentStatus;
    }

    public GuestLogStatus getStatus() {
        return status.get();
    }

    public void setStatus(GuestLogStatus status) {
        this.status.set(status);
    }

    public ObjectProperty<GuestLogStatus> statusProperty() {
        return status;
    }

    public GuestLog getGuestLog() {
        return guestLog;
    }

    public void setGuestLog(GuestLog guestLog) {
        this.guestLog = guestLog;
    }
}

