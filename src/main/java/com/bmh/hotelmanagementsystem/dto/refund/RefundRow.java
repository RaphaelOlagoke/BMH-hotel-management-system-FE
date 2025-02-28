package com.bmh.hotelmanagementsystem.dto.refund;

import com.bmh.hotelmanagementsystem.BackendService.entities.refund.Refund;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class RefundRow {

    private final StringProperty ref_column;
    private final StringProperty amount_column;
    private final StringProperty date_column;
    private final StringProperty reason_column;
    private final StringProperty invoice_ref_column;
    private final Refund refund;

    public RefundRow(String ref_column, String amount_column, String date_column,
                     String reason_column, String invoice_ref_column, Refund refund) {
        this.ref_column = new SimpleStringProperty(ref_column);
        this.amount_column = new SimpleStringProperty(amount_column);
        this.date_column = new SimpleStringProperty(date_column);
        this.reason_column = new SimpleStringProperty(reason_column);
        this.invoice_ref_column = new SimpleStringProperty(invoice_ref_column);
        this.refund = refund;
    }

    public String getRef_column() {
        return ref_column.get();
    }

    public StringProperty ref_columnProperty() {
        return ref_column;
    }

    public void setRef_column(String ref_column) {
        this.ref_column.set(ref_column);
    }

    public String getAmount_column() {
        return amount_column.get();
    }

    public StringProperty amount_columnProperty() {
        return amount_column;
    }

    public void setAmount_column(String amount_column) {
        this.amount_column.set(amount_column);
    }

    public String getDate_column() {
        return date_column.get();
    }

    public StringProperty date_columnProperty() {
        return date_column;
    }

    public void setDate_column(String date_column) {
        this.date_column.set(date_column);
    }

    public String getReason_column() {
        return reason_column.get();
    }

    public StringProperty reason_columnProperty() {
        return reason_column;
    }

    public void setReason_column(String reason_column) {
        this.reason_column.set(reason_column);
    }

    public String getInvoice_ref_column() {
        return invoice_ref_column.get();
    }

    public StringProperty invoice_ref_columnProperty() {
        return invoice_ref_column;
    }

    public void setInvoice_ref_column(String invoice_ref_column) {
        this.invoice_ref_column.set(invoice_ref_column);
    }

    public Refund getRefund() {
        return refund;
    }
}
