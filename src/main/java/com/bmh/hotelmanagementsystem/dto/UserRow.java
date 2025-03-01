package com.bmh.hotelmanagementsystem.dto;

import com.bmh.hotelmanagementsystem.BackendService.entities.User;
import com.bmh.hotelmanagementsystem.BackendService.enums.GuestLogStatus;
import com.bmh.hotelmanagementsystem.BackendService.enums.PaymentStatus;
import javafx.beans.property.*;

public class UserRow {

    private final StringProperty email_column;
    private final StringProperty username_column;
//    private final StringProperty password_column;
    private final StringProperty role_column;
    private final BooleanProperty isEnabled_column;
    private final StringProperty access_column;
    private final StringProperty created_by_column;
    private final StringProperty created_date_column;
    private final StringProperty modified_by_column;
    private final StringProperty modified_date_column;

    private final User user;

    public UserRow(String email_column, String username_column,
                   String role_column, boolean isEnabled_column, String access_column,
                   String created_by_column, String created_date_column, String modified_by_column,
                   String modified_date_column, User user) {
        this.email_column = new SimpleStringProperty(email_column);
        this.username_column = new SimpleStringProperty(username_column);
//        this.password_column = new SimpleStringProperty(password_column);
        this.role_column = new SimpleStringProperty(role_column);
        this.isEnabled_column = new SimpleBooleanProperty(isEnabled_column);
        this.access_column = new SimpleStringProperty(access_column);
        this.created_by_column = new SimpleStringProperty(created_by_column);
        this.created_date_column = new SimpleStringProperty(created_date_column);
        this.modified_by_column = new SimpleStringProperty(modified_by_column);
        this.modified_date_column = new SimpleStringProperty(modified_date_column);
        this.user = user;
    }

    public String getEmail_column() {
        return email_column.get();
    }

    public StringProperty email_columnProperty() {
        return email_column;
    }

    public String getUsername_column() {
        return username_column.get();
    }

    public StringProperty username_columnProperty() {
        return username_column;
    }

//    public String getPassword_column() {
//        return password_column.get();
//    }
//
//    public StringProperty password_columnProperty() {
//        return password_column;
//    }

    public String getRole_column() {
        return role_column.get();
    }

    public StringProperty role_columnProperty() {
        return role_column;
    }

    public boolean isIsEnabled_column() {
        return isEnabled_column.get();
    }

    public BooleanProperty isEnabled_columnProperty() {
        return isEnabled_column;
    }

    public String getAccess_column() {
        return access_column.get();
    }

    public StringProperty access_columnProperty() {
        return access_column;
    }

    public String getCreated_by_column() {
        return created_by_column.get();
    }

    public StringProperty created_by_columnProperty() {
        return created_by_column;
    }

    public String getCreated_date_column() {
        return created_date_column.get();
    }

    public StringProperty created_date_columnProperty() {
        return created_date_column;
    }

    public String getModified_by_column() {
        return modified_by_column.get();
    }

    public StringProperty modified_by_columnProperty() {
        return modified_by_column;
    }

    public String getModified_date_column() {
        return modified_date_column.get();
    }

    public StringProperty modified_date_columnProperty() {
        return modified_date_column;
    }

    public User getUser() {
        return user;
    }
}
