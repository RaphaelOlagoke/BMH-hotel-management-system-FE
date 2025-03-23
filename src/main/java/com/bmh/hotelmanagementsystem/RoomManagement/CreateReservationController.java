package com.bmh.hotelmanagementsystem.RoomManagement;

import com.bmh.hotelmanagementsystem.BackendService.RestClient;
import com.bmh.hotelmanagementsystem.BackendService.entities.AdditionalChargesSummary;
import com.bmh.hotelmanagementsystem.BackendService.entities.ApiResponse;
import com.bmh.hotelmanagementsystem.BackendService.entities.ApiResponseSingleData;
import com.bmh.hotelmanagementsystem.BackendService.entities.Restaurant.CreateOrderRequest;
import com.bmh.hotelmanagementsystem.BackendService.entities.Restaurant.Order;
import com.bmh.hotelmanagementsystem.BackendService.entities.Room.*;
import com.bmh.hotelmanagementsystem.BackendService.enums.RoomStatus;
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
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.bmh.hotelmanagementsystem.Utils.showLoadingScreen;

public class CreateReservationController extends Controller {

    private Stage primaryStage;

    private String previousLocation;

    private Object data;

    @FXML
    private TextField guest_name;

    @FXML
    private ComboBox<String> room_type;

    @FXML
    private ComboBox<Integer> room;

    @FXML
    private Label price;
    @FXML
    private Label total_price;

    @FXML
    private Label rooms_message;

    @FXML
    private TextField phone_number;

    @FXML
    private FlowPane rooms_flowPane;

    @FXML
    private Button create;

    private List<SelectedRoom> selectedRoomList = new ArrayList<>();
    private List<Integer> selectedRooms = new ArrayList<>();

    private Double currentPrice = 0.0;

    private Double total;
    private Double totalWithoutAdditionalCharges;

    private Double tax = 0.0;
    private Double vat = 0.0;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setData(Object data, String previousLocation){
        this.data = data;
        this.previousLocation = previousLocation;
    }

    public void initialize() {
//        ObservableList<String> roomStatus = FXCollections.observableArrayList();
//
//        roomStatus.add(null);
//        for (RoomStatus status : RoomStatus.values()) {
//            roomStatus.add(status.toJson());
//        }
//
//        room_status.setItems(roomStatus);

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


        ObservableList<String> roomType = FXCollections.observableArrayList();

        for (RoomType type : RoomType.values()) {
            roomType.add(type.toJson());
        }

        room_type.setItems(roomType);

        ObservableList<Integer> roomsData = FXCollections.observableArrayList();

        room_type.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

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

                        Platform.runLater(() -> {
                            List<Room> rooms;
                            rooms = new ArrayList<>();

                            roomsData.clear();

                            // Extract the list of rooms
                            rooms = apiResponse.getData();
//                    checkInData.setRoomType(RoomType.valueOf(newValue));
                            if (rooms != null) {
                                for (Room room : rooms) {
                                    roomsData.add(room.getRoomNumber());
                                }
                                room.setItems(roomsData);
                                rooms_message.setText("");
                            } else {
                                rooms_message.setText("No rooms available");
                            }
                        });

                    } catch (Exception e) {
                        Platform.runLater(() -> {
                            loadingStage.close();
                            System.out.println(e);
                            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                            errorAlert.setTitle("Sever Error");
                            errorAlert.setContentText("Something went wrong");
                            errorAlert.showAndWait();
//                            rooms = new ArrayList<>();
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

                            currentPrice = roomPrice.getRoomPrice();
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

        room.setItems(roomsData);

        room.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                FXMLLoader hboxLoader = new FXMLLoader(getClass().getResource("/com/bmh/hotelmanagementsystem/room/add_room.fxml"));
                try {
                    if (newValue != null && !selectedRooms.contains(newValue)) {
                        HBox hBox = hboxLoader.load();
                        Label roomNumberLabel = (Label) hBox.lookup("#roomNumber");
                        Button cancelButton = (Button) hBox.lookup("#cancel");

                        SelectedRoom selectedRoom = new SelectedRoom();
                        selectedRoom.setRoomNumber(newValue);
                        selectedRoom.setRoomPrice(currentPrice);
                        selectedRoom.setRoomType(RoomType.valueOf(room_type.getSelectionModel().getSelectedItem()));

                        roomNumberLabel.setText(newValue.toString());
                        cancelButton.setOnAction(event -> removeRoom(newValue,hBox, selectedRoom));

                        rooms_flowPane.getChildren().add(hBox);
                        selectedRooms.add(newValue);

                        selectedRoomList.add(selectedRoom);

                        total = 0.0;
                        for (SelectedRoom item : selectedRoomList){
                            total += item.getRoomPrice() + tax + ((vat/100) * item.getRoomPrice());
                        }

                        totalWithoutAdditionalCharges = 0.0;
                        for (SelectedRoom item : selectedRoomList){
                            totalWithoutAdditionalCharges += item.getRoomPrice();
                        }


                        int noOfDays = 1;
//                        try {
//                            noOfDays = Integer.parseInt(no_of_days.getText());
//                        }
//                        catch (Exception e){
//                            noOfDays = 1;
//                        }
                        total = total * noOfDays;
                        totalWithoutAdditionalCharges = totalWithoutAdditionalCharges * noOfDays;

                        DecimalFormat formatter = new DecimalFormat("#,###.00");

                        String formattedPrice = formatter.format(total);
                        total_price.setText("₦"+formattedPrice);

                        updatePrice();

                    }

                } catch (IOException e) {
                    Utils.showGeneralErrorDialog();
                }
            }
        });

