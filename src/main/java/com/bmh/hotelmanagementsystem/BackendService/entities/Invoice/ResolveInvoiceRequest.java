package com.bmh.hotelmanagementsystem.BackendService.entities.Invoice;

import com.bmh.hotelmanagementsystem.BackendService.enums.PaymentMethod;

import java.util.List;

public class ResolveInvoiceRequest {
    private List<String> invoiceRefs;
    private PaymentMethod paymentMethod;


    public List<String> getInvoiceRefs() {
        return invoiceRefs;
    }

    public void setInvoiceRefs(List<String> invoiceRefs) {
        this.invoiceRefs = invoiceRefs;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}


