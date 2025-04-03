package com.bmh.hotelmanagementsystem.BackendService.entities.hall;

import com.bmh.hotelmanagementsystem.BackendService.enums.HallType;

public class HallPrices {
    private HallType hallType;
    private Double hallPrice;

    public HallType getHallType() {
        return hallType;
    }

    public void setHallType(HallType hallType) {
        this.hallType = hallType;
    }

    public Double getHallPrice() {
        return hallPrice;
    }

    public void setHallPrice(Double hallPrice) {
        this.hallPrice = hallPrice;
    }
}


