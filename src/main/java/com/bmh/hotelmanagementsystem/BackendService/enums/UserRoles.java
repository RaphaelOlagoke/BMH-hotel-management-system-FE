package com.bmh.hotelmanagementsystem.BackendService.enums;


public enum UserRoles {

    USER,
    ADMIN,
    SUPER_ADMIN;

    public static UserRoles fromString(String value) {
        return UserRoles.valueOf(value.toUpperCase());
    }

    public String toJson() {
        return this.name();
    }
}
