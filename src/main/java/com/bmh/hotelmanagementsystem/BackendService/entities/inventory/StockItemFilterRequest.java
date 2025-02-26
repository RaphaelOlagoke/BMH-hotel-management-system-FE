package com.bmh.hotelmanagementsystem.BackendService.entities.inventory;


import com.bmh.hotelmanagementsystem.BackendService.enums.StockItemCategory;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class StockItemFilterRequest {
    private StockItemCategory category;
    private LocalDate startExpiryDate;
    private LocalDate endExpiryDate;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private int quantity;

    public StockItemCategory getCategory() {
        return category;
    }

    public void setCategory(StockItemCategory category) {
        this.category = category;
    }

    public LocalDate getStartExpiryDate() {
        return startExpiryDate;
    }

    public void setStartExpiryDate(LocalDate startExpiryDate) {
        this.startExpiryDate = startExpiryDate;
    }

    public LocalDate getEndExpiryDate() {
        return endExpiryDate;
    }

    public void setEndExpiryDate(LocalDate endExpiryDate) {
        this.endExpiryDate = endExpiryDate;
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
