package com.bmh.hotelmanagementsystem.BackendService.entities.Restaurant;

import com.bmh.hotelmanagementsystem.BackendService.enums.PaymentMethod;

import java.util.List;

public class CreateOrderRequest {

    private String customerName;
    private List<BillItem> items;

    private PaymentMethod paymentMethod;

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public List<BillItem> getItems() {
        return items;
    }

    public void setItems(List<BillItem> items) {
        this.items = items;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}

