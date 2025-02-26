package com.bmh.hotelmanagementsystem.BackendService.entities.inventory;


import com.bmh.hotelmanagementsystem.BackendService.enums.StockActionReason;
import com.bmh.hotelmanagementsystem.BackendService.enums.StockHistoryAction;

import java.time.LocalDateTime;

public class StockHistory {

    private String ref;
    private StockItem item;
    private String department;
    private Integer quantityMoved;
//    private User movedBy;
    private String unit;
    private StockActionReason reason;
    private StockHistoryAction action;
    private LocalDateTime timestamp;

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public StockItem getItem() {
        return item;
    }

    public void setItem(StockItem item) {
        this.item = item;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Integer getQuantityMoved() {
        return quantityMoved;
    }

    public void setQuantityMoved(Integer quantityMoved) {
        this.quantityMoved = quantityMoved;
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

    public StockHistoryAction getAction() {
        return action;
    }

    public void setAction(StockHistoryAction action) {
        this.action = action;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
