package com.bmh.hotelmanagementsystem.BackendService.enums;

public enum StockItemCategory {

    ROOM,
    RESTAURANT_BAR,
    HOUSE_KEEPING,
    OFFICE_SUPPLIES,
    MAINTENANCE,
    OTHERS;

    public static StockItemCategory fromString(String value) {
        return StockItemCategory.valueOf(value.toUpperCase());
    }

    public String toJson() {
        return this.name();
    }
}
