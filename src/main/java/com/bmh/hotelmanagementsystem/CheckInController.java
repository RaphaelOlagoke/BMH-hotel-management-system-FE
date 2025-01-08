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
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
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
    private Label price;
    @FXML
    private Label total_price;

    @FXML
    private Label rooms_message;

    @FXML
    private FlowPane rooms_flowPane;

    CheckIn checkInData = new CheckIn();

    private List<Integer> selectedRooms = new ArrayList<>();

    private Double currentPrice = 0.0;
    private List<SelectedRoom> selectedRoomList = new ArrayList<>();

    private Double total;

    @FXML
    public void initialize() throws IOException {

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
//                    checkInData.setRoomType(RoomType.valueOf(newValue));
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
//                    checkInData.setRoomPrice(roomPrice.getRoomPrice());
                    DecimalFormat formatter = new DecimalFormat("#,###.00");

                    String formattedPrice = formatter.format(roomPrice.getRoomPrice());
                    price.setText(formattedPrice);

                    currentPrice = roomPrice.getRoomPrice();


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
        room.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                FXMLLoader hboxLoader = new FXMLLoader(getClass().getResource("/com/bmh/hotelmanagementsystem/components/add_room.fxml"));
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
                            total += item.getRoomPrice();
                        }
                        DecimalFormat formatter = new DecimalFormat("#,###.00");

                        String formattedPrice = formatter.format(total);
                        total_price.setText(formattedPrice);

                    }

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });


    }

    public void removeRoom(Integer roomNumber,HBox hBox, SelectedRoom selectedRoom){
        rooms_flowPane.getChildren().remove(hBox);
        selectedRooms.remove(roomNumber);
        selectedRoomList.remove(selectedRoom);

        total = 0.0;
        for (SelectedRoom item : selectedRoomList){
            total += item.getRoomPrice();
        }
        DecimalFormat formatter = new DecimalFormat("#,###.00");

        String formattedPrice = formatter.format(total);
        total_price.setText(formattedPrice);

        room.getSelectionModel().clearSelection();
    }


    public void checkIn(){
        try {
            checkInData.setGuestName(guest_name.getText());
            checkInData.setSelectedRooms(selectedRoomList != null && !selectedRoomList.isEmpty() ? selectedRoomList : null);
            checkInData.setTotalPrice(total);
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
