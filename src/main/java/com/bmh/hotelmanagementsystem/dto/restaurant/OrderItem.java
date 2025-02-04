package com.bmh.hotelmanagementsystem.dto.restaurant;

import javafx.beans.property.*;

public class OrderItem {

    private final StringProperty name;
    private final IntegerProperty quantity;
    private final DoubleProperty price;

    public OrderItem(String name, Integer quantity, Double price) {
        this.name = new SimpleStringProperty(name);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.price = new SimpleDoubleProperty(price);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public int getQuantity() {
        return quantity.get();
    }

    public IntegerProperty quantityProperty() {
        return quantity;
    }

    public double getPrice() {
        return price.get();
    }

    public DoubleProperty priceProperty() {
        return price;
    }
}
