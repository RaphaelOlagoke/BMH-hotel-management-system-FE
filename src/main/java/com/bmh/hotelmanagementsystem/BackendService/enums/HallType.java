package com.bmh.hotelmanagementsystem.BackendService.enums;

public enum HallType {

    CONFERENCE_ROOM, MEETING_ROOM, MEETING_HALL;

    public static HallType fromString(String value) {
        return HallType.valueOf(value.toUpperCase());
    }

    public String toJson() {
        return this.name();
    }
}
