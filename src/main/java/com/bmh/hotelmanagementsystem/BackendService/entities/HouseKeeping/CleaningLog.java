package com.bmh.hotelmanagementsystem.BackendService.entities.HouseKeeping;


import com.bmh.hotelmanagementsystem.BackendService.entities.Room.Room;
import com.bmh.hotelmanagementsystem.BackendService.enums.CleaningStatus;
import com.bmh.hotelmanagementsystem.BackendService.enums.CleaningType;

import java.time.LocalDateTime;

public class CleaningLog {

    private String ref;
    private Room room;
    private CleaningStatus status;
    private LocalDateTime assignedOn;
    private LocalDateTime completedOn;
    private CleaningType cleaningType;


    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
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
