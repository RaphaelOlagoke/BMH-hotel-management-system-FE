package com.bmh.hotelmanagementsystem.RoomManagement;

import com.bmh.hotelmanagementsystem.BackendService.RestClient;
import com.bmh.hotelmanagementsystem.BackendService.entities.AdditionalChargesSummary;
import com.bmh.hotelmanagementsystem.BackendService.entities.ApiResponse;
import com.bmh.hotelmanagementsystem.BackendService.entities.ApiResponseSingleData;
import com.bmh.hotelmanagementsystem.BackendService.entities.Room.GuestLog;
import com.bmh.hotelmanagementsystem.BackendService.entities.Room.GuestLogRoom;
import com.bmh.hotelmanagementsystem.BackendService.entities.Room.Room;
import com.bmh.hotelmanagementsystem.BackendService.entities.Room.RoomPrices;
import com.bmh.hotelmanagementsystem.BackendService.enums.GuestLogStatus;
import com.bmh.hotelmanagementsystem.BackendService.enums.RoomType;
import com.bmh.hotelmanagementsystem.Controller;
import com.bmh.hotelmanagementsystem.Utils;
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

public class AddRoomController extends Controller {

    private Stage primaryStage;

    private String previousLocation;

    private GuestLog data;

    private Double tax = 0.0;
    private Double vat = 0.0;

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
                                new_room_comboBox.getItems().clear();
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

                    try {
                        String response = RestClient.get("/roomPrices/?roomType=" + newValue);

                        ObjectMapper objectMapper = new ObjectMapper();

                        // Convert JSON string to ApiResponse
                        ApiResponseSingleData<RoomPrices> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponseSingleData<RoomPrices>>() {
                        });

                        Platform.runLater(() -> {
                            loadingStage.close();
                            RoomPrices roomPrice;
                            roomPrice = apiResponse.getData();
//                    checkInData.setRoomPrice(roomPrice.getRoomPrice());
                            DecimalFormat formatter = new DecimalFormat("#,###.00");

                            String formattedPrice = formatter.format(roomPrice.getRoomPrice() + ((vat/100) * roomPrice.getRoomPrice()) + tax);
                            price.setText("₦" + formattedPrice);
                        });


                    } catch (Exception e) {
                        Platform.runLater(() -> {
                            loadingStage.close();
                            System.out.println(e);
                            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                            errorAlert.setTitle("Sever Error");
                            errorAlert.setContentText("Something went wrong");
                            errorAlert.showAndWait();
                        });
                    }

                }).start();
            }

        });
    }

    @FXML
    private ComboBox<Integer> new_room_comboBox;

    @FXML
    private Button add_room;

    @FXML
    private ComboBox<String> room_type;

    @FXML
    private Label price;

    @FXML
    private Label rooms_message;


    public void initialize() {

        Stage loadingStage = showLoadingScreen(primaryStage);

        Platform.runLater(() -> loadingStage.show());

        new Thread(() -> {
            try {
                String response = RestClient.get("/additionalCharge/");

                ObjectMapper objectMapper = new ObjectMapper();

                ApiResponseSingleData<AdditionalChargesSummary> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponseSingleData<AdditionalChargesSummary>>() {
                });

                if (apiResponse.getResponseHeader().getResponseCode().equals("00")) {
                    Platform.runLater(() -> {
                        loadingStage.close();
                        tax = apiResponse.getData().getTaxPrice();
                        vat = apiResponse.getData().getVatPrice();
                    });
                }

            } catch (Exception e) {
                Platform.runLater(() -> {
                    System.out.println(e);
                    loadingStage.close();
                    Utils.showGeneralErrorDialog();
                });
            }
        }).start();

        add_room.setOnAction(event -> addRoom());

    }


    public void addRoom(){
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirm action");
        confirmationAlert.setHeaderText("Are you sure you want to add this room?");
        confirmationAlert.setContentText("Please confirm your action.");

        Optional<ButtonType> result = confirmationAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            Stage loadingStage = Utils.showLoadingScreen(primaryStage);
            Platform.runLater(() -> loadingStage.show());


            new Thread(() -> {

                try {

                    if(new_room_comboBox.getSelectionModel().getSelectedItem() == null){
                        Platform.runLater(() -> {
                            loadingStage.close();
                            Utils.showAlertDialog(Alert.AlertType.INFORMATION, "Invalid Request", "No room Selected");
                        });
                        return;
                    }

                    int currentRoom = this.data.getGuestLogRooms().get(0).getRoom().getRoomNumber();
                    int newRoom = new_room_comboBox.getSelectionModel().getSelectedItem();


                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.registerModule(new JavaTimeModule());

                    String response = RestClient.post("/guestLog/addRoom?currentRoom=" +currentRoom+ "&newRoom=" + newRoom,"");
                    ApiResponseSingleData<?> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponseSingleData<?>>() {
                    });

                    if (apiResponse.getResponseHeader().getResponseCode().equals("00")) {
                        Platform.runLater(() -> {
                            loadingStage.close();
                            Utils.showAlertDialog(Alert.AlertType.INFORMATION, "Updated Successfully", "Room has been added successfully");
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
