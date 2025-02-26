package com.bmh.hotelmanagementsystem.restaurant;

import com.bmh.hotelmanagementsystem.BackendService.RestClient;
import com.bmh.hotelmanagementsystem.BackendService.entities.ApiResponse;
import com.bmh.hotelmanagementsystem.BackendService.entities.ApiResponseSingleData;
import com.bmh.hotelmanagementsystem.BackendService.entities.Invoice.CreateInvoiceRequest;
import com.bmh.hotelmanagementsystem.BackendService.entities.Invoice.Invoice;
import com.bmh.hotelmanagementsystem.BackendService.entities.Invoice.Item;
import com.bmh.hotelmanagementsystem.BackendService.entities.Restaurant.*;
import com.bmh.hotelmanagementsystem.BackendService.entities.Room.GuestLog;
import com.bmh.hotelmanagementsystem.BackendService.entities.Room.GuestLogRoom;
import com.bmh.hotelmanagementsystem.BackendService.entities.Room.Room;
import com.bmh.hotelmanagementsystem.BackendService.entities.Room.RoomPrices;
import com.bmh.hotelmanagementsystem.BackendService.enums.*;
import com.bmh.hotelmanagementsystem.Controller;
import com.bmh.hotelmanagementsystem.Utils;
import com.bmh.hotelmanagementsystem.dto.restaurant.OrderDetails;
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
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.bmh.hotelmanagementsystem.Utils.showLoadingScreen;

public class ChargeToRoomController extends Controller {

    private Stage primaryStage;
    private String previousLocation;

    private OrderDetails data;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setData(Object data, String previousLocation){
        this.data = (OrderDetails) data;
        this.previousLocation = previousLocation;
    }

    @FXML
    private Button charge;
    @FXML
    private ComboBox<Integer> room_comboBox;
    @FXML
    private Label guestName;
    @FXML
    private Label roomNumber;

    public void initialize() {

        Stage loadingStage = showLoadingScreen(primaryStage);

        Platform.runLater(() -> loadingStage.show());

        new Thread(() -> {
            ObservableList<Integer> listOfRooms = FXCollections.observableArrayList();


            try {
                String response = RestClient.get("/room/status?roomStatus=occupied");

                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());

                // Convert JSON string to ApiResponse
                ApiResponse<Room> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponse<Room>>() {
                });

                if (apiResponse.getResponseHeader().getResponseCode().equals("00")) {
                    Platform.runLater(() -> {
                        loadingStage.close();
                        for (Room room : apiResponse.getData()) {
                            listOfRooms.add(room.getRoomNumber());
                        }

                        room_comboBox.setItems(listOfRooms);
                    });
                }
                else {
                    Platform.runLater(() -> {
                        Utils.showAlertDialog(Alert.AlertType.ERROR,apiResponse.getResponseHeader().getResponseMessage(),apiResponse.getError() );
                    });
                }

            } catch (Exception e) {
                Platform.runLater(() -> {
                    System.out.println(e);
                    Utils.showGeneralErrorDialog();
                });
            }
        }).start();

        room_comboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observableValue, Integer oldValue, Integer newValue) {
                Stage loadingStage = showLoadingScreen(primaryStage);

                Platform.runLater(() -> loadingStage.show());

                new Thread(() -> {
                    try {
                        String response = RestClient.get("/guestLog/find?roomNumber=" + newValue);

                        ObjectMapper objectMapper = new ObjectMapper();
                        objectMapper.registerModule(new JavaTimeModule());

                        // Convert JSON string to ApiResponse
                        ApiResponseSingleData<GuestLog> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponseSingleData<GuestLog>>() {
                        });

                        GuestLog guestLog = apiResponse.getData();
                        if (apiResponse.getResponseHeader().getResponseCode().equals("00")) {
                            Platform.runLater(() -> {
                                loadingStage.close();
                                guestName.setText("Guest Name:  " + guestLog.getGuestName());
                                StringBuilder room = new StringBuilder();

                                int size = guestLog.getGuestLogRooms().size();
                                int index = 0;

                                for (GuestLogRoom guestLogRoom : guestLog.getGuestLogRooms()) {
                                    room.append(guestLogRoom.getRoom().getRoomNumber());

                                    if (index < size - 1) {
                                        room.append(", ");
                                    }
                                    index++;
                                }
                                roomNumber.setText("Room Number:  " + room);
                            });
                        } else {
                            Platform.runLater(() -> {
                                loadingStage.close();
                                Utils.showAlertDialog(Alert.AlertType.ERROR, apiResponse.getResponseHeader().getResponseMessage(), apiResponse.getError());
                            });

                        }



                    } catch (Exception e) {
                        loadingStage.close();
                        System.out.println(e);
                        Utils.showGeneralErrorDialog();
                    }
                }).start();
            }
        });

        charge.setOnAction(event -> chargeToRoom());
    }

    public void chargeToRoom() {
        if (room_comboBox.getSelectionModel().isEmpty()) {
            Platform.runLater(() -> {
                Utils.showAlertDialog(Alert.AlertType.INFORMATION, "Invalid request", "Room cannot be empty");
            });
            return;
        }

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirm Payment");
        confirmationAlert.setHeaderText("Are you sure you want to charge order to room " + room_comboBox.getSelectionModel().getSelectedItem() + "?");
        confirmationAlert.setContentText("Please confirm your action.");

        Optional<ButtonType> result = confirmationAlert.showAndWait();


        if (result.isPresent() && result.get() == ButtonType.OK) {

            Stage loadingStage = Utils.showLoadingScreen(primaryStage);
            Platform.runLater(() -> loadingStage.show());

            new Thread(() -> {
                try {
                    CreateOrderRequest request = new CreateOrderRequest();
//                    request.setCustomerName(customerName.getText());
                    request.setItems(data.getBillItems());
                    request.setPaymentMethod(data.getPaymentMethod());
                    request.setRoomNumber(room_comboBox.getSelectionModel().getSelectedItem());

                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.registerModule(new JavaTimeModule());

                    String jsonString = objectMapper.writeValueAsString(request);

                    String response = RestClient.post("/restaurant/order/chargeToRoom", jsonString);
                    ApiResponseSingleData<Order> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponseSingleData<Order>>() {
                    });

                    if (apiResponse.getResponseHeader().getResponseCode().equals("00")) {
                        Platform.runLater(() -> {
                            loadingStage.close();
                            Utils.showAlertDialog(Alert.AlertType.INFORMATION, "Created Successfully", "Order created successfully");

                            try {
                                Utils utils = new Utils();
                                utils.switchScreenWithData("/com/bmh/hotelmanagementsystem/restaurant/receipt.fxml", primaryStage, apiResponse.getData(), previousLocation);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

//                            primaryStage.close();

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
        } else {
            confirmationAlert.close();
        }
    }


}
