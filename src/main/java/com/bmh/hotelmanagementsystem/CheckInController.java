package com.bmh.hotelmanagementsystem;

import com.bmh.hotelmanagementsystem.BackendService.RestClient;
import com.bmh.hotelmanagementsystem.BackendService.entities.*;
import com.bmh.hotelmanagementsystem.BackendService.enums.PaymentMethod;
import com.bmh.hotelmanagementsystem.BackendService.enums.RoomType;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CheckInController extends Controller{

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
    private ComboBox<String> payment_method;

    @FXML
    private Label price;

    @FXML
    private Label rooms_message;

    CheckIn checkInData = new CheckIn();

    @FXML
    public void initialize() throws IOException {

        ObservableList<String> payment_Methods = FXCollections.observableArrayList();

//        for (PaymentMethod paymentMethod : PaymentMethod.values()) {
//            payment_Methods.add(paymentMethod.toJson());
//        }
        payment_Methods.add(PaymentMethod.CARD.toJson());
        payment_Methods.add(PaymentMethod.TRANSFER.toJson());
        payment_Methods.add(PaymentMethod.CASH.toJson());

        payment_method.setItems(payment_Methods);


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
                try {
                    String response = RestClient.get("/room/typeAndStatus?roomType=" +newValue+ "&roomStatus=Available");

                    ObjectMapper objectMapper = new ObjectMapper();

                    // Convert JSON string to ApiResponse
                    ApiResponse<Room> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponse<Room>>() {
                    });

                    roomsData.clear();

                    // Extract the list of rooms
                    rooms = apiResponse.getData();
                    checkInData.setRoomType(RoomType.valueOf(newValue));
                    if (rooms != null){
                        for (Room room : rooms) {
                            roomsData.add(room.getRoomNumber());
                        }
                        rooms_message.setText("");
                    }
                    else{
                        rooms_message.setText("No rooms available");
                    }

                } catch (Exception e) {
                    System.out.println(e);
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Sever Error");
                    errorAlert.setContentText("Something went wrong");
                    errorAlert.showAndWait();
                    rooms = new ArrayList<>();
                }
                try {
                    RoomPrices roomPrice;
                    String response = RestClient.get("/roomPrices/?roomType=" + newValue);

                    ObjectMapper objectMapper = new ObjectMapper();

                    // Convert JSON string to ApiResponse
                    ApiResponseSingleData<RoomPrices> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponseSingleData<RoomPrices>>() {
                    });

                    roomPrice = apiResponse.getData();
                    checkInData.setRoomPrice(roomPrice.getRoomPrice());
                    DecimalFormat formatter = new DecimalFormat("#,###.00");

                    String formattedPrice = formatter.format(roomPrice.getRoomPrice());
                    price.setText(formattedPrice);


                } catch (Exception e) {
                    System.out.println(e);
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Sever Error");
                    errorAlert.setContentText("Something went wrong");
                    errorAlert.showAndWait();
                }
            }
        });

        room.setItems(roomsData);


    }


    public void checkIn(){
        try {
            checkInData.setPaymentMethod(payment_method.getSelectionModel().getSelectedItem() != null ? PaymentMethod.valueOf(payment_method.getSelectionModel().getSelectedItem()) : null);
            checkInData.setGuestName(guest_name.getText());
            checkInData.setRoomNumber(room.getSelectionModel().getSelectedItem() != null? room.getSelectionModel().getSelectedItem() : 0);
            if (checkInData.getGuestName() == null || checkInData.getRoomType() == null || checkInData.getRoomPrice() == 0 || checkInData.getRoomNumber() == 0 || checkInData.getPaymentMethod() == null){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Invalid request");
//                alert.setHeaderText("This is a header");
                alert.setContentText("Fields can not be empty");

                // Show the popup
                alert.showAndWait();
            }
            else{
                Utils utils = new Utils();
                utils.switchScreenWithCheckInData("/com/bmh/hotelmanagementsystem/check-in-invoice-view.fxml", primaryStage, checkInData);
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
