package com.bmh.hotelmanagementsystem.dto.invoice;

import com.bmh.hotelmanagementsystem.BackendService.entities.Invoice.Invoice;
import com.bmh.hotelmanagementsystem.BackendService.entities.Invoice.Item;
import javafx.beans.property.*;

public class ItemRow {

    private final StringProperty name;

    private final IntegerProperty quantity;

    private final StringProperty price;

    private final Item item;

    public ItemRow(String name, int quantity, String price, Item item) {
        this.name =  new SimpleStringProperty(name);
        this.quantity =  new SimpleIntegerProperty(quantity);
        this.price =  new SimpleStringProperty(price);
        this.item = item;
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

    public String getPrice() {
        return price.get();
    }

    public StringProperty priceProperty() {
        return price;
    }

    public Item getItem() {
        return item;
    }
}
