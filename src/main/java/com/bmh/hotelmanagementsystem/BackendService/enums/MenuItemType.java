package com.bmh.hotelmanagementsystem.BackendService.enums;

public enum MenuItemType {

    APPETIZER,
    MAINS,
    DESSERT,
    BEVERAGE,
    SIDES,
    SPECIALS;

    public static MenuItemType fromString(String value) {
        return MenuItemType.valueOf(value.toUpperCase());
    }

    public String toJson() {
        return this.name();
    }
}
