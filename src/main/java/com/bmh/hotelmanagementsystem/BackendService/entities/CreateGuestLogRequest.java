package com.bmh.hotelmanagementsystem.BackendService.entities;

import com.bmh.hotelmanagementsystem.BackendService.enums.PaymentMethod;

public class CreateGuestLogRequest {
    private String guestName;
    private int roomNumber;
    private PaymentMethod paymentMethod;

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
