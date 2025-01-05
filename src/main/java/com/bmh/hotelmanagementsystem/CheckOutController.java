package com.bmh.hotelmanagementsystem;

import com.bmh.hotelmanagementsystem.BackendService.RestClient;
import com.bmh.hotelmanagementsystem.BackendService.entities.*;
import com.bmh.hotelmanagementsystem.BackendService.enums.RoomType;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CheckOutController extends Controller{

    private Stage primaryStage;
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    private ComboBox<Integer> rooms;

    @FXML
    private Label guest_name;
    @FXML
    private Label room_type;
    @FXML
    private Label room_price;
    @FXML
    private Label outstanding_payments;

    @FXML
    private Button generate_invoice;
    @FXML
    private Button check_out;

    private GuestLog guestLog;


    @FXML
    public void initialize() throws IOException {
        List<Room> listOfRooms;
        ObservableList<Integer> roomsData = FXCollections.observableArrayList();

        try {
            String response = RestClient.get("/room/status?roomStatus=occupied");

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());

            // Convert JSON string to ApiResponse
            ApiResponse<Room> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponse<Room>>() {});

            // Extract the list of rooms
            listOfRooms = apiResponse.getData();
            for (Room room : listOfRooms) {
                roomsData.add(room.getRoomNumber());
            }

        } catch (Exception e) {
            listOfRooms = null;
        }

        rooms.setItems(roomsData);
        rooms.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observableValue, Integer oldValue, Integer newValue) {
                try {
                    String response = RestClient.get("/guestLog/find?roomNumber=" +newValue);

                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.registerModule(new JavaTimeModule());

                    // Convert JSON string to ApiResponse
                    ApiResponseSingleData<GuestLog> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponseSingleData<GuestLog>>() {
                    });

                    guestLog = apiResponse.getData();

                    try {
                        RoomPrices roomPrice;
                        String responseRoomPrice = RestClient.get("/roomPrices/?roomType=" + guestLog.getRoom().getRoomType());
                        ApiResponseSingleData<RoomPrices> apiResponseRoomPrice = objectMapper.readValue(responseRoomPrice, new TypeReference<ApiResponseSingleData<RoomPrices>>() {
                        });

                        roomPrice = apiResponseRoomPrice.getData();
                        DecimalFormat formatter = new DecimalFormat("#,###.00");

                        String formattedPrice = formatter.format(roomPrice.getRoomPrice());
                        room_price.setText(formattedPrice);


                    } catch (Exception e) {
                        System.out.println(e);
                    }

                    guest_name.setText("Name:    " + guestLog.getGuestName());
                    room_type.setText("Room Type:    " + guestLog.getRoom().getRoomType());
                    outstanding_payments.setText("Outstanding Payment:    " + guestLog.getTotalAmountDue());

                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        });

        generate_invoice.setOnAction(event -> generateInvoice());
        check_out.setOnAction(event -> checkOut());
    }

    public void generateInvoice(){
        try {
            Utils utils = new Utils();
            utils.switchScreenWithGuestLog("/com/bmh/hotelmanagementsystem/invoice-view.fxml", primaryStage,guestLog);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void checkOut(){
        if(rooms != null){
            try {
                RoomPrices roomPrice;
                String response = RestClient.post("/guestLog/check-out?roomId=" + rooms.getSelectionModel().getSelectedItem(),"");

                ObjectMapper objectMapper = new ObjectMapper();
                ApiResponseSingleData<?> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponseSingleData<?>>() {
                });

                if(apiResponse.getResponseHeader().getResponseCode().equals("00")){
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Check-out Successful");
                    successAlert.setContentText("The check-out was successful.");
                    successAlert.showAndWait();

                    Utils utils = new Utils();
                    utils.switchScreen("/com/bmh/hotelmanagementsystem/home-view.fxml", primaryStage);
                }
                else{
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle(apiResponse.getResponseHeader().getResponseMessage());
                    errorAlert.setContentText(apiResponse.getError());
                    errorAlert.showAndWait();
                }


            } catch (Exception e) {
                System.out.println(e);
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Error");
                errorAlert.setContentText("Something went wrong. Please try again.");
                errorAlert.showAndWait();
            }
        }
        else {
            Alert warningAlert = new Alert(Alert.AlertType.WARNING);
            warningAlert.setTitle("Invalid Request");
            warningAlert.setContentText("Field can not be null.");
            warningAlert.showAndWait();
        }
    }


}
