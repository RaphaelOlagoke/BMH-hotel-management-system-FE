package com.bmh.hotelmanagementsystem.BackendService.entities.Restaurant;


import com.bmh.hotelmanagementsystem.BackendService.enums.RestaurantOrderStatus;

import java.time.LocalDateTime;

public class OrderFilterRequest {
    private String customerName;
    private RestaurantOrderStatus status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

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
}
