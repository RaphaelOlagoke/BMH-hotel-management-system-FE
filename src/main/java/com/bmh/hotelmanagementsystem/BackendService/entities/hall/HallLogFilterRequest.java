package com.bmh.hotelmanagementsystem.BackendService.entities.hall;

import com.bmh.hotelmanagementsystem.BackendService.enums.GuestLogStatus;
import com.bmh.hotelmanagementsystem.BackendService.enums.HallLogStatus;
import com.bmh.hotelmanagementsystem.BackendService.enums.HallType;
import com.bmh.hotelmanagementsystem.BackendService.enums.PaymentStatus;

import java.time.LocalDateTime;

public class HallLogFilterRequest {

    private HallLogStatus status;
    private PaymentStatus paymentStatus;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private HallType hallType;

    public HallLogStatus getStatus() {
        return status;
    }

    public void setStatus(HallLogStatus status) {
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

    public HallType getHallType() {
        return hallType;
    }

    public void setHallType(HallType hallType) {
        this.hallType = hallType;
    }
}
