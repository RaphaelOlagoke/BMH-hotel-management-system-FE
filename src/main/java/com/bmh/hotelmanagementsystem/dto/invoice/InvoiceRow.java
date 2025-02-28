package com.bmh.hotelmanagementsystem.dto.invoice;

import com.bmh.hotelmanagementsystem.BackendService.entities.Invoice.Invoice;
import com.bmh.hotelmanagementsystem.BackendService.enums.PaymentMethod;
import com.bmh.hotelmanagementsystem.BackendService.enums.PaymentStatus;
import com.bmh.hotelmanagementsystem.BackendService.enums.ServiceType;
import javafx.beans.property.*;

public class InvoiceRow {

    private final StringProperty ref_column;
    private final StringProperty issue_date_column;
    private final StringProperty payment_date_column;
    private final ObjectProperty<PaymentMethod> payment_method_column;
    private final ObjectProperty<PaymentStatus> payment_status_column;
    private final ObjectProperty<ServiceType> service_column;
    private final StringProperty service_details_column;
    private final StringProperty total_amount_column;
    private final StringProperty amount_paid_column;
    private final StringProperty discount_amount_column;

    private final Invoice invoice;


    public InvoiceRow(String ref_column, String issue_date_column, String payment_date_column,
                      PaymentMethod payment_method_column, PaymentStatus payment_status_column,
                      ServiceType service_column, String service_details_column,String total_amount_column,
                      String amount_paid_column, String discount_amount_column, Invoice invoice) {
        this.ref_column = new SimpleStringProperty(ref_column);
        this.issue_date_column = new SimpleStringProperty(issue_date_column);
        this.payment_date_column = new SimpleStringProperty(payment_date_column);
        this.payment_method_column = new SimpleObjectProperty<>(payment_method_column);
        this.payment_status_column = new SimpleObjectProperty<>(payment_status_column);
        this.service_column = new SimpleObjectProperty<>(service_column);
        this.service_details_column = new SimpleStringProperty(service_details_column);
        this.total_amount_column = new SimpleStringProperty(total_amount_column);
        this.amount_paid_column = new SimpleStringProperty(amount_paid_column);
        this.discount_amount_column = new SimpleStringProperty(discount_amount_column);
        this.invoice = invoice;
    }

    public String getRef_column() {
        return ref_column.get();
    }

    public StringProperty ref_columnProperty() {
        return ref_column;
    }

    public String getIssue_date_column() {
        return issue_date_column.get();
    }

    public StringProperty issue_date_columnProperty() {
        return issue_date_column;
    }

    public String getPayment_date_column() {
        return payment_date_column.get();
    }

    public StringProperty payment_date_columnProperty() {
        return payment_date_column;
    }

    public PaymentMethod getPayment_method_column() {
        return payment_method_column.get();
    }

    public ObjectProperty<PaymentMethod> payment_method_columnProperty() {
        return payment_method_column;
    }

    public PaymentStatus getPayment_status_column() {
        return payment_status_column.get();
    }

    public ObjectProperty<PaymentStatus> payment_status_columnProperty() {
        return payment_status_column;
    }

    public ServiceType getService_column() {
        return service_column.get();
    }

    public ObjectProperty<ServiceType> service_columnProperty() {
        return service_column;
    }

    public String getService_details_column() {
        return service_details_column.get();
    }

    public StringProperty service_details_columnProperty() {
        return service_details_column;
    }

    public String getTotal_amount_column() {
        return total_amount_column.get();
    }

    public StringProperty total_amount_columnProperty() {
        return total_amount_column;
    }

    public String getAmount_paid_column() {
        return amount_paid_column.get();
    }

    public StringProperty amount_paid_columnProperty() {
        return amount_paid_column;
    }

    public String getDiscount_amount_column() {
        return discount_amount_column.get();
    }

    public StringProperty discount_amount_columnProperty() {
        return discount_amount_column;
    }

    public Invoice getInvoice() {
        return invoice;
    }
}
