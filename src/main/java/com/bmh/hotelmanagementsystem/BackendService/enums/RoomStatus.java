package com.bmh.hotelmanagementsystem.BackendService.enums;


public enum RoomStatus {

    AVAILABLE, OCCUPIED;
//    , CLEANING, MAINTENANCE;

    public static RoomStatus fromString(String value) {
        return RoomStatus.valueOf(value.toUpperCase());
    }

    public String toJson() {
        return this.name();
    }

}
