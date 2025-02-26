package com.bmh.hotelmanagementsystem.BackendService.entities.Room;


public class RoomSummary {

    private int noOfAvailableRooms;
    private int noOfOccupiedRooms;
    private int noOfRoomsThatNeedCleaning;
    private int noOfRoomsThatNeedMaintenance;

    public int getNoOfAvailableRooms() {
        return noOfAvailableRooms;
    }

    public void setNoOfAvailableRooms(int noOfAvailableRooms) {
        this.noOfAvailableRooms = noOfAvailableRooms;
    }

    public int getNoOfOccupiedRooms() {
        return noOfOccupiedRooms;
    }

    public void setNoOfOccupiedRooms(int noOfOccupiedRooms) {
        this.noOfOccupiedRooms = noOfOccupiedRooms;
    }

    public int getNoOfRoomsThatNeedCleaning() {
        return noOfRoomsThatNeedCleaning;
    }

    public void setNoOfRoomsThatNeedCleaning(int noOfRoomsThatNeedCleaning) {
        this.noOfRoomsThatNeedCleaning = noOfRoomsThatNeedCleaning;
    }

    public int getNoOfRoomsThatNeedMaintenance() {
        return noOfRoomsThatNeedMaintenance;
    }

    public void setNoOfRoomsThatNeedMaintenance(int noOfRoomsThatNeedMaintenance) {
        this.noOfRoomsThatNeedMaintenance = noOfRoomsThatNeedMaintenance;
    }
}
