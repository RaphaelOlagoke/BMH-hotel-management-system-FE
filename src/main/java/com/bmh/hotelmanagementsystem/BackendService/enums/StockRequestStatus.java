package com.bmh.hotelmanagementsystem.BackendService.enums;

public enum StockRequestStatus {
    PENDING, APPROVED, DECLINED, RETRIEVED;

    public static StockRequestStatus fromString(String value) {
        return StockRequestStatus.valueOf(value.toUpperCase());
    }

    public String toJson() {
        return this.name();
    }
}
