package com.bmh.hotelmanagementsystem.BackendService.enums;

public enum RestaurantOrderStatus {

    IN_PROGRESS,
    READY,
    COMPLETED,
    CANCELED;

    public static RestaurantOrderStatus fromString(String value) {
        return RestaurantOrderStatus.valueOf(value.toUpperCase());
    }

    public String toJson() {
        return this.name();
    }
}
