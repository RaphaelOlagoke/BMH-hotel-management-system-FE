package com.bmh.hotelmanagementsystem.dto.restaurant;

import com.bmh.hotelmanagementsystem.BackendService.entities.Restaurant.Order;
import com.bmh.hotelmanagementsystem.BackendService.enums.RestaurantOrderStatus;
import javafx.beans.property.*;

public class OrderTableRow {

    private final StringProperty customer_column;

    private final StringProperty ref_column;
    private final ObjectProperty<RestaurantOrderStatus> status_column;
    private final StringProperty date;

    private final Order order;

    public OrderTableRow(String customer_column, String ref_column, RestaurantOrderStatus status_column, String date, Order order) {
        this.customer_column = new SimpleStringProperty(customer_column);
        this.ref_column = new SimpleStringProperty(ref_column);
        this.status_column = new SimpleObjectProperty<>(status_column);
        this.date = new SimpleStringProperty(date);
        this.order = order;
    }

    public String getCustomer_column() {
        return customer_column.get();
    }

    public StringProperty customer_columnProperty() {
        return customer_column;
    }

    public String getRef_column() {
        return ref_column.get();
    }

    public StringProperty ref_columnProperty() {
        return ref_column;
    }

    public RestaurantOrderStatus getStatus_column() {
        return status_column.get();
    }

    public ObjectProperty<RestaurantOrderStatus> status_columnProperty() {
        return status_column;
    }

    public String getDate() {
        return date.get();
    }

    public StringProperty dateProperty() {
        return date;
    }

    public Order getOrder() {
        return order;
    }
}
