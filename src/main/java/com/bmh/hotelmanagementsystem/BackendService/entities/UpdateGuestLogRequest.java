package com.bmh.hotelmanagementsystem.BackendService.entities;

import java.util.List;

public class UpdateGuestLogRequest {

    private List<Integer> roomNumbers;

    public List<Integer> getRoomNumbers() {
        return roomNumbers;
    }

    public void setRoomNumbers(List<Integer> roomNumbers) {
        this.roomNumbers = roomNumbers;
    }
}
