package com.bmh.hotelmanagementsystem.BackendService.dto;

import com.bmh.hotelmanagementsystem.BackendService.enums.MaintenanceStatus;
import javafx.beans.property.*;

public class MaintenanceRow {

    private final StringProperty ref_column;
    private final StringProperty description_column;
    private final DoubleProperty cost_column;
    private final ObjectProperty<MaintenanceStatus> status_column;
    private final StringProperty created_on_column;
    private final StringProperty completed_on_column;


    public MaintenanceRow(String ref_column, String description_column, Double cost_column, MaintenanceStatus status_column, String createdOnColumn, String completedOnColumn) {
        this.ref_column = new SimpleStringProperty(ref_column);
        this.description_column = new SimpleStringProperty(description_column);
        this.cost_column = new SimpleDoubleProperty(cost_column);
        this.status_column = new SimpleObjectProperty<>(status_column);
        created_on_column = new SimpleStringProperty(createdOnColumn);
        completed_on_column = new SimpleStringProperty(completedOnColumn);
    }

    public String getRef_column() {
        return ref_column.get();
    }

    public StringProperty ref_columnProperty() {
        return ref_column;
    }

    public String getDescription_column() {
        return description_column.get();
    }

    public StringProperty description_columnProperty() {
        return description_column;
    }

    public double getCost_column() {
        return cost_column.get();
    }

    public DoubleProperty cost_columnProperty() {
        return cost_column;
    }

    public MaintenanceStatus getStatus_column() {
        return status_column.get();
    }

    public ObjectProperty<MaintenanceStatus> status_columnProperty() {
        return status_column;
    }

    public String getCreated_on_column() {
        return created_on_column.get();
    }

    public StringProperty created_on_columnProperty() {
        return created_on_column;
    }

    public String getCompleted_on_column() {
        return completed_on_column.get();
    }

    public StringProperty completed_on_columnProperty() {
        return completed_on_column;
    }
}
