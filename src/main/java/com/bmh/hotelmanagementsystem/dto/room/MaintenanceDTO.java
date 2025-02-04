package com.bmh.hotelmanagementsystem.dto.room;

import com.bmh.hotelmanagementsystem.BackendService.entities.Room.Maintenance;

import java.util.List;

public class MaintenanceDTO {

    private int roomNumber;
    List<Maintenance> maintenanceList;

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public List<Maintenance> getMaintenanceList() {
        return maintenanceList;
    }

    public void setMaintenanceList(List<Maintenance> maintenanceList) {
        this.maintenanceList = maintenanceList;
    }
}


