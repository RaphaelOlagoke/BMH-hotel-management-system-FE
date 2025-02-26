package com.bmh.hotelmanagementsystem.BackendService.entities.inventory;

import com.bmh.hotelmanagementsystem.BackendService.enums.StockItemCategory;

import java.time.LocalDate;

public class CreateStockItemRequest {

    private StockItemCategory category;

    private String name;

    private Integer quantity;

    private String unit;

    private LocalDate expiryDate;

    public StockItemCategory getCategory() {
        return category;
    }

    public void setCategory(StockItemCategory category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }
}
