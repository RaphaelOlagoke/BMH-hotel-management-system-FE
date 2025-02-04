package com.bmh.hotelmanagementsystem.BackendService.enums;

public enum CleaningType {
    STANDARD,
    POST_CHECKOUT,
    EMERGENCY,
    MAINTENANCE;


    public static CleaningType fromString(String value) {
        return CleaningType.valueOf(value.toUpperCase());
    }

    public String toJson() {
        return this.name();
    }
}
