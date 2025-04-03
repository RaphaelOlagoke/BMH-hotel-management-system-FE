package com.bmh.hotelmanagementsystem.BackendService.entities.hall;

import com.bmh.hotelmanagementsystem.BackendService.entities.Invoice.Invoice;
import com.bmh.hotelmanagementsystem.BackendService.enums.GuestLogStatus;
import com.bmh.hotelmanagementsystem.BackendService.enums.HallLogStatus;
import com.bmh.hotelmanagementsystem.BackendService.enums.ID_TYPE;
import com.bmh.hotelmanagementsystem.BackendService.enums.PaymentStatus;

import java.time.LocalDateTime;
import java.util.List;

public class HallLog {

    private String ref;
    private String guestName;
    private String hall;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private PaymentStatus paymentStatus;
    private HallLogStatus status;
    private Invoice invoices;

    private String description;
    private ID_TYPE idType;
    private String idRef;
    private String nextOfKinName;
    private String nextOfKinNumber;
    private String phoneNumber;

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

    public String getHall() {
        return hall;
    }

    public void setHall(String hall) {
        this.hall = hall;
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

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public HallLogStatus getStatus() {
        return status;
    }

    public void setStatus(HallLogStatus status) {
        this.status = status;
    }

    public Invoice getInvoices() {
        return invoices;
    }

    public void setInvoices(Invoice invoices) {
        this.invoices = invoices;
    }

    public ID_TYPE getIdType() {
        return idType;
    }

    public void setIdType(ID_TYPE idType) {
        this.idType = idType;
    }

    public String getIdRef() {
        return idRef;
    }

    public void setIdRef(String idRef) {
        this.idRef = idRef;
    }

    public String getNextOfKinName() {
        return nextOfKinName;
    }

    public void setNextOfKinName(String nextOfKinName) {
        this.nextOfKinName = nextOfKinName;
    }

    public String getNextOfKinNumber() {
        return nextOfKinNumber;
    }

    public void setNextOfKinNumber(String nextOfKinNumber) {
        this.nextOfKinNumber = nextOfKinNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
