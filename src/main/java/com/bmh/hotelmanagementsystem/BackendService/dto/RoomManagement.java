package com.bmh.hotelmanagementsystem.BackendService.dto;

import com.bmh.hotelmanagementsystem.BackendService.entities.Room;
import com.bmh.hotelmanagementsystem.BackendService.enums.RoomStatus;
import com.bmh.hotelmanagementsystem.BackendService.enums.RoomType;
import javafx.beans.property.*;

public class RoomManagement {

    private final StringProperty room_number_column;
    private final ObjectProperty<RoomType> type_column;
    private final ObjectProperty<RoomStatus> status_column;
    private final BooleanProperty needs_cleaning_column;
    private final BooleanProperty needs_maintenance_column;
    private final BooleanProperty archive;
    private final Room room;

    public RoomManagement(String room_number_column, RoomType type_column, RoomStatus status_column, boolean needs_cleaning_column, boolean needs_maintenance_column, boolean archive, Room room) {
        this.room_number_column = new SimpleStringProperty(room_number_column);
        this.type_column = new SimpleObjectProperty<>(type_column);
        this.status_column =  new SimpleObjectProperty<>(status_column);
        this.needs_cleaning_column = new SimpleBooleanProperty(needs_cleaning_column);
        this.needs_maintenance_column = new SimpleBooleanProperty(needs_maintenance_column);
        this.archive = new SimpleBooleanProperty(archive);
        this.room = room;
    }

    public String getRoom_number_column() {
        return room_number_column.get();
    }

    public StringProperty room_number_columnProperty() {
        return room_number_column;
    }

    public RoomType getType_column() {
        return type_column.get();
    }

    public ObjectProperty<RoomType> type_columnProperty() {
        return type_column;
    }

    public RoomStatus getStatus_column() {
        return status_column.get();
    }

    public ObjectProperty<RoomStatus> status_columnProperty() {
        return status_column;
    }

    public boolean isNeeds_cleaning_column() {
        return needs_cleaning_column.get();
    }

    public BooleanProperty needs_cleaning_columnProperty() {
        return needs_cleaning_column;
    }

    public boolean isNeeds_maintenance_column() {
        return needs_maintenance_column.get();
    }

    public BooleanProperty needs_maintenance_columnProperty() {
        return needs_maintenance_column;
    }

    public boolean isArchive() {
        return archive.get();
    }

    public BooleanProperty archiveProperty() {
        return archive;
    }

    public Room getRoom() {
        return room;
    }
}
