package com.bmh.hotelmanagementsystem.BackendService.entities.HouseKeeping;

public class CreateCleaningLogRequest {
    private int roomNumber;
//    private CleaningType cleaningType;


    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }
}
