package com.bmh.hotelmanagementsystem.BackendService.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PaymentMethod {

    CARD, CASH, TRANSFER, NONE;

    public static PaymentMethod fromString(String value) {
        return PaymentMethod.valueOf(value.toUpperCase());
    }

    public String toJson() {
        return this.name();
    }
}
