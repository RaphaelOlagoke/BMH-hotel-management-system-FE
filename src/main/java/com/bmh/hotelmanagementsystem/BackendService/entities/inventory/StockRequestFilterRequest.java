package com.bmh.hotelmanagementsystem.BackendService.entities.inventory;


import com.bmh.hotelmanagementsystem.BackendService.enums.StockRequestStatus;

import java.time.LocalDateTime;

public class StockRequestFilterRequest {
    private String department;
    private StockRequestStatus status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public StockRequestStatus getStatus() {
        return status;
    }

    public void setStatus(StockRequestStatus status) {
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
