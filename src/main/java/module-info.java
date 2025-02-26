module com.example.bmhhotelmanagementsystem {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires java.net.http;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;

    opens com.bmh.hotelmanagementsystem to javafx.fxml;
    exports com.bmh.hotelmanagementsystem;
    exports com.bmh.hotelmanagementsystem.components;
    exports com.bmh.hotelmanagementsystem.BackendService.enums;
    exports com.bmh.hotelmanagementsystem.BackendService.entities;
    opens com.bmh.hotelmanagementsystem.components to javafx.fxml;
    exports com.bmh.hotelmanagementsystem.RoomManagement;
    opens com.bmh.hotelmanagementsystem.RoomManagement to javafx.fxml;
    opens com.bmh.hotelmanagementsystem.BackendService.entities to com.fasterxml.jackson.databind;
    opens com.bmh.hotelmanagementsystem.BackendService.enums to com.fasterxml.jackson.databind;
    exports com.bmh.hotelmanagementsystem.BackendService.entities.Restaurant;
    opens com.bmh.hotelmanagementsystem.BackendService.entities.Restaurant to com.fasterxml.jackson.databind;
    exports com.bmh.hotelmanagementsystem.BackendService.entities.Room;
    opens com.bmh.hotelmanagementsystem.BackendService.entities.Room to com.fasterxml.jackson.databind;
    exports com.bmh.hotelmanagementsystem.BackendService.entities.HouseKeeping;
    opens com.bmh.hotelmanagementsystem.BackendService.entities.HouseKeeping to com.fasterxml.jackson.databind;
    exports com.bmh.hotelmanagementsystem.BackendService.entities.Invoice;
    opens com.bmh.hotelmanagementsystem.BackendService.entities.Invoice to com.fasterxml.jackson.databind;
    exports com.bmh.hotelmanagementsystem.BackendService.entities.inventory;
    opens com.bmh.hotelmanagementsystem.BackendService.entities.inventory to com.fasterxml.jackson.databind;
    exports com.bmh.hotelmanagementsystem.restaurant;
    opens com.bmh.hotelmanagementsystem.restaurant to javafx.fxml;
    opens com.bmh.hotelmanagementsystem.invoice to javafx.fxml;
    opens com.bmh.hotelmanagementsystem.inventory to javafx.fxml;
    exports com.bmh.hotelmanagementsystem.invoice;
    exports com.bmh.hotelmanagementsystem.inventory;
    exports com.bmh.hotelmanagementsystem.HouseKeeping;
    opens com.bmh.hotelmanagementsystem.HouseKeeping to javafx.fxml;
}
