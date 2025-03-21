package com.bmh.hotelmanagementsystem.BackendService.entities;


public class AdditionalChargesSummary {

    Double vatPrice;
    Double taxPrice;

    public Double getVatPrice() {
        return vatPrice;
    }

    public void setVatPrice(Double vatPrice) {
        this.vatPrice = vatPrice;
    }

    public Double getTaxPrice() {
        return taxPrice;
    }

    public void setTaxPrice(Double taxPrice) {
        this.taxPrice = taxPrice;
    }
}
