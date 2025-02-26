package com.bmh.hotelmanagementsystem.BackendService.enums;

public enum StockActionReason {
    MISCOUNT,           // For when stock was miscounted
    DAMAGED,            // For when stock is damaged
    EXPIRED,            // For expired items
    RETURNED,           // For returned stock
    RESTOCK,            // For adding stock again
    OTHER,           // A generic reason, in case it's not one of the above
    TRANSFERRED;

    public static StockActionReason fromString(String value) {
        return StockActionReason.valueOf(value.toUpperCase());
    }

    public String toJson() {
        return this.name();
    }
}