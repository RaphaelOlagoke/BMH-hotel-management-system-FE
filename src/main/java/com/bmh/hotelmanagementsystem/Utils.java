package com.bmh.hotelmanagementsystem;

import com.bmh.hotelmanagementsystem.BackendService.entities.DTO;
import com.bmh.hotelmanagementsystem.BackendService.entities.Room;
import com.bmh.hotelmanagementsystem.RoomManagement.SingleRoomController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;

public class Utils {


    @FXML
    public void setControllerPrimaryStage(FXMLLoader fxmlLoader, Stage primaryStage){
        Controller controller = fxmlLoader.getController();
        controller.setPrimaryStage(primaryStage);
    }

    @FXML
    public void switchScreen(String view, Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(view));
        Scene scene = new Scene(loader.load());
        primaryStage.setScene(scene);
        setControllerPrimaryStage(loader, primaryStage);
    }

    @FXML
    public void switchScreenWithRoom(String view, Stage primaryStage, Room room) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(view));
        Scene scene = new Scene(loader.load());

        SingleRoomController singleRoomController = loader.getController();
        singleRoomController.setRoom(room);

        primaryStage.setScene(scene);
        setControllerPrimaryStage(loader, primaryStage);
    }

}
