package com.bmh.hotelmanagementsystem.BackendService.enums;

public enum MaintenanceStatus {

    ACTIVE, COMPLETE;


    public static MaintenanceStatus fromString(String value) {
        return MaintenanceStatus.valueOf(value.toUpperCase());
    }

    public String toJson() {
        return this.name();
    }
}
