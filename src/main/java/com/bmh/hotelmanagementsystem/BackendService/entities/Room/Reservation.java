package com.bmh.hotelmanagementsystem.BackendService.entities.Room;

import com.bmh.hotelmanagementsystem.BackendService.enums.ReservationLogStatus;
import javafx.beans.property.ObjectProperty;

import java.time.LocalDateTime;
import java.util.List;

public class Reservation {

    private String ref;
    private String guestName;
    private String guestPhoneNumber;
    private List<Room> roomNumbers;

    private ReservationLogStatus status;

    private LocalDateTime createdDateTime;

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public String getGuestPhoneNumber() {
        return guestPhoneNumber;
    }

    public void setGuestPhoneNumber(String guestPhoneNumber) {
        this.guestPhoneNumber = guestPhoneNumber;
    }

    public List<Room> getRoomNumbers() {
        return roomNumbers;
    }

    public void setRoomNumbers(List<Room> roomNumbers) {
        this.roomNumbers = roomNumbers;
    }

    public ReservationLogStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationLogStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(LocalDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }
}
