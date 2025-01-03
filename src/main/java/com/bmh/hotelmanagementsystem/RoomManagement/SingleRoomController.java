package com.bmh.hotelmanagementsystem.RoomManagement;

import com.bmh.hotelmanagementsystem.BackendService.RestClient;
import com.bmh.hotelmanagementsystem.BackendService.entities.ApiResponse;
import com.bmh.hotelmanagementsystem.BackendService.entities.Room;
import com.bmh.hotelmanagementsystem.Controller;
import com.bmh.hotelmanagementsystem.Utils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SingleRoomController extends Controller {

    private Stage primaryStage;

    @FXML
    private Button back;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    public void initialize() throws IOException {

        back.setOnAction(event -> goBack());
    }




    public void goBack(){
        try {
            Utils utils = new Utils();
            utils.switchScreen("/com/bmh/hotelmanagementsystem/room-view.fxml", primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


