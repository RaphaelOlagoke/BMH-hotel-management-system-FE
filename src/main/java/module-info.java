module com.example.bmhhotelmanagementsystem {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    opens com.bmh.hotelmanagementsystem to javafx.fxml;
    exports com.bmh.hotelmanagementsystem;
    exports com.bmh.hotelmanagementsystem.components;
    opens com.bmh.hotelmanagementsystem.components to javafx.fxml;
    exports com.bmh.hotelmanagementsystem.RoomManagement;
    opens com.bmh.hotelmanagementsystem.RoomManagement to javafx.fxml;
}