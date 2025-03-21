package com.bmh.hotelmanagementsystem.BackendService.entities.Invoice;


public class InvoiceSummary {

    private int noOfPaidInvoice;
    private int noOfUnPaidInvoice;
    private int noOfDebitInvoice;
    private int noOfCreditInvoice;
    private int noOfRefundedInvoice;

    private Double totalValueOfPaidInvoice;
    private Double totalValueOfUnPaidInvoice;
    private Double totalValueOfDebitInvoice;

    private Double totalValueOfCreditInvoice;
    private Double totalValueOfRefundedInvoice;

    public int getNoOfPaidInvoice() {
        return noOfPaidInvoice;
    }

    public void setNoOfPaidInvoice(int noOfPaidInvoice) {
        this.noOfPaidInvoice = noOfPaidInvoice;
    }

    public int getNoOfUnPaidInvoice() {
        return noOfUnPaidInvoice;
    }

    public void setNoOfUnPaidInvoice(int noOfUnPaidInvoice) {
        this.noOfUnPaidInvoice = noOfUnPaidInvoice;
    }

    public int getNoOfDebitInvoice() {
        return noOfDebitInvoice;
    }

    public void setNoOfDebitInvoice(int noOfDebitInvoice) {
        this.noOfDebitInvoice = noOfDebitInvoice;
    }

    public int getNoOfRefundedInvoice() {
        return noOfRefundedInvoice;
    }

    public void setNoOfRefundedInvoice(int noOfRefundedInvoice) {
        this.noOfRefundedInvoice = noOfRefundedInvoice;
    }

    public Double getTotalValueOfPaidInvoice() {
        return totalValueOfPaidInvoice;
    }

    public void setTotalValueOfPaidInvoice(Double totalValueOfPaidInvoice) {
        this.totalValueOfPaidInvoice = totalValueOfPaidInvoice;
    }

    public Double getTotalValueOfUnPaidInvoice() {
        return totalValueOfUnPaidInvoice;
    }

    public void setTotalValueOfUnPaidInvoice(Double totalValueOfUnPaidInvoice) {
        this.totalValueOfUnPaidInvoice = totalValueOfUnPaidInvoice;
    }

    public Double getTotalValueOfDebitInvoice() {
        return totalValueOfDebitInvoice;
    }

    public void setTotalValueOfDebitInvoice(Double totalValueOfDebitInvoice) {
        this.totalValueOfDebitInvoice = totalValueOfDebitInvoice;
    }

    public Double getTotalValueOfRefundedInvoice() {
        return totalValueOfRefundedInvoice;
    }

    public void setTotalValueOfRefundedInvoice(Double totalValueOfRefundedInvoice) {
        this.totalValueOfRefundedInvoice = totalValueOfRefundedInvoice;
    }

    public int getNoOfCreditInvoice() {
        return noOfCreditInvoice;
    }

    public void setNoOfCreditInvoice(int noOfCreditInvoice) {
        this.noOfCreditInvoice = noOfCreditInvoice;
    }

    public Double getTotalValueOfCreditInvoice() {
        return totalValueOfCreditInvoice;
    }

    public void setTotalValueOfCreditInvoice(Double totalValueOfCreditInvoice) {
        this.totalValueOfCreditInvoice = totalValueOfCreditInvoice;
    }
}
