package com.bmh.hotelmanagementsystem.dto.discount;

import com.bmh.hotelmanagementsystem.BackendService.entities.discount.Discount;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class DiscountRow {

    private final StringProperty discount_code_column;
    private final StringProperty discount_percentage_column;
    private final StringProperty start_date_column;
    private final StringProperty end_date_column;
    private final BooleanProperty is_active_column;
    private final BooleanProperty is_one_time_use_column;
    private final Discount discount;

    public DiscountRow(String discount_code_column, String discount_percentage_column, String
            start_date_column, String end_date_column, boolean is_active_column,
                       boolean is_one_time_use_column, Discount discount) {
        this.discount_code_column = new SimpleStringProperty(discount_code_column);
        this.discount_percentage_column = new SimpleStringProperty(discount_percentage_column);
        this.start_date_column = new SimpleStringProperty(start_date_column);
        this.end_date_column = new SimpleStringProperty(end_date_column);
        this.is_active_column = new SimpleBooleanProperty(is_active_column);
        this.is_one_time_use_column = new SimpleBooleanProperty(is_one_time_use_column);
        this.discount = discount;
    }

    public String getDiscount_code_column() {
        return discount_code_column.get();
    }

    public StringProperty discount_code_columnProperty() {
        return discount_code_column;
    }

    public String getDiscount_percentage_column() {
        return discount_percentage_column.get();
    }

    public StringProperty discount_percentage_columnProperty() {
        return discount_percentage_column;
    }

    public String getStart_date_column() {
        return start_date_column.get();
    }

    public StringProperty start_date_columnProperty() {
        return start_date_column;
    }

    public String getEnd_date_column() {
        return end_date_column.get();
    }

    public StringProperty end_date_columnProperty() {
        return end_date_column;
    }

    public boolean isIs_active_column() {
        return is_active_column.get();
    }

    public BooleanProperty is_active_columnProperty() {
        return is_active_column;
    }

    public boolean isIs_one_time_use_column() {
        return is_one_time_use_column.get();
    }

    public BooleanProperty is_one_time_use_columnProperty() {
        return is_one_time_use_column;
    }

    public Discount getDiscount() {
        return discount;
    }
}
