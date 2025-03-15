package com.bmh.hotelmanagementsystem.BackendService.entities.Room;

import com.bmh.hotelmanagementsystem.BackendService.entities.Invoice.Invoice;
import com.bmh.hotelmanagementsystem.BackendService.enums.GuestLogStatus;
import com.bmh.hotelmanagementsystem.BackendService.enums.ID_TYPE;
import com.bmh.hotelmanagementsystem.BackendService.enums.PaymentStatus;

import java.time.LocalDateTime;
import java.util.List;

public class GuestLog {
    private String guestName;
    private List<GuestLogRoom> guestLogRooms;
    private LocalDateTime checkInDate;
    private LocalDateTime checkOutDate;
    private LocalDateTime expectedCheckOutDate;
    private PaymentStatus paymentStatus;
    private Double totalAmountDue;
    private Double amountPaid;
    private String notes;
    private GuestLogStatus status;
    private List<Invoice> invoices;


    private ID_TYPE id_type;
    private String idRef;
    private String nextOfKinName;
    private String nextOfKinNumber;
    private String phoneNumber;

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public List<GuestLogRoom> getGuestLogRooms() {
        return guestLogRooms;
    }

    public void setGuestLogRooms(List<GuestLogRoom> guestLogRooms) {
        this.guestLogRooms = guestLogRooms;
    }

    public LocalDateTime getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(LocalDateTime checkInDate) {
        this.checkInDate = checkInDate;
    }

    public LocalDateTime getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(LocalDateTime checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public LocalDateTime getExpectedCheckOutDate() {
        return expectedCheckOutDate;
    }

    public void setExpectedCheckOutDate(LocalDateTime expectedCheckOutDate) {
        this.expectedCheckOutDate = expectedCheckOutDate;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public Double getTotalAmountDue() {
        return totalAmountDue;
    }

    public void setTotalAmountDue(Double totalAmountDue) {
        this.totalAmountDue = totalAmountDue;
    }

    public Double getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(Double amountPaid) {
        this.amountPaid = amountPaid;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public GuestLogStatus getStatus() {
        return status;
    }

    public void setStatus(GuestLogStatus status) {
        this.status = status;
    }

    public List<Invoice> getInvoices() {
        return invoices;
    }

    public void setInvoices(List<Invoice> invoices) {
        this.invoices = invoices;
    }


    public ID_TYPE getId_type() {
        return id_type;
    }

    public void setId_type(ID_TYPE id_type) {
        this.id_type = id_type;
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
}

