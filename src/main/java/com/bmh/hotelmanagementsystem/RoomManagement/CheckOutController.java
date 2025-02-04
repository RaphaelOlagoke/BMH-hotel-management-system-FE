package com.bmh.hotelmanagementsystem.RoomManagement;

import com.bmh.hotelmanagementsystem.BackendService.RestClient;
import com.bmh.hotelmanagementsystem.BackendService.entities.*;
import com.bmh.hotelmanagementsystem.BackendService.entities.Room.*;
import com.bmh.hotelmanagementsystem.BackendService.enums.GuestLogStatus;
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
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CheckOutController extends Controller {

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

    @FXML
    private FlowPane rooms_flowPane;

    private GuestLog guestLog;

    private List<Integer> selectedRooms = new ArrayList<>();


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
                    Double total_price = 0.0;

                    StringBuilder types = new StringBuilder();

                    int size = guestLog.getGuestLogRooms().size();
                    int index = 0;


                    for (GuestLogRoom guestLogRoom : guestLog.getGuestLogRooms()) {

                        try {
                            RoomPrices roomPrice;
                            String responseRoomPrice = RestClient.get("/roomPrices/?roomType=" + guestLogRoom.getRoom().getRoomType());
                            ApiResponseSingleData<RoomPrices> apiResponseRoomPrice = objectMapper.readValue(responseRoomPrice, new TypeReference<ApiResponseSingleData<RoomPrices>>() {
                            });

                            roomPrice = apiResponseRoomPrice.getData();
                            total_price += roomPrice.getRoomPrice();

                            types.append(guestLogRoom.getRoom().getRoomType());
                            if (index < size - 1) {
                                types.append(", ");
                            }
                            index++;


                        } catch (Exception e) {
                            System.out.println(e);
                        }
                    }

                    DecimalFormat formatter = new DecimalFormat("#,###.00");

                    selectedRooms = new ArrayList<>();
                    rooms_flowPane.getChildren().clear();

                    List<GuestLogRoom> activeGuestLogRooms = new ArrayList<>();


                    for (GuestLogRoom guestLogRoom : guestLog.getGuestLogRooms()) {

                        if(guestLogRoom.getGuestLogStatus() == GuestLogStatus.ACTIVE) {

                            FXMLLoader hboxLoader = new FXMLLoader(getClass().getResource("/com/bmh/hotelmanagementsystem/room/add_room.fxml"));
                            HBox hBox = hboxLoader.load();
                            Label roomNumberLabel = (Label) hBox.lookup("#roomNumber");
                            Button cancelButton = (Button) hBox.lookup("#cancel");

                            activeGuestLogRooms.add(guestLogRoom);

                            roomNumberLabel.setText(String.valueOf(guestLogRoom.getRoom().getRoomNumber()));
                            if (activeGuestLogRooms.size() > 1) {
                                cancelButton.setOnAction(event -> removeRoom(guestLogRoom.getRoom().getRoomNumber(), hBox));
                            }

                            rooms_flowPane.getChildren().add(hBox);
                            selectedRooms.add(guestLogRoom.getRoom().getRoomNumber());
                        }
                    }

                    String formattedPrice = formatter.format(total_price);
                    room_price.setText(formattedPrice);

                    guest_name.setText("Name:    " + guestLog.getGuestName());
                    room_type.setText("Room Type(s):    " + types);
                    outstanding_payments.setText("Outstanding Payment:    " + guestLog.getTotalAmountDue());

                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        });

        generate_invoice.setOnAction(event -> generateInvoice());
        check_out.setOnAction(event -> checkOut());
    }

    public void removeRoom(Integer roomNumber,HBox hBox){
        rooms_flowPane.getChildren().remove(hBox);
        selectedRooms.remove(roomNumber);

//        rooms.getSelectionModel().clearSelection();
    }

    public void generateInvoice(){
        if(rooms.getSelectionModel().getSelectedItem() == null){
            Alert warningAlert = new Alert(Alert.AlertType.WARNING);
            warningAlert.setTitle("Invalid Request");
            warningAlert.setContentText("Field can not be null.");
            warningAlert.showAndWait();
            return;
        }
        try {
            Utils utils = new Utils();
            utils.switchScreenWithGuestLog("/com/bmh/hotelmanagementsystem/invoice/invoice-view.fxml", primaryStage,guestLog, "checkOut-view.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void checkOut() {
        if (selectedRooms == null) {
            Alert warningAlert = new Alert(Alert.AlertType.WARNING);
            warningAlert.setTitle("Invalid Request");
            warningAlert.setContentText("No room selected");
            warningAlert.showAndWait();
        }
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirm Check-Out");
        confirmationAlert.setHeaderText("Are you sure you want to check out?");
        confirmationAlert.setContentText("Please confirm your action.");

        Optional<ButtonType> result = confirmationAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {

            if (selectedRooms != null) {
                try {
                    // Show the loading screen
                    Stage loadingStage = new Stage();
                    ProgressIndicator progressIndicator = new ProgressIndicator();
                    StackPane loadingRoot = new StackPane();
                    loadingRoot.getChildren().add(progressIndicator);
                    Scene loadingScene = new Scene(loadingRoot, 200, 200);
                    loadingStage.setScene(loadingScene);
                    loadingStage.setTitle("Processing...");
                    loadingStage.initOwner(primaryStage);
                    loadingStage.initModality(Modality.APPLICATION_MODAL);
                    loadingStage.show();

                    UpdateGuestLogRequest request = new UpdateGuestLogRequest();
                    request.setRoomNumbers(selectedRooms);

                    ObjectMapper objectMapper = new ObjectMapper();
                    String jsonString = objectMapper.writeValueAsString(request);

                    String response = RestClient.post("/guestLog/check-out", jsonString);
                    ApiResponseSingleData<?> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponseSingleData<?>>() {});

                    Platform.runLater(() -> {
                        try {
                            loadingStage.close();

                            if (apiResponse.getResponseHeader().getResponseCode().equals("00")) {
                                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                                successAlert.setTitle("Check-out Successful");
                                successAlert.setContentText("The check-out was successful.");
                                successAlert.showAndWait();

                                // Switch screen to home view
                                Utils utils = new Utils();
                                utils.switchScreen("/com/bmh/hotelmanagementsystem/home-view.fxml", primaryStage);
                            } else {
                                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                                errorAlert.setTitle(apiResponse.getResponseHeader().getResponseMessage());
                                errorAlert.setContentText(apiResponse.getError());
                                errorAlert.showAndWait();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            loadingStage.close();

                            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                            errorAlert.setTitle("Error");
                            errorAlert.setContentText("Something went wrong. Please try again.");
                            errorAlert.showAndWait();
                        }
                    });

                } catch (Exception e) {
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setContentText("Something went wrong. Please try again.");
                        alert.showAndWait();
                    });
                }
            } else {
                Alert warningAlert = new Alert(Alert.AlertType.WARNING);
                warningAlert.setTitle("Invalid Request");
                warningAlert.setContentText("Field cannot be null.");
                warningAlert.showAndWait();
            }
        } else {
            confirmationAlert.close();
        }
    }



}
