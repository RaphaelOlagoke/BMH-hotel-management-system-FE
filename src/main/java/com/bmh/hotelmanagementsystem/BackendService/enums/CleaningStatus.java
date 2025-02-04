package com.bmh.hotelmanagementsystem.BackendService.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CleaningStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    CANCELED;
//    SKIPPED;

    public static CleaningStatus fromString(String value) {
        return CleaningStatus.valueOf(value.toUpperCase());
    }

    public String toJson() {
        return this.name();
    }
}
