package com.bmh.hotelmanagementsystem.dto.inventory;

import com.bmh.hotelmanagementsystem.BackendService.entities.inventory.StockItem;
import com.bmh.hotelmanagementsystem.BackendService.enums.StockItemCategory;
import javafx.beans.property.*;

public class StockRow {

    private final StringProperty name_column;
    private final ObjectProperty<StockItemCategory> category_column;
    private final IntegerProperty quantity_column;
    private final StringProperty unit_column;
    private final StringProperty created_date_column;
    private final StringProperty expiration_date_column;

    private final StockItem stockItem;

    public StockRow(String name_column, StockItemCategory category_column,
                    int quantity_column, String unit_column, String created_date_column,
                    String expiration_date_column, StockItem stockItem) {
        this.name_column = new SimpleStringProperty(name_column);
        this.category_column = new SimpleObjectProperty<>(category_column);
        this.quantity_column = new SimpleIntegerProperty(quantity_column);
        this.unit_column = new SimpleStringProperty(unit_column);
        this.created_date_column = new SimpleStringProperty(created_date_column);
        this.expiration_date_column = new SimpleStringProperty(expiration_date_column);
        this.stockItem = stockItem;
    }

    public String getName_column() {
        return name_column.get();
    }

    public StringProperty name_columnProperty() {
        return name_column;
    }

    public StockItemCategory getCategory_column() {
        return category_column.get();
    }

    public ObjectProperty<StockItemCategory> category_columnProperty() {
        return category_column;
    }

    public int getQuantity_column() {
        return quantity_column.get();
    }

    public IntegerProperty quantity_columnProperty() {
        return quantity_column;
    }

    public String getUnit_column() {
        return unit_column.get();
    }

    public StringProperty unit_columnProperty() {
        return unit_column;
    }

    public String getCreated_date_column() {
        return created_date_column.get();
    }

    public StringProperty created_date_columnProperty() {
        return created_date_column;
    }

    public String getExpiration_date_column() {
        return expiration_date_column.get();
    }

    public StringProperty expiration_date_columnProperty() {
        return expiration_date_column;
    }

    public StockItem getStockItem() {
        return stockItem;
    }
}
