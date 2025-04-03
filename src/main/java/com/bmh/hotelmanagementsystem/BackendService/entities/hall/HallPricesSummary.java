package com.bmh.hotelmanagementsystem.BackendService.entities.hall;

public class HallPricesSummary {

    Double meetingRoomPrice;
    Double meetingHallPrice;
    Double conferenceHallPrice;

    public Double getMeetingRoomPrice() {
        return meetingRoomPrice;
    }

    public void setMeetingRoomPrice(Double meetingRoomPrice) {
        this.meetingRoomPrice = meetingRoomPrice;
    }

    public Double getMeetingHallPrice() {
        return meetingHallPrice;
    }

    public void setMeetingHallPrice(Double meetingHallPrice) {
        this.meetingHallPrice = meetingHallPrice;
    }

    public Double getConferenceHallPrice() {
        return conferenceHallPrice;
    }

    public void setConferenceHallPrice(Double conferenceHallPrice) {
        this.conferenceHallPrice = conferenceHallPrice;
    }
}
