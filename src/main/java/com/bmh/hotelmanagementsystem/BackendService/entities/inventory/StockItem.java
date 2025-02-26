package com.bmh.hotelmanagementsystem.BackendService.entities.inventory;

import com.bmh.hotelmanagementsystem.BackendService.enums.StockItemCategory;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class StockItem {
    private String ref;
    private StockItemCategory category;

    private String name;

    private Integer quantity;

    private String unit;

    private LocalDate expiryDate;

    //    private User addedBy;
    private LocalDateTime createdDateTime;
    private LocalDateTime lastModifiedDateTime;

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

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public LocalDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(LocalDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public LocalDateTime getLastModifiedDateTime() {
        return lastModifiedDateTime;
    }

    public void setLastModifiedDateTime(LocalDateTime lastModifiedDateTime) {
        this.lastModifiedDateTime = lastModifiedDateTime;
    }
}
