package com.bmh.hotelmanagementsystem.BackendService.entities.Room;

import com.bmh.hotelmanagementsystem.BackendService.enums.PaymentMethod;

import java.util.List;

public class CreateGuestLogRequest {
    private String guestName;
    private List<Integer> roomNumbers;
    private PaymentMethod paymentMethod;

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

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
