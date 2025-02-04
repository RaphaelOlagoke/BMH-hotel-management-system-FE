package com.bmh.hotelmanagementsystem.BackendService.entities.Room;

import com.bmh.hotelmanagementsystem.BackendService.enums.GuestLogStatus;

import java.time.LocalDateTime;

public class GuestLogRoom {

//    private GuestLogDto guestLog;
    private Room room;
    private LocalDateTime checkInDate;
    private LocalDateTime checkOutDate;
    private GuestLogStatus guestLogStatus;

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public LocalDateTime getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(LocalDateTime checkInDate) {
        this.checkInDate = checkInDate;
    }

    public LocalDateTime getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(LocalDateTime checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public GuestLogStatus getGuestLogStatus() {
        return guestLogStatus;
    }

    public void setGuestLogStatus(GuestLogStatus guestLogStatus) {
        this.guestLogStatus = guestLogStatus;
    }
}
