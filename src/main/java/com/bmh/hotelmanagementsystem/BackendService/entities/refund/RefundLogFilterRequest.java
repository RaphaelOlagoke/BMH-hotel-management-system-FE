package com.bmh.hotelmanagementsystem.BackendService.entities.refund;

import java.time.LocalDateTime;

public class RefundLogFilterRequest {

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String query;

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

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
