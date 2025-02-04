package com.bmh.hotelmanagementsystem.RoomManagement;

import com.bmh.hotelmanagementsystem.BackendService.RestClient;
import com.bmh.hotelmanagementsystem.dto.room.GuestReservation;
import com.bmh.hotelmanagementsystem.BackendService.entities.*;
import com.bmh.hotelmanagementsystem.BackendService.entities.Room.GuestLog;
import com.bmh.hotelmanagementsystem.BackendService.entities.Room.GuestLogFilterRequest;
import com.bmh.hotelmanagementsystem.BackendService.entities.Room.GuestLogRoom;
import com.bmh.hotelmanagementsystem.BackendService.entities.Room.Room;
import com.bmh.hotelmanagementsystem.BackendService.enums.GuestLogStatus;
import com.bmh.hotelmanagementsystem.BackendService.enums.PaymentStatus;
import com.bmh.hotelmanagementsystem.Controller;
import com.bmh.hotelmanagementsystem.Utils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import static com.bmh.hotelmanagementsystem.Utils.showLoadingScreen;

public class GuestLogController extends Controller {
    private Stage primaryStage;

    private String previousLocation;
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }


    @FXML
    private TableView<GuestReservation> guestLogTable;
    @FXML
    private TableColumn<GuestReservation, String> guestName;
    @FXML
    private TableColumn<GuestReservation, String> rooms;
    @FXML
    private TableColumn<GuestReservation, String> checkInDate;
    @FXML
    private TableColumn<GuestReservation, String> checkOutDate;
    @FXML
    private TableColumn<GuestReservation, PaymentStatus> paymentStatus;
    @FXML
    private TableColumn<GuestReservation, GuestLogStatus> status;

    @FXML
    private TableColumn<GuestReservation, Void> viewMore;

    @FXML
    private ComboBox<String> status_comboBox;
    @FXML
    private ComboBox<Integer> room_comboBox;
    @FXML
    private ComboBox<String> paymentStatus_comboBox;

    @FXML
    private DatePicker start_date;
    @FXML
    private DatePicker end_date;

    @FXML
    private Button apply;

    private List<GuestLog> guestLogs = new ArrayList<>();


    private ObservableList<GuestReservation> data = FXCollections.observableArrayList();


    public void initialize() {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        start_date.setConverter(new javafx.util.StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                return date != null ? date.format(formatter) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                try {
                    return string != null && !string.isEmpty() ? LocalDate.parse(string, formatter) : null;
                } catch (DateTimeParseException e) {
                    // Invalid date input, show alert and return null

                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Invalid Date");
                    alert.setContentText("Invalid date entered. Please enter a valid date (yyyy-MM-dd).");
                    alert.showAndWait();
                    return null;
                }
            }
        });

        // Do the same for the end_date DatePicker
        end_date.setConverter(new javafx.util.StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                return date != null ? date.format(formatter) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                try {
                    return string != null && !string.isEmpty() ? LocalDate.parse(string, formatter) : null;
                } catch (DateTimeParseException e) {
                    // Invalid date input, show alert and return null
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Invalid Date");
                    alert.setContentText("Invalid date entered. Please enter a valid date (yyyy-MM-dd).");
                    alert.showAndWait();
                    return null;
                }
            }
        });

        ObservableList<String> guest_log_status = FXCollections.observableArrayList();

        guest_log_status.add(null);
        for (GuestLogStatus guestLogStatus : GuestLogStatus.values()) {
            guest_log_status.add(guestLogStatus.toJson());
        }

        status_comboBox.setItems(guest_log_status);


        ObservableList<String> payment_status = FXCollections.observableArrayList();

        payment_status.add(null);
        payment_status.add(PaymentStatus.COMPLETE.toJson());
        payment_status.add(PaymentStatus.DUE.toJson());

        paymentStatus_comboBox.setItems(payment_status);

        Stage loadingStage = showLoadingScreen(primaryStage);

        Platform.runLater(() -> loadingStage.show());

        new Thread(() -> {
            ObservableList<Integer> listOfRooms = FXCollections.observableArrayList();
            listOfRooms.add(null);

            try {
                String response = RestClient.get("/room/");

                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());

                // Convert JSON string to ApiResponse
                ApiResponse<Room> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponse<Room>>() {
                });

                if (apiResponse.getResponseHeader().getResponseCode().equals("00")) {
                    Platform.runLater(() -> {
                        List<Room> allRooms = new ArrayList<>();
                        allRooms = apiResponse.getData();
                        for (Room room : allRooms) {
                            listOfRooms.add(room.getRoomNumber());
                        }

                        room_comboBox.setItems(listOfRooms);

                        room_comboBox.setOnAction(event -> {
                            Integer selectedItem = room_comboBox.getValue();
                            if (selectedItem == null) {
                                // Clear the selection by setting the value to null
                                room_comboBox.setValue(null);
                            }
                        });

                        guestName.setCellValueFactory(cellData -> cellData.getValue().guestNameProperty());
                        rooms.setCellValueFactory(cellData -> cellData.getValue().roomsProperty());
                        checkInDate.setCellValueFactory(cellData -> cellData.getValue().checkInDateProperty());
                        checkOutDate.setCellValueFactory(cellData -> cellData.getValue().checkOutDateProperty());
                        paymentStatus.setCellValueFactory(cellData -> cellData.getValue().paymentStatusProperty());
                        status.setCellValueFactory(cellData -> cellData.getValue().statusProperty());

                        viewMore.setCellFactory(createViewMoreButtonCellFactory());

                        guestLogTable.setRowFactory(tableView -> {
                            TableRow<GuestReservation> row = new TableRow<>();

                            // Add a mouse click event listener to each row
                            row.setOnMouseClicked(event -> {
                                // Check if the row is not empty (i.e., not the header)
                                if (!row.isEmpty()) {
                                    GuestReservation clickedRow = row.getItem();  // Get the item (data) in the clicked row
                                    showGuestDetails(clickedRow);
                                }
                            });

                            return row;
                        });

//                        guestName.setStyle("-fx-pref-height: 70px; -fx-font-weight: bold; -fx-background-color: #4CAF50; -fx-text-fill: white;");
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

            try {
                String response = RestClient.get("/guestLog/");

                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());

                ApiResponse<GuestLog> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponse<GuestLog>>() {});

                if (apiResponse.getResponseHeader().getResponseCode().equals("00")) {
                    Platform.runLater(() -> {
                        loadingStage.close();
                        guestLogs = apiResponse.getData();
                        for (GuestLog guestLog : guestLogs){
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

                            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                            String checkInDate = guestLog.getCheckInDate().format(dateTimeFormatter);
                            String checkOutDate = "";
                            if (guestLog.getCheckOutDate() != null) {
                                checkOutDate = guestLog.getCheckOutDate().format(dateTimeFormatter);
                            }
                            GuestReservation guestReservation = new GuestReservation(guestLog.getGuestName(), room.toString(), checkInDate,
                                    checkOutDate, guestLog.getPaymentStatus(), guestLog.getStatus(), guestLog);

                            data.add(guestReservation);
                        }
//        data.add( new GuestReservation("Jane Smith", "105, 309", LocalDateTime.now(),
//                LocalDateTime.now().plusDays(1), PaymentStatus.COMPLETE, GuestLogStatus.ACTIVE));

                        guestLogTable.setItems(data);


                        for (TableColumn<GuestReservation, ?> column : guestLogTable.getColumns()) {
                            column.setStyle("-fx-pref-height: 70px;");
                        }

                        status.setCellFactory(column -> {
                            return new TableCell<GuestReservation, GuestLogStatus>() {
                                @Override
                                protected void updateItem(GuestLogStatus item, boolean empty) {
                                    super.updateItem(item, empty);
                                    if (empty || item == null) {
                                        setText(null);
                                        setGraphic(null);  // Reset cell content
                                    } else {
                                        // Create a Label for the status
                                        Label label = new Label(item.toJson());
                                        label.setStyle("-fx-font-weight: bold; -fx-padding: 5px;");

                                        // Apply styles based on the status
                                        if (item == GuestLogStatus.ACTIVE) {
                                            label.setStyle("-fx-text-fill: green; -fx-background-color: #e0f7e0; -fx-font-weight: bold; -fx-padding: 5px; -fx-background-radius: 15;");
                                        } else if (item == GuestLogStatus.COMPLETE) {
                                            label.setStyle("-fx-text-fill: blue; -fx-background-color: #e0e0f7; -fx-font-weight: bold; -fx-padding: 5px; -fx-background-radius: 15;");
                                        } else {
                                            label.setStyle("-fx-padding: 5px;");  // Default padding if not ACTIVE or COMPLETE
                                        }

                                        setGraphic(label);  // Set the Label as the graphic for this cell
                                    }
                                }
                            };
                        });
                    });
                }
                else{
                    loadingStage.close();
                    Platform.runLater(() -> {
                        Utils.showAlertDialog(Alert.AlertType.ERROR,apiResponse.getResponseHeader().getResponseMessage(),apiResponse.getError() );
                    });
                }

            } catch (Exception e) {
                loadingStage.close();
                Platform.runLater(() -> {
                    System.out.println(e);
                    Utils.showGeneralErrorDialog();
                });
            }
        }).start();

        apply.setOnAction(event -> apply());
    }

    private Callback<TableColumn<GuestReservation, Void>, TableCell<GuestReservation, Void>> createViewMoreButtonCellFactory() {
        return new Callback<>() {
            @Override
            public TableCell<GuestReservation, Void> call(TableColumn<GuestReservation, Void> param) {
                final TableCell<GuestReservation, Void> cell = new TableCell<GuestReservation, Void>() {

                    final FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bmh/hotelmanagementsystem/components/view_more_button.fxml"));
                    final Button button;

                    {
                        try {
                            button = loader.load();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    private final Button btn = new Button("View More");

                    {
                        button.setOnAction(event -> {
                            GuestReservation guest = getTableView().getItems().get(getIndex());
                            showGuestDetails(guest);  // Call a method to handle the button click
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(button);
                        }
                    }
                };
                return cell;
            }
        };
    }

    // This method will show more details when the "View More" button is clicked
    private void showGuestDetails(GuestReservation guest) {
//        System.out.println("Viewing more details for: " + guest.getGuestName());
        GuestLog guestLog  = guest.getGuestLog();
        try {
            Utils utils = new Utils();
            utils.switchScreenWithGuestLog("/com/bmh/hotelmanagementsystem/room/single-guest-log-view.fxml", primaryStage,guestLog, "guest-log-view.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void apply() {
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

        new Thread(() -> {

            try {
                Integer selectedRoom = room_comboBox.getSelectionModel().getSelectedItem();

                GuestLogFilterRequest request = new GuestLogFilterRequest();
                request.setStatus(status_comboBox.getSelectionModel().getSelectedItem() != null? GuestLogStatus.valueOf(status_comboBox.getSelectionModel().getSelectedItem()) : null);
                request.setPaymentStatus(paymentStatus_comboBox.getSelectionModel().getSelectedItem() != null? PaymentStatus.valueOf(paymentStatus_comboBox.getSelectionModel().getSelectedItem()) : null);

                request.setStartDate(start_date.getValue() != null ? start_date.getValue().atStartOfDay() : null);
                request.setEndDate(end_date.getValue() != null ? end_date.getValue().atTime(23, 59, 0) : null);
                try{
                    request.setRoomNumber(room_comboBox.getSelectionModel().getSelectedItem());
                }
                catch (NullPointerException e){
                    request.setRoomNumber(0);
                }


                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());

                String jsonString = objectMapper.writeValueAsString(request);

                String response = RestClient.post("/guestLog/filter", jsonString);
                ApiResponse<GuestLog> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponse<GuestLog>>() {});


                Platform.runLater(() -> {
                    try {
                        if (apiResponse.getResponseHeader().getResponseCode().equals("00")) {
                            // Close the loading screen
                            loadingStage.close();

                            // Show success message and navigate to home
                            guestLogs = apiResponse.getData();

                            data.clear();
                            for (GuestLog guestLog : guestLogs){
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

                                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                                String checkInDate = guestLog.getCheckInDate().format(dateTimeFormatter);
                                String checkOutDate = "";
                                if (guestLog.getCheckOutDate() != null) {
                                    checkOutDate = guestLog.getCheckOutDate().format(dateTimeFormatter);
                                }
                                GuestReservation guestReservation = new GuestReservation(guestLog.getGuestName(), room.toString(), checkInDate,
                                        checkOutDate, guestLog.getPaymentStatus(), guestLog.getStatus(), guestLog);

                                data.add(guestReservation);
                            }
                            guestLogTable.setItems(data);
                        } else {
                            // Close the loading screen
                            loadingStage.close();

                            // Show error message from API response
                            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                            errorAlert.setTitle(apiResponse.getResponseHeader().getResponseMessage());
                            errorAlert.setContentText(apiResponse.getError());
                            errorAlert.showAndWait();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        loadingStage.close();  // Close the loading screen in case of error

                        // Show general error message
                        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                        errorAlert.setTitle("Error");
                        errorAlert.setContentText("Something went wrong. Please try again.");
                        errorAlert.showAndWait();
                    }
                });


            } catch (Exception e) {
                Platform.runLater(() -> {
                    loadingStage.close();
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Server Error");
                    alert.setContentText("Something went wrong. Please try again.");
                    alert.showAndWait();
                });
            }
        }).start();
    }

}
