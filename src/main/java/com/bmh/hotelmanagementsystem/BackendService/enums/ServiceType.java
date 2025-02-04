package com.bmh.hotelmanagementsystem.BackendService.enums;

public enum ServiceType {

    ROOM,
    RESTAURANT_BAR,
    MAINTENANCE;

    public static ServiceType fromString(String value) {
        return ServiceType.valueOf(value.toUpperCase());
    }

    public String toJson() {
        return this.name();
    }
}
