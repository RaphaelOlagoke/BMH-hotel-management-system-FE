package com.bmh.hotelmanagementsystem.dto.room;

import com.bmh.hotelmanagementsystem.BackendService.entities.Room.GuestLog;
import com.bmh.hotelmanagementsystem.BackendService.entities.Room.Reservation;
import com.bmh.hotelmanagementsystem.BackendService.enums.GuestLogStatus;
import com.bmh.hotelmanagementsystem.BackendService.enums.ReservationLogStatus;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ReservationRow {

    private final StringProperty guestName;
    private final StringProperty rooms;
    private final StringProperty phoneNumber;
    private final ObjectProperty<ReservationLogStatus> status;

    private final StringProperty date;
    private Reservation reservation;

    public ReservationRow(String guestName, String rooms, String phoneNumber, ReservationLogStatus status , String date,Reservation reservation) {
        this.guestName = new SimpleStringProperty(guestName);
        this.rooms = new SimpleStringProperty(rooms);
        this.phoneNumber = new SimpleStringProperty(phoneNumber);
        this.status = new SimpleObjectProperty<>(status);
        this.date = new SimpleStringProperty(date);
        this.reservation = reservation;
    }


    public String getGuestName() {
        return guestName.get();
    }

    public StringProperty guestNameProperty() {
        return guestName;
    }

    public String getRooms() {
        return rooms.get();
    }

    public StringProperty roomsProperty() {
        return rooms;
    }

    public String getPhoneNumber() {
        return phoneNumber.get();
    }

    public StringProperty phoneNumberProperty() {
        return phoneNumber;
    }

    public ReservationLogStatus getStatus() {
        return status.get();
    }

    public ObjectProperty<ReservationLogStatus> statusProperty() {
        return status;
    }

    public void setGuestName(String guestName) {
        this.guestName.set(guestName);
    }

    public void setRooms(String rooms) {
        this.rooms.set(rooms);
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber.set(phoneNumber);
    }

    public void setStatus(ReservationLogStatus status) {
        this.status.set(status);
    }

    public String getDate() {
        return date.get();
    }

    public StringProperty dateProperty() {
        return date;
    }

    public void setDate(String date) {
        this.date.set(date);
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public Reservation getReservation() {
        return reservation;
    }
}
