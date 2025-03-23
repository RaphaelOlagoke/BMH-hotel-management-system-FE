package com.bmh.hotelmanagementsystem.BackendService.entities.Room;

import java.util.List;

public class CreateReservationRequest {

    private String guestName;
    private List<Integer> roomNumbers;
    private String phoneNumber;

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public List<Integer> getRoomNumbers() {
        return roomNumbers;
    }

    public void setRoomNumbers(List<Integer> roomNumbers) {
        this.roomNumbers = roomNumbers;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
