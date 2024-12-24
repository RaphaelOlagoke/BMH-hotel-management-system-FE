package com.bmh.hotelmanagementsystem.RoomManagement;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;

import java.io.IOException;
import java.util.List;

public class MaintenanceRoomsController {

    @FXML
    private FlowPane maintenance_rooms;

    @FXML
    public void initialize(){
        List<String> itemsFromDatabase = List.of("Maintenance Item 1", "Item 2", "Item 3");

        // Iterate over the data and create a button for each item
        for (String item : itemsFromDatabase) {
            addButtonToContainer(item);
        }
    }

    private void addButtonToContainer(String item) {
        try {
            FXMLLoader buttonLoader = new FXMLLoader(getClass().getResource("/com/bmh/hotelmanagementsystem/components/room_button.fxml"));
            Button button = buttonLoader.load();

            AnchorPane graphic = (AnchorPane) button.getGraphic();

            Label roomNumberLabel = (Label) graphic.lookup("#room_number");
            Label roomTypeLabel = (Label) graphic.lookup("#room_type");

            roomNumberLabel.setText(item);

            // Set an action for the button
            button.setOnAction(event -> onButtonClick(item));

            // Add the button to the FlowPane container
            maintenance_rooms.getChildren().add(button);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onButtonClick(String item){
        System.out.println(item);
    }
}
