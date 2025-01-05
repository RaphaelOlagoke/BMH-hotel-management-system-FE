package com.bmh.hotelmanagementsystem.BackendService.entities;

import com.bmh.hotelmanagementsystem.BackendService.enums.RoomType;

public class RoomPrices {
    private RoomType roomType;
    private Double roomPrice;

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public Double getRoomPrice() {
        return roomPrice;
    }

    public void setRoomPrice(Double roomPrice) {
        this.roomPrice = roomPrice;
    }
}


