package com.bmh.hotelmanagementsystem.RoomManagement;

import com.bmh.hotelmanagementsystem.BackendService.RestClient;
import com.bmh.hotelmanagementsystem.BackendService.entities.ApiResponse;
import com.bmh.hotelmanagementsystem.BackendService.entities.ApiResponseSingleData;
import com.bmh.hotelmanagementsystem.BackendService.entities.Room.*;
import com.bmh.hotelmanagementsystem.BackendService.enums.RoomType;
import com.bmh.hotelmanagementsystem.Controller;
import com.bmh.hotelmanagementsystem.Utils;
import com.bmh.hotelmanagementsystem.dto.room.GuestReservation;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.bmh.hotelmanagementsystem.Utils.showLoadingScreen;

public class ChangeRoomController extends Controller {

    private Stage primaryStage;

    private String previousLocation;

    private GuestLog data;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setData(Object data, String previousLocation){
        this.data = (GuestLog) data;
        this.previousLocation = previousLocation;


        ObservableList<String> roomTypes = FXCollections.observableArrayList();

        for (RoomType roomType : RoomType.values()) {
            roomTypes.add(roomType.toJson());
        }

        ObservableList<Integer> roomsData = FXCollections.observableArrayList();

        room_type.setItems(roomTypes);

        ObservableList<Integer> listOfGuestLogRooms = FXCollections.observableArrayList();
        for (GuestLogRoom guestLogRoom : this.data.getGuestLogRooms()){
            listOfGuestLogRooms.add(guestLogRoom.getRoom().getRoomNumber());
        }

        old_room_comboBox.setItems(listOfGuestLogRooms);

        room_type.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

                List<Room> rooms;
                rooms = new ArrayList<>();

                Stage loadingStage = showLoadingScreen(primaryStage);

                Platform.runLater(() -> loadingStage.show());


                new Thread(() -> {

                    try {
                        String response = RestClient.get("/room/typeAndStatus?roomType=" + newValue + "&roomStatus=Available");

                        ObjectMapper objectMapper = new ObjectMapper();
                        objectMapper.registerModule(new JavaTimeModule());

                        // Convert JSON string to ApiResponse
                        ApiResponse<Room> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponse<Room>>() {
                        });

                        if (apiResponse.getResponseHeader().getResponseCode().equals("00")) {
                            Platform.runLater(() -> {
                                loadingStage.close();


                                ObservableList<Integer> listOfAllRooms = FXCollections.observableArrayList();

                                for (Room room : apiResponse.getData()) {
                                    listOfAllRooms.add(room.getRoomNumber());
                                }

                                new_room_comboBox.setItems(listOfAllRooms);

                            });
                        } else {
                            Platform.runLater(() -> {
                                loadingStage.close();
                                Utils.showAlertDialog(Alert.AlertType.ERROR, apiResponse.getResponseHeader().getResponseMessage(), apiResponse.getError());
                            });
                        }

                    } catch (Exception e) {
                        Platform.runLater(() -> {
                            loadingStage.close();
                            System.out.println(e);
                            Utils.showGeneralErrorDialog();
                        });
                    }
                }).start();
            }

        });
    }

    @FXML
    private ComboBox<Integer> old_room_comboBox;

    @FXML
    private ComboBox<Integer> new_room_comboBox;

    @FXML
    private Button change_room;

    @FXML
    private ComboBox<String> room_type;


    public void initialize() {

        change_room.setOnAction(event -> changeRoom());
    }

    public void changeRoom(){
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirm action");
        confirmationAlert.setHeaderText("Are you sure you want to change this room?");
        confirmationAlert.setContentText("Please confirm your action.");

        Optional<ButtonType> result = confirmationAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            Stage loadingStage = Utils.showLoadingScreen(primaryStage);
            Platform.runLater(() -> loadingStage.show());

            new Thread(() -> {
                try {

                    if (old_room_comboBox.getSelectionModel().getSelectedItem() == null || new_room_comboBox.getSelectionModel().getSelectedItem() == null){
                        Platform.runLater(() -> {
                            loadingStage.close();
                            Utils.showAlertDialog(Alert.AlertType.ERROR, "Invalid Request", "Invalid request, No room selected");
                        });
                        return;
                    }

                    int oldRoomNumber = old_room_comboBox.getSelectionModel().getSelectedItem();
                    int newRoomNumber = new_room_comboBox.getSelectionModel().getSelectedItem();


                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.registerModule(new JavaTimeModule());

                    String response = RestClient.post("/guestLog/changeRoom?oldRoomNumber=" +oldRoomNumber+ "&newRoomNumber=" + newRoomNumber,"");
                    ApiResponseSingleData<?> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponseSingleData<?>>() {
                    });

                    if (apiResponse.getResponseHeader().getResponseCode().equals("00")) {
                        Platform.runLater(() -> {
                            loadingStage.close();
                            Utils.showAlertDialog(Alert.AlertType.INFORMATION, "Updated Successfully", "Room has been changed successfully");
                            primaryStage.close();

                        });
                    } else {
                        Platform.runLater(() -> {
                            loadingStage.close();
                            Utils.showAlertDialog(Alert.AlertType.ERROR, apiResponse.getResponseHeader().getResponseMessage(), apiResponse.getError());
                        });

                    }

                } catch (Exception e) {
                    Platform.runLater(() -> {
                        loadingStage.close();
                        e.printStackTrace();
                        Utils.showGeneralErrorDialog();
                    });
                }
            }).start();
        }
        else {
            confirmationAlert.close();
        }

    }
}
