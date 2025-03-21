package com.bmh.hotelmanagementsystem.RoomManagement;

import com.bmh.hotelmanagementsystem.BackendService.RestClient;
import com.bmh.hotelmanagementsystem.BackendService.entities.*;
import com.bmh.hotelmanagementsystem.BackendService.entities.Room.CheckIn;
import com.bmh.hotelmanagementsystem.BackendService.entities.Room.Room;
import com.bmh.hotelmanagementsystem.BackendService.entities.Room.RoomPrices;
import com.bmh.hotelmanagementsystem.BackendService.enums.ID_TYPE;
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
import javafx.scene.input.KeyCode;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static com.bmh.hotelmanagementsystem.Utils.showLoadingScreen;

public class CheckInController extends Controller {

    private Stage primaryStage;


    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    private Button checkIn;

    @FXML
    private TextField guest_name;

    @FXML
    private ComboBox<String> room_type;

    @FXML
    private ComboBox<Integer> room;

    @FXML
    private ComboBox<String> id_type;

    @FXML
    private Label price;
    @FXML
    private Label total_price;

    @FXML
    private Label rooms_message;

    @FXML
    private TextField no_of_days;

    @FXML
    private TextField id_ref;

    @FXML
    private TextField phone_number;

    @FXML
    private TextField next_of_kin_name;

    @FXML
    private TextField next_of_kin_number;

    @FXML
    private FlowPane rooms_flowPane;

    CheckIn checkInData = new CheckIn();

    private List<Integer> selectedRooms = new ArrayList<>();

    private Double currentPrice = 0.0;
    private List<SelectedRoom> selectedRoomList = new ArrayList<>();

    private Double total;
    private Double totalWithoutAdditionalCharges;

    private Double tax = 0.0;
    private Double vat = 0.0;

    @FXML
    public void initialize() throws IOException {

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


        no_of_days.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                updatePrice();
            }
        });

        ObservableList<String> roomTypes = FXCollections.observableArrayList();

        for (RoomType roomType : RoomType.values()) {
            roomTypes.add(roomType.toJson());
        }

        ObservableList<Integer> roomsData = FXCollections.observableArrayList();

        room_type.setItems(roomTypes);

        ObservableList<String> idTypes = FXCollections.observableArrayList();

        for (ID_TYPE idType : ID_TYPE.values()) {
            idTypes.add(idType.toJson());
        }

        id_type.setItems(idTypes);

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
                        try {
                             noOfDays = Integer.parseInt(no_of_days.getText());
                        }
                        catch (Exception e){
                             noOfDays = 1;
                        }
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


    }

    public void updatePrice(){
        int noOfDays = 0;
        try{
            noOfDays = Integer.parseInt(no_of_days.getText());
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
        try {
            noOfDays = Integer.parseInt(no_of_days.getText());
        }
        catch (Exception e){
            noOfDays = 1;
        }
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


    public void checkIn(){
        try {
            try{
                checkInData.setNoOfDays(Integer.parseInt(no_of_days.getText()));
                if (checkInData.getNoOfDays() > 30 || checkInData.getNoOfDays() < 1){
                    Utils.showAlertDialog(Alert.AlertType.INFORMATION, "Invalid request", "No of days must be in the range of 1-30");
                    return;
                }
            }
            catch (Exception e){
                Utils.showAlertDialog(Alert.AlertType.INFORMATION, "Invalid request", "Invalid number of days");
                return;
            }
            checkInData.setGuestName(guest_name.getText());
            checkInData.setSelectedRooms(selectedRoomList != null && !selectedRoomList.isEmpty() ? selectedRoomList : null);
            checkInData.setTotalPrice(total);
            checkInData.setTotalPriceWithoutAdditionalCharges(totalWithoutAdditionalCharges);
            checkInData.setIdType(id_type.getSelectionModel().getSelectedItem() != null ? ID_TYPE.valueOf(id_type.getSelectionModel().getSelectedItem()) : null);
            checkInData.setIdRef(id_ref.getText());
            checkInData.setPhoneNumber(phone_number.getText());
            checkInData.setNextOfKinName(next_of_kin_name.getText());
            checkInData.setNextOfKinNumber(next_of_kin_number.getText());
//            checkInData.setRoomNumber(room.getSelectionModel().getSelectedItem() != null? room.getSelectionModel().getSelectedItem() : 0);
//            checkInData.setRoomNumbers(selectedRooms != null && !selectedRooms.isEmpty()? selectedRooms : null);
            if (checkInData.getGuestName() == null || checkInData.getSelectedRooms() == null){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Invalid request");
//                alert.setHeaderText("This is a header");
                alert.setContentText("Fields can not be empty");

                // Show the popup
                alert.showAndWait();
            }
            else{
                Utils utils = new Utils();
                utils.switchScreenWithCheckInData("/com/bmh/hotelmanagementsystem/room/check-in-invoice-view.fxml", primaryStage, checkInData);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Sever Error");
            errorAlert.setContentText("Something went wrong");
            errorAlert.showAndWait();
        }
    }
}
