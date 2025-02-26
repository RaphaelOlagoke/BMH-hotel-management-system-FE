package com.bmh.hotelmanagementsystem.BackendService.entities.inventory;


import com.bmh.hotelmanagementsystem.BackendService.enums.StockActionReason;
import com.bmh.hotelmanagementsystem.BackendService.enums.StockHistoryAction;

import java.time.LocalDateTime;

public class StockHistoryFilterRequest {
    private String department;
    private StockActionReason reason;
    private StockHistoryAction action;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
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
