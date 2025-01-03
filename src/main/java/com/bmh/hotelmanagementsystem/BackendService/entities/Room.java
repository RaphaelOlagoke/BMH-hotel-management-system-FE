package com.bmh.hotelmanagementsystem.BackendService.entities;

import com.bmh.hotelmanagementsystem.BackendService.enums.RoomStatus;
import com.bmh.hotelmanagementsystem.BackendService.enums.RoomType;

public class Room {

    private int roomNumber;
    private RoomType roomType;
    private RoomStatus roomStatus;

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
}

