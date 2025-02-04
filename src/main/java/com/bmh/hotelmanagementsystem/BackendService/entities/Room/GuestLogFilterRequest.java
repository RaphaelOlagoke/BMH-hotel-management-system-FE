package com.bmh.hotelmanagementsystem.BackendService.entities.Room;

import com.bmh.hotelmanagementsystem.BackendService.enums.GuestLogStatus;
import com.bmh.hotelmanagementsystem.BackendService.enums.PaymentStatus;

import java.time.LocalDateTime;

public class GuestLogFilterRequest {

    private GuestLogStatus status;
    private PaymentStatus paymentStatus;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private int roomNumber;

    public GuestLogStatus getStatus() {
        return status;
    }

    public void setStatus(GuestLogStatus status) {
        this.status = status;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }
}
