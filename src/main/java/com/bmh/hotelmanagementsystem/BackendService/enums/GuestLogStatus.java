package com.bmh.hotelmanagementsystem.BackendService.enums;

public enum GuestLogStatus {

    ACTIVE, COMPLETE, OVERDUE;

    public static GuestLogStatus fromString(String value) {
        return GuestLogStatus.valueOf(value.toUpperCase());
    }

    public String toJson() {
        return this.name();
    }
}
