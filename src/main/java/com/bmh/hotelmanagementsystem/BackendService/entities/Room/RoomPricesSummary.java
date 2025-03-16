package com.bmh.hotelmanagementsystem.BackendService.entities.Room;

public class RoomPricesSummary {
    
//    Double standardPrice;
//    Double deluxePrice;
//    Double executivePrice;
//    Double vipPrice;


    Double executiveSuitePrice;
    Double businessSuiteAPrice;
    Double businessSuiteBPrice;
    Double executiveDeluxePrice;
    Double deluxePrice;
    Double classicPrice;


    public Double getExecutiveSuitePrice() {
        return executiveSuitePrice;
    }

    public void setExecutiveSuitePrice(Double executiveSuitePrice) {
        this.executiveSuitePrice = executiveSuitePrice;
    }

    public Double getBusinessSuiteAPrice() {
        return businessSuiteAPrice;
    }

    public void setBusinessSuiteAPrice(Double businessSuiteAPrice) {
        this.businessSuiteAPrice = businessSuiteAPrice;
    }

    public Double getBusinessSuiteBPrice() {
        return businessSuiteBPrice;
    }

    public void setBusinessSuiteBPrice(Double businessSuiteBPrice) {
        this.businessSuiteBPrice = businessSuiteBPrice;
    }

    public Double getExecutiveDeluxePrice() {
        return executiveDeluxePrice;
    }

    public void setExecutiveDeluxePrice(Double executiveDeluxePrice) {
        this.executiveDeluxePrice = executiveDeluxePrice;
    }

    public Double getDeluxePrice() {
        return deluxePrice;
    }

    public void setDeluxePrice(Double deluxePrice) {
        this.deluxePrice = deluxePrice;
    }

    public Double getClassicPrice() {
        return classicPrice;
    }

    public void setClassicPrice(Double classicPrice) {
        this.classicPrice = classicPrice;
    }
}
