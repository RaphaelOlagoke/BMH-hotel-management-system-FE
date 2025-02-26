package com.bmh.hotelmanagementsystem.BackendService.entities.inventory;

import com.bmh.hotelmanagementsystem.BackendService.enums.StockActionReason;
import com.bmh.hotelmanagementsystem.BackendService.enums.StockItemCategory;

import java.time.LocalDate;

public class UpdateStockItemRequest {

    private String ref;
    private StockItemCategory category;

    private String name;

    private Integer quantity;

    private String unit;
    private StockActionReason reason;

    private LocalDate expiryDate;

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

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

    public StockActionReason getReason() {
        return reason;
    }

    public void setReason(StockActionReason reason) {
        this.reason = reason;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }
}
