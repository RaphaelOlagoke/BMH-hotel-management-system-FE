package com.bmh.hotelmanagementsystem.dto.inventory;

import com.bmh.hotelmanagementsystem.BackendService.entities.inventory.StockItem;
import com.bmh.hotelmanagementsystem.BackendService.entities.inventory.StockRequest;
import com.bmh.hotelmanagementsystem.BackendService.enums.StockActionReason;
import com.bmh.hotelmanagementsystem.BackendService.enums.StockHistoryAction;
import com.bmh.hotelmanagementsystem.BackendService.enums.StockRequestStatus;
import javafx.beans.property.*;

public class StockRequestRow {
    private final StringProperty item_name_column;
    private final StringProperty department_column;
    private final IntegerProperty quantityRequested_column;
    private final ObjectProperty<StockRequestStatus> status_column;
    private final StringProperty retrievedAt_column;
    private final StringProperty createdAt_column;
    private final StockRequest stockRequest;

    public StockRequestRow(String item_name_column, String department_column, int quantityRequested_column,
                           StockRequestStatus status_column,  String retrievedAt_column, String createdAt_column,
                           StockRequest stockRequest) {
        this.item_name_column = new SimpleStringProperty(item_name_column);
        this.department_column = new SimpleStringProperty(department_column);
        this.quantityRequested_column = new SimpleIntegerProperty (quantityRequested_column);
        this.status_column = new SimpleObjectProperty<>(status_column);
        this.retrievedAt_column = new SimpleStringProperty(retrievedAt_column);
        this.createdAt_column = new SimpleStringProperty(createdAt_column);
        this.stockRequest = stockRequest;
    }

    public String getItem_name_column() {
        return item_name_column.get();
    }

    public StringProperty item_name_columnProperty() {
        return item_name_column;
    }

    public String getDepartment_column() {
        return department_column.get();
    }

    public StringProperty department_columnProperty() {
        return department_column;
    }

    public int getQuantityRequested_column() {
        return quantityRequested_column.get();
    }

    public IntegerProperty quantityRequested_columnProperty() {
        return quantityRequested_column;
    }

    public StockRequestStatus getStatus_column() {
        return status_column.get();
    }

    public ObjectProperty<StockRequestStatus> status_columnProperty() {
        return status_column;
    }

    public String getRetrievedAt_column() {
        return retrievedAt_column.get();
    }

    public StringProperty retrievedAt_columnProperty() {
        return retrievedAt_column;
    }

    public String getCreatedAt_column() {
        return createdAt_column.get();
    }

    public StringProperty createdAt_columnProperty() {
        return createdAt_column;
    }

    public StockRequest getStockRequest() {
        return stockRequest;
    }
}
