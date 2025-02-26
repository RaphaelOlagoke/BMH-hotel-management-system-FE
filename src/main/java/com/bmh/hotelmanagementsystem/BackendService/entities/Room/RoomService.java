package com.bmh.hotelmanagementsystem.BackendService.entities.Room;


public class RoomService {

    private String ref;

    private String service;

    private Double price;

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
