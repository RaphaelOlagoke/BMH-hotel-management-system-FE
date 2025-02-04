package com.bmh.hotelmanagementsystem.dto.restaurant;

import com.bmh.hotelmanagementsystem.BackendService.entities.Restaurant.BillItem;
import com.bmh.hotelmanagementsystem.BackendService.enums.PaymentMethod;

import java.util.List;

public class OrderDetails {

    PaymentMethod paymentMethod;

    List<BillItem> billItems;

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public List<BillItem> getBillItems() {
        return billItems;
    }

    public void setBillItems(List<BillItem> billItems) {
        this.billItems = billItems;
    }
}


