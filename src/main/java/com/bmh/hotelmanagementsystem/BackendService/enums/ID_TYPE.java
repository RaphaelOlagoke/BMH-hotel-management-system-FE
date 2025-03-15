package com.bmh.hotelmanagementsystem.BackendService.enums;

public enum ID_TYPE {
    DRIVER_LICENSE, NIN, VOTER_CARD, PASSPORT;

    public static ID_TYPE fromString(String value) {
        return ID_TYPE.valueOf(value.toUpperCase());
    }

    public String toJson() {
        return this.name();
    }
}
