package com.bmh.hotelmanagementsystem.BackendService.entities.Room;

import com.bmh.hotelmanagementsystem.BackendService.enums.ID_TYPE;
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

    private int noOfDays;

    private ID_TYPE idType;

    private String idRef;
    private String nextOfKinName;
    private String nextOfKinNumber;
    private String phoneNumber;

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

    public int getNoOfDays() {
        return noOfDays;
    }

    public void setNoOfDays(int noOfDays) {
        this.noOfDays = noOfDays;
    }

    public ID_TYPE getIdType() {
        return idType;
    }

    public void setIdType(ID_TYPE idType) {
        this.idType = idType;
    }

    public String getIdRef() {
        return idRef;
    }

    public void setIdRef(String idRef) {
        this.idRef = idRef;
    }

    public String getNextOfKinName() {
        return nextOfKinName;
    }

    public void setNextOfKinName(String nextOfKinName) {
        this.nextOfKinName = nextOfKinName;
    }

    public String getNextOfKinNumber() {
        return nextOfKinNumber;
    }

    public void setNextOfKinNumber(String nextOfKinNumber) {
        this.nextOfKinNumber = nextOfKinNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
