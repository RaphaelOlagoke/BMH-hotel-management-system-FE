package com.bmh.hotelmanagementsystem.BackendService.entities.Room;

import com.bmh.hotelmanagementsystem.BackendService.enums.ID_TYPE;
import com.bmh.hotelmanagementsystem.BackendService.enums.PaymentMethod;

import java.util.List;

public class CreateGuestLogRequest {
    private String guestName;
    private List<Integer> roomNumbers;
    private PaymentMethod paymentMethod;

    private String discountCode;

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

    public List<Integer> getRoomNumbers() {
        return roomNumbers;
    }

    public void setRoomNumbers(List<Integer> roomNumbers) {
        this.roomNumbers = roomNumbers;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getDiscountCode() {
        return discountCode;
    }

    public void setDiscountCode(String discountCode) {
        this.discountCode = discountCode;
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
