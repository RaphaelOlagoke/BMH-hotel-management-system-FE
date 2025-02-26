package com.bmh.hotelmanagementsystem.BackendService.enums;

public enum PaymentStatus {

    UNPAID,
    PAID,
    DEBIT, REFUNDED;
//    PAID, UNPAID;

    public static PaymentStatus fromString(String value) {
        return PaymentStatus.valueOf(value.toUpperCase());
    }

    public String toJson() {
        return this.name();
    }
}