        create.setOnAction(event -> createReservation());
    }

    public void updatePrice(){
        int noOfDays = 1;
        try{
//            noOfDays = Integer.parseInt(no_of_days.getText());
            total = 0.0;
            for (SelectedRoom item : selectedRoomList){
                total += item.getRoomPrice() + tax + ((vat/100) * item.getRoomPrice());
            }

            totalWithoutAdditionalCharges = 0.0;
            for (SelectedRoom item : selectedRoomList){
                totalWithoutAdditionalCharges += item.getRoomPrice();
            }

            totalWithoutAdditionalCharges = totalWithoutAdditionalCharges * noOfDays;

            total = total * noOfDays;

            DecimalFormat formatter = new DecimalFormat("#,###.00");

            String formattedPrice = formatter.format(total);
            total_price.setText("₦"+formattedPrice);
        }
        catch (Exception e){
            Utils.showAlertDialog(Alert.AlertType.INFORMATION, "Invalid request", "No of days must be in the range of 1-30");
        }
    }

    public void removeRoom(Integer roomNumber,HBox hBox, SelectedRoom selectedRoom){
        rooms_flowPane.getChildren().remove(hBox);
        selectedRooms.remove(roomNumber);
        selectedRoomList.remove(selectedRoom);

        int noOfDays = 1;
//        try {
//            noOfDays = Integer.parseInt(no_of_days.getText());
//        }
//        catch (Exception e){
//            noOfDays = 1;
//        }
        total = 0.0;
        for (SelectedRoom item : selectedRoomList){
            total += item.getRoomPrice() + tax + ((vat/100) * item.getRoomPrice());
        }

        totalWithoutAdditionalCharges = 0.0;
        for (SelectedRoom item : selectedRoomList){
            totalWithoutAdditionalCharges += item.getRoomPrice();
        }

        totalWithoutAdditionalCharges = totalWithoutAdditionalCharges * noOfDays;

        total = total * noOfDays;
        DecimalFormat formatter = new DecimalFormat("#,###.00");

        String formattedPrice = formatter.format(total);
        total_price.setText(formattedPrice);

        room.getSelectionModel().clearSelection();
    }

    public void createReservation(){
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirm action");
        confirmationAlert.setHeaderText("Are you sure you want to create this reservation?");
        confirmationAlert.setContentText("Please confirm your action.");

        Optional<ButtonType> result = confirmationAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            Stage loadingStage = Utils.showLoadingScreen(primaryStage);
            Platform.runLater(() -> loadingStage.show());

            new Thread(() -> {
                try {

                    if (guest_name.getText() == null || guest_name.getText().equals("")){
                        Utils.showAlertDialog(Alert.AlertType.INFORMATION, "Invalid Request", "Guest name cannot be null");
                    }

                    if (phone_number.getText() == null || phone_number.getText().equals("")){
                        Utils.showAlertDialog(Alert.AlertType.INFORMATION, "Invalid Request", "Guest Phone number cannot be null");
                    }

                    if (selectedRoomList == null || selectedRoomList.isEmpty()){
                        Utils.showAlertDialog(Alert.AlertType.INFORMATION, "Invalid Request", "No room selected");
                    }

                    List<Integer> roomNumbers = new ArrayList<>();

                    StringBuilder rooms = new StringBuilder();
                    StringBuilder types = new StringBuilder();

                    int size = selectedRoomList.size();
                    int index = 0;

                    for (SelectedRoom selectedRoom : selectedRoomList) {
                        rooms.append(selectedRoom.getRoomNumber());
                        types.append(selectedRoom.getRoomType());
                        roomNumbers.add(selectedRoom.getRoomNumber());
                        if (index < size - 1) {
                            rooms.append(", ");
                            types.append(", ");
                        }
                        index++;
                    }

                    CreateReservationRequest request = new CreateReservationRequest();
                    request.setGuestName(guest_name.getText());
                    request.setRoomNumbers(roomNumbers);
                    request.setPhoneNumber(phone_number.getText());

                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.registerModule(new JavaTimeModule());

                    String jsonString = objectMapper.writeValueAsString(request);

                    String response = RestClient.post("/reservation/", jsonString);
                    ApiResponseSingleData<Reservation> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponseSingleData<Reservation>>() {
                    });

                    if (apiResponse.getResponseHeader().getResponseCode().equals("00")) {
                        Platform.runLater(() -> {
                            loadingStage.close();
                            Utils.showAlertDialog(Alert.AlertType.INFORMATION, "Created Successfully", "reservation created successfully");
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
