package com.bmh.hotelmanagementsystem.BackendService.entities.Room;

import com.bmh.hotelmanagementsystem.BackendService.enums.PaymentMethod;
import com.bmh.hotelmanagementsystem.RoomManagement.SelectedRoom;

import java.util.List;

public class CheckIn {

    private String guestName;
//    private int roomNumber;

//    List<Integer> roomNumbers;
//
//    private double roomPrice;
//
//    private RoomType roomType;

    private List<SelectedRoom> selectedRooms;
    private PaymentMethod paymentMethod;

    private Double totalPrice;

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

//    public int getRoomNumber() {
//        return roomNumber;
//    }
//
//    public void setRoomNumber(int roomNumber) {
//        this.roomNumber = roomNumber;
//    }


//    public List<Integer> getRoomNumbers() {
//        return roomNumbers;
//    }
//
//    public void setRoomNumbers(List<Integer> roomNumbers) {
//        this.roomNumbers = roomNumbers;
//    }
//
//    public double getRoomPrice() {
//        return roomPrice;
//    }
//
//    public void setRoomPrice(double roomPrice) {
//        this.roomPrice = roomPrice;
//    }
//
//    public RoomType getRoomType() {
//        return roomType;
//    }
//
//    public void setRoomType(RoomType roomType) {
//        this.roomType = roomType;
//    }


    public List<SelectedRoom> getSelectedRooms() {
        return selectedRooms;
    }

    public void setSelectedRooms(List<SelectedRoom> selectedRooms) {
        this.selectedRooms = selectedRooms;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
