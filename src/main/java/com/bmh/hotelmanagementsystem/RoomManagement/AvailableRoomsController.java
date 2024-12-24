package com.bmh.hotelmanagementsystem.RoomManagement;

import com.bmh.hotelmanagementsystem.Controller;
import com.bmh.hotelmanagementsystem.Utils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class AvailableRoomsController extends Controller {

    private Stage primaryStage;


    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    private FlowPane available_rooms;

    @FXML
    public void initialize(){
        List<String> itemsFromDatabase = List.of("Item 1", "Item 2", "Item 3");

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
            available_rooms.getChildren().add(button);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onButtonClick(String item){
        try {
            Utils utils = new Utils();
            utils.switchScreen("/com/bmh/hotelmanagementsystem/room/singleRoom-view.fxml", primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
