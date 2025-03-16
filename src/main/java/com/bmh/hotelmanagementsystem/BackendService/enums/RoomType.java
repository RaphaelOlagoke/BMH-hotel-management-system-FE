package com.bmh.hotelmanagementsystem.BackendService.enums;
public enum RoomType {

//    STANDARD,DELUXE,EXECUTIVE,VIP;

    EXECUTIVE_SUITE,
    BUSINESS_SUITE_A,
    BUSINESS_SUITE_B,
    EXECUTIVE_DELUXE,
    DELUXE,
    CLASSIC;

    public static RoomType fromString(String value) {
        return RoomType.valueOf(value.toUpperCase());
    }

    public String toJson() {
        return this.name();
    }
}
