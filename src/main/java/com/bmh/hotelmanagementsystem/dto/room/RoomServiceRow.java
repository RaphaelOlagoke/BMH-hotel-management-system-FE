package com.bmh.hotelmanagementsystem.dto.room;

import com.bmh.hotelmanagementsystem.BackendService.entities.Room.RoomService;
import javafx.beans.property.*;

public class RoomServiceRow {

    private final StringProperty name_column;
    private final StringProperty price_column;

    private final RoomService roomService;

    public RoomServiceRow(String name_column, String price_column, RoomService roomService) {
        this.name_column = new SimpleStringProperty(name_column);
        this.price_column = new SimpleStringProperty(price_column);
        this.roomService = roomService;
    }

    public String getName_column() {
        return name_column.get();
    }

    public StringProperty name_columnProperty() {
        return name_column;
    }

    public String getPrice_column() {
        return price_column.get();
    }

    public StringProperty price_columnProperty() {
        return price_column;
    }

    public RoomService getRoomService() {
        return roomService;
    }
}
