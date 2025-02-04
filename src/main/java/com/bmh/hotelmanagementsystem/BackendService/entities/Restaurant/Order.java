package com.bmh.hotelmanagementsystem.BackendService.entities.Restaurant;

import com.bmh.hotelmanagementsystem.BackendService.entities.Invoice.Invoice;
import com.bmh.hotelmanagementsystem.BackendService.enums.RestaurantOrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public class Order {

    private String ref;
    private String customerName;
    private RestaurantOrderStatus status;
    private List<BillItem> items;
    private Invoice invoice;
    private LocalDateTime createdDateTime;

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public RestaurantOrderStatus getStatus() {
        return status;
    }

    public void setStatus(RestaurantOrderStatus status) {
        this.status = status;
    }

    public List<BillItem> getItems() {
        return items;
    }

    public void setItems(List<BillItem> items) {
        this.items = items;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public LocalDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(LocalDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }
}
