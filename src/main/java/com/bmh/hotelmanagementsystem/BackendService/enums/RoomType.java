package com.bmh.hotelmanagementsystem.BackendService.enums;
public enum RoomType {

    STANDARD,DELUXE,EXECUTIVE,VIP;

    public static RoomType fromString(String value) {
        return RoomType.valueOf(value.toUpperCase());
    }

    public String toJson() {
        return this.name();
    }
}
