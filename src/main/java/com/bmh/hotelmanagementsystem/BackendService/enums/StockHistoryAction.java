package com.bmh.hotelmanagementsystem.BackendService.enums;


public enum StockHistoryAction {
    ADDED, TRANSFERRED, RETRIEVED, REMOVED;

    public static StockHistoryAction fromString(String value) {
        return StockHistoryAction.valueOf(value.toUpperCase());
    }

    public String toJson() {
        return this.name();
    }
}
