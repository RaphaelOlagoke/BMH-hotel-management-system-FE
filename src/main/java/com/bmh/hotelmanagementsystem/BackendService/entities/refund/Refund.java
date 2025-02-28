package com.bmh.hotelmanagementsystem.BackendService.entities.refund;

import com.bmh.hotelmanagementsystem.BackendService.entities.Invoice.Invoice;

import java.time.LocalDateTime;

public class Refund {

    private String ref;
    private Double amount;
    private String reason;
    private LocalDateTime date = LocalDateTime.now();
    private Invoice invoice;

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }
}
