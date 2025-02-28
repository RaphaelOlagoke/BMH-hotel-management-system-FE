package com.bmh.hotelmanagementsystem.BackendService.entities.refund;


public class CreateRefundRequest {

//    private Double amount;
    private String reason;
    private String invoiceRef;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getInvoiceRef() {
        return invoiceRef;
    }

    public void setInvoiceRef(String invoiceRef) {
        this.invoiceRef = invoiceRef;
    }
}
