package com.bmh.hotelmanagementsystem.dto.inventory;

import com.bmh.hotelmanagementsystem.BackendService.enums.StockActionReason;
import com.bmh.hotelmanagementsystem.BackendService.enums.StockHistoryAction;
import javafx.beans.property.*;

public class StockHistoryRow {

    private final StringProperty ref_column;
    private final StringProperty item_name_column;
    private final StringProperty department_column;
    private final IntegerProperty quantityMoved_column;
    private final StringProperty unit_column;
    //    private User movedBy;
    private final ObjectProperty<StockActionReason> reason_column;
    private final ObjectProperty<StockHistoryAction> action_column;
    private final StringProperty timestamp_column;

    public StockHistoryRow(String ref_column, String item_name_column, String department_column,
                           int quantityMoved_column, String unit_column, StockActionReason reason_column,
                           StockHistoryAction action_column, String timestamp_column) {
        this.ref_column = new SimpleStringProperty(ref_column);
        this.item_name_column = new SimpleStringProperty(item_name_column);
        this.department_column = new SimpleStringProperty(department_column);
        this.quantityMoved_column = new SimpleIntegerProperty(quantityMoved_column);
        this.unit_column = new SimpleStringProperty(unit_column);
        this.reason_column = new SimpleObjectProperty<>(reason_column);
        this.action_column = new SimpleObjectProperty<>(action_column);
        this.timestamp_column = new SimpleStringProperty(timestamp_column);
    }

    public String getRef_column() {
        return ref_column.get();
    }

    public StringProperty ref_columnProperty() {
        return ref_column;
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

    public int getQuantityMoved_column() {
        return quantityMoved_column.get();
    }

    public IntegerProperty quantityMoved_columnProperty() {
        return quantityMoved_column;
    }

    public String getUnit_column() {
        return unit_column.get();
    }

    public StringProperty unit_columnProperty() {
        return unit_column;
    }

    public StockActionReason getReason_column() {
        return reason_column.get();
    }

    public ObjectProperty<StockActionReason> reason_columnProperty() {
        return reason_column;
    }

    public StockHistoryAction getAction_column() {
        return action_column.get();
    }

    public ObjectProperty<StockHistoryAction> action_columnProperty() {
        return action_column;
    }

    public String getTimestamp_column() {
        return timestamp_column.get();
    }

    public StringProperty timestamp_columnProperty() {
        return timestamp_column;
    }
}
