package com.bmh.hotelmanagementsystem.BackendService.entities.Room;

import com.bmh.hotelmanagementsystem.BackendService.enums.RoomStatus;
import com.bmh.hotelmanagementsystem.BackendService.enums.RoomType;

public class RoomFilterRequest {

    private int roomNumber;
    private RoomType roomType;
    private RoomStatus roomStatus;
    private Boolean needsCleaning;
    private Boolean needsMaintenance;
    private Boolean archived;

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public RoomStatus getRoomStatus() {
        return roomStatus;
    }

    public void setRoomStatus(RoomStatus roomStatus) {
        this.roomStatus = roomStatus;
    }

    public Boolean getNeedsCleaning() {
        return needsCleaning;
    }

    public void setNeedsCleaning(Boolean needsCleaning) {
        this.needsCleaning = needsCleaning;
    }

    public Boolean getNeedsMaintenance() {
        return needsMaintenance;
    }

    public void setNeedsMaintenance(Boolean needsMaintenance) {
        this.needsMaintenance = needsMaintenance;
    }

    public Boolean getArchived() {
        return archived;
    }

    public void setArchived(Boolean archived) {
        this.archived = archived;
    }
}
