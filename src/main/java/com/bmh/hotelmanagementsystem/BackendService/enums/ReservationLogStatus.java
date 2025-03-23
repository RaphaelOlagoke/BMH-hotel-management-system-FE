package com.bmh.hotelmanagementsystem.BackendService.enums;

public enum ReservationLogStatus {

    ACTIVE, COMPLETE, CANCELED;

    public static ReservationLogStatus fromString(String value) {
        return ReservationLogStatus.valueOf(value.toUpperCase());
    }

    public String toJson() {
        return this.name();
    }
}
