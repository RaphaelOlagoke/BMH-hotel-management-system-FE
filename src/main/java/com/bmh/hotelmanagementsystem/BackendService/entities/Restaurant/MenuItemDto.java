package com.bmh.hotelmanagementsystem.BackendService.entities.Restaurant;

import com.bmh.hotelmanagementsystem.BackendService.enums.MenuItemType;

public class MenuItemDto {

    private String ref;
    private String name;
    private Double price;
    private MenuItemType category;

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public MenuItemType getCategory() {
        return category;
    }

    public void setCategory(MenuItemType category) {
        this.category = category;
    }
}
