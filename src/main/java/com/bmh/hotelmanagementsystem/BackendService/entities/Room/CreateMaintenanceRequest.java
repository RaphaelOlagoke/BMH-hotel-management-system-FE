package com.bmh.hotelmanagementsystem.BackendService.entities.Room;

public class CreateMaintenanceRequest {
    private int roomNumber;
    private String description;

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
