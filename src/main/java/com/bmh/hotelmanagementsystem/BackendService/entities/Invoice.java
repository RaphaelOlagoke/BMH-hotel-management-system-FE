package com.bmh.hotelmanagementsystem.BackendService.entities;

import com.bmh.hotelmanagementsystem.BackendService.enums.PaymentMethod;
import com.bmh.hotelmanagementsystem.BackendService.enums.PaymentStatus;

import java.time.LocalDateTime;
import java.util.List;

public class Invoice {
//    private GuestLog guestLog;
    private LocalDateTime issueDate;
    private Double totalAmount;
    private PaymentStatus paymentStatus;
    private PaymentMethod paymentMethod;
    private String service;
//    private String serviceDetails;
    private List<Item> items;

    public LocalDateTime getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDateTime issueDate) {
        this.issueDate = issueDate;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

//    public String getServiceDetails() {
//        return serviceDetails;
//    }
//
//    public void setServiceDetails(String serviceDetails) {
//        this.serviceDetails = serviceDetails;
//    }


    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }
}
