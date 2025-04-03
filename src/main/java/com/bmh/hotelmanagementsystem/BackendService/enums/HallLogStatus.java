package com.bmh.hotelmanagementsystem.BackendService.enums;

public enum HallLogStatus {

    ACTIVE, COMPLETE, CANCELED, UPCOMING;

    public static HallLogStatus fromString(String value) {
        return HallLogStatus.valueOf(value.toUpperCase());
    }

    public String toJson() {
        return this.name();
    }
}
