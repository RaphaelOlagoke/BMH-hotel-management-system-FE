package com.bmh.hotelmanagementsystem.BackendService.entities.HouseKeeping;

import com.bmh.hotelmanagementsystem.BackendService.enums.CleaningStatus;
import com.bmh.hotelmanagementsystem.BackendService.enums.CleaningType;

import java.time.LocalDateTime;

public class CleaningLogFilterRequest {

    private int roomNumber;
    private CleaningStatus status;
    private LocalDateTime assignedOn;
    private LocalDateTime completedOn;
    private CleaningType cleaningType;

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public CleaningStatus getStatus() {
        return status;
    }

    public void setStatus(CleaningStatus status) {
        this.status = status;
    }

    public LocalDateTime getAssignedOn() {
        return assignedOn;
    }

    public void setAssignedOn(LocalDateTime assignedOn) {
        this.assignedOn = assignedOn;
    }

    public LocalDateTime getCompletedOn() {
        return completedOn;
    }

    public void setCompletedOn(LocalDateTime completedOn) {
        this.completedOn = completedOn;
    }

    public CleaningType getCleaningType() {
        return cleaningType;
    }

    public void setCleaningType(CleaningType cleaningType) {
        this.cleaningType = cleaningType;
    }
}
