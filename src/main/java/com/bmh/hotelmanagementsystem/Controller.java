package com.bmh.hotelmanagementsystem;

import com.bmh.hotelmanagementsystem.BackendService.entities.Room.GuestLog;
import javafx.stage.Stage;

import java.io.IOException;

public class Controller {

    private Stage primaryStage;
    private GuestLog guestLog;

    private String previousLocation;

    private Object data;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setGuestLog(GuestLog guestLog, String previousLocation) throws IOException {
        this.guestLog = guestLog;
        this.previousLocation = previousLocation;
    }

    public void setData(Object data, String previousLocation){
        this.data = data;
        this.previousLocation = previousLocation;
    }
}
