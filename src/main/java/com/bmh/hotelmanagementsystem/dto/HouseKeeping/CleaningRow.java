package com.bmh.hotelmanagementsystem.dto.HouseKeeping;

import com.bmh.hotelmanagementsystem.BackendService.entities.HouseKeeping.CleaningLog;
import com.bmh.hotelmanagementsystem.BackendService.enums.CleaningStatus;
import javafx.beans.property.*;

public class CleaningRow {

    private final StringProperty ref_column;

    private final IntegerProperty room_column;
    private final ObjectProperty<CleaningStatus> status_column;
    private final StringProperty assigned_on_column;
    private final StringProperty completed_on_column;

    private final CleaningLog cleaningLog;


    public CleaningRow(String ref_column, Integer room_column, CleaningStatus status_column, String assigned_on_column, String completed_on_column, CleaningLog cleaningLog) {
        this.ref_column = new SimpleStringProperty(ref_column);
        this.room_column = new SimpleIntegerProperty(room_column);
        this.status_column = new SimpleObjectProperty<>(status_column);
        this.assigned_on_column = new SimpleStringProperty(assigned_on_column);
        this.completed_on_column = new SimpleStringProperty(completed_on_column);
        this.cleaningLog = cleaningLog;
    }

    public String getRef_column() {
        return ref_column.get();
    }

    public StringProperty ref_columnProperty() {
        return ref_column;
    }

    public int getRoom_column() {
        return room_column.get();
    }

    public IntegerProperty room_columnProperty() {
        return room_column;
    }

    public CleaningStatus getStatus_column() {
        return status_column.get();
    }

    public ObjectProperty<CleaningStatus> status_columnProperty() {
        return status_column;
    }

    public String getAssigned_on_column() {
        return assigned_on_column.get();
    }

    public StringProperty assigned_on_columnProperty() {
        return assigned_on_column;
    }

    public String getCompleted_on_column() {
        return completed_on_column.get();
    }

    public StringProperty completed_on_columnProperty() {
        return completed_on_column;
    }

    public CleaningLog getCleaningLog() {
        return cleaningLog;
    }
}
