package com.bmh.hotelmanagementsystem.BackendService.entities.inventory;


import com.bmh.hotelmanagementsystem.BackendService.enums.StockItemCategory;

public class CreateStockRequest {

    private String stockItemRef;

    private StockItemCategory department;
    private Integer quantityRequested;

    public String getItem() {
        return stockItemRef;
    }

    public void setItem(String item) {
        this.stockItemRef = item;
    }

    public StockItemCategory getDepartment() {
        return department;
    }

    public void setDepartment(StockItemCategory department) {
        this.department = department;
    }

    public Integer getQuantityRequested() {
        return quantityRequested;
    }

    public void setQuantityRequested(Integer quantityRequested) {
        this.quantityRequested = quantityRequested;
    }
}
