package com.bmh.hotelmanagementsystem.RoomManagement;

import com.bmh.hotelmanagementsystem.BackendService.enums.RoomType;

public class SelectedRoom {

    private Integer roomNumber;
    private Double roomPrice;

    private RoomType roomType;

    public Integer getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(Integer roomNumber) {
        this.roomNumber = roomNumber;
    }

    public Double getRoomPrice() {
        return roomPrice;
    }

    public void setRoomPrice(Double roomPrice) {
        this.roomPrice = roomPrice;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }
}
