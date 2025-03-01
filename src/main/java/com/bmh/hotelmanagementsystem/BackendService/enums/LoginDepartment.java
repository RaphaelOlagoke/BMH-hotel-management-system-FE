package com.bmh.hotelmanagementsystem.BackendService.enums;

public enum LoginDepartment {

    RECEPTIONIST,
    RESTAURANT_BAR,
    ADMIN,
    SUPER_ADMIN;

    public static LoginDepartment fromString(String value) {
        return LoginDepartment.valueOf(value.toUpperCase());
    }

    public String toJson() {
        return this.name();
    }
}
