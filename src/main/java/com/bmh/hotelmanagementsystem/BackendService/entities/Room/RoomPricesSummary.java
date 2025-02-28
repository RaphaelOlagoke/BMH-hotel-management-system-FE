package com.bmh.hotelmanagementsystem.BackendService.entities.Room;

public class RoomPricesSummary {
    
    Double standardPrice;
    Double deluxePrice;
    Double executivePrice;
    Double vipPrice;

    public Double getStandardPrice() {
        return standardPrice;
    }

    public void setStandardPrice(Double standardPrice) {
        this.standardPrice = standardPrice;
    }

    public Double getDeluxePrice() {
        return deluxePrice;
    }

    public void setDeluxePrice(Double deluxePrice) {
        this.deluxePrice = deluxePrice;
    }

    public Double getExecutivePrice() {
        return executivePrice;
    }

    public void setExecutivePrice(Double executivePrice) {
        this.executivePrice = executivePrice;
    }

    public Double getVipPrice() {
        return vipPrice;
    }

    public void setVipPrice(Double vipPrice) {
        this.vipPrice = vipPrice;
    }
}
