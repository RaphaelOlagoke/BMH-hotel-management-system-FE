package com.bmh.hotelmanagementsystem.BackendService.entities;


public class CompleteMaintenanceRequest {

    private String ref;
    private Double cost;

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }
}
