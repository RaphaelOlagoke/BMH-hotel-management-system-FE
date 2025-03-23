package com.bmh.hotelmanagementsystem.RoomManagement;

import com.bmh.hotelmanagementsystem.BackendService.RestClient;
import com.bmh.hotelmanagementsystem.BackendService.entities.Room.*;
import com.bmh.hotelmanagementsystem.BackendService.enums.ReservationLogStatus;
import com.bmh.hotelmanagementsystem.TokenStorage;
import com.bmh.hotelmanagementsystem.dto.room.GuestReservation;
import com.bmh.hotelmanagementsystem.BackendService.entities.*;
import com.bmh.hotelmanagementsystem.BackendService.enums.GuestLogStatus;
import com.bmh.hotelmanagementsystem.BackendService.enums.PaymentStatus;
import com.bmh.hotelmanagementsystem.Controller;
import com.bmh.hotelmanagementsystem.Utils;
import com.bmh.hotelmanagementsystem.dto.room.ReservationRow;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
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
import java.util.Locale;

import static com.bmh.hotelmanagementsystem.Utils.showLoadingScreen;

public class ReservationLogController extends Controller {
    private Stage primaryStage;

    private String previousLocation;
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }



    @FXML
    private Label user;

    @FXML
    private Label todayDate;
    @FXML
    private TableView<ReservationRow> reservationLogTable;
    @FXML
    private TableColumn<ReservationRow, String> guestName;
    @FXML
    private TableColumn<ReservationRow, String> rooms;
    @FXML
    private TableColumn<ReservationRow, String> phoneNumber;
    @FXML
    private TableColumn<ReservationRow, ReservationLogStatus> status;

    @FXML
    private TableColumn<ReservationRow, String> date;

    @FXML
    private TableColumn<ReservationRow, Void> viewMore;

    @FXML
    private Pagination pagination;

    @FXML
    private ComboBox<String> status_comboBox;
    @FXML
    private ComboBox<Integer> room_comboBox;

    @FXML
    private DatePicker start_date;
    @FXML
    private DatePicker end_date;

    @FXML
    private Button apply;

    @FXML
    private Button create;

    @FXML
    private Label noOfEntries;

    private List<Reservation> reservationLogs = new ArrayList<>();


    private ObservableList<ReservationRow> data = FXCollections.observableArrayList();

    private int pageSize = 10;
    private int page = 0;


    public void initialize() {

        String[] credentials = TokenStorage.loadCredentials();
        String username = "";
        if (credentials != null) {
            username = credentials[0];
        }

        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("d, MMMM, yyyy", Locale.ENGLISH);
        String formattedDate = currentDate.format(dateTimeFormatter);
        user.setText("Hello " + username);
        todayDate.setText(formattedDate);

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

        start_date.setValue(LocalDate.now());
        end_date.setValue(LocalDate.now());

        ObservableList<String> reservation_log_status = FXCollections.observableArrayList();

        reservation_log_status.add(null);
        for (ReservationLogStatus reservationLogStatus : ReservationLogStatus.values()) {
            reservation_log_status.add(reservationLogStatus.toJson());
        }

        status_comboBox.setItems(reservation_log_status);

        status_comboBox.setValue(ReservationLogStatus.ACTIVE.toJson());

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
                        loadingStage.close();
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
                        phoneNumber.setCellValueFactory(cellData -> cellData.getValue().phoneNumberProperty());
                        date.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
                        status.setCellValueFactory(cellData -> cellData.getValue().statusProperty());

                        viewMore.setCellFactory(createViewMoreButtonCellFactory());

                        reservationLogTable.setRowFactory(tableView -> {
                            TableRow<ReservationRow> row = new TableRow<>();

                            // Add a mouse click event listener to each row
                            row.setOnMouseClicked(event -> {
                                // Check if the row is not empty (i.e., not the header)
                                if (!row.isEmpty()) {
                                    ReservationRow clickedRow = row.getItem();  // Get the item (data) in the clicked row
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
                        loadingStage.close();
                        Utils.showAlertDialog(Alert.AlertType.ERROR,apiResponse.getResponseHeader().getResponseMessage(),apiResponse.getError() );
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

        pagination.setPageFactory(this::createPage);
        apply.setOnAction(event -> apply());
        create.setOnAction(event -> createReservation());
    }


    private void loadPage(int page, int size) {
        new Thread(() -> {
            try {

                Integer selectedRoom = room_comboBox.getSelectionModel().getSelectedItem();

                ReservationLogFilterRequest request = new ReservationLogFilterRequest();
                request.setStatus(status_comboBox.getSelectionModel().getSelectedItem() != null? ReservationLogStatus.valueOf(status_comboBox.getSelectionModel().getSelectedItem()) : null);

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

                String response = RestClient.post("/reservation/filter?page=" + page + "&size=" + size, jsonString);

                ApiResponse<Reservation> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponse<Reservation>>() {});

                if (apiResponse.getResponseHeader().getResponseCode().equals("00")) {
                    Platform.runLater(() -> {
    //                    loadingStage.close();
                        reservationLogs = apiResponse.getData();
                        data.clear();
                        for (Reservation reservation : reservationLogs){
                            StringBuilder room = new StringBuilder();

                            int count = reservation.getRoomNumbers().size();
                            int index = 0;

                            for (Room item : reservation.getRoomNumbers()) {
                                room.append(item.getRoomNumber());

                                if (index < count - 1) {
                                    room.append(", ");
                                }
                                index++;
                            }

                            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                            String date = "";
                            if (reservation.getCreatedDateTime() != null) {
                                date = reservation.getCreatedDateTime().format(dateTimeFormatter);
                            }

                            ReservationRow reservationRow = new ReservationRow(reservation.getGuestName(), room.toString(),
                                    reservation.getGuestPhoneNumber(), reservation.getStatus(), date, reservation);

                            data.add(reservationRow);
                        }

                        reservationLogTable.setItems(data);
                        pagination.setPageCount(apiResponse.getTotalPages());
                        pagination.setCurrentPageIndex(page);

                        noOfEntries.setText("Showing Data 1 to " + apiResponse.getTotalPages() + " of " + apiResponse.getTotalElements() + " entries");

                        status.setCellFactory(column -> {
                            return new TableCell<ReservationRow, ReservationLogStatus>() {
                                @Override
                                protected void updateItem(ReservationLogStatus item, boolean empty) {
                                    super.updateItem(item, empty);
                                    if (empty || item == null) {
                                        setText(null);
                                        setGraphic(null);  // Reset cell content
                                    } else {
                                        // Create a Label for the status
                                        Label label = new Label(item.toJson());
                                        label.setStyle("-fx-font-weight: bold; -fx-padding: 5px;");

                                        // Apply styles based on the status
                                        if (item == ReservationLogStatus.ACTIVE) {
                                            label.setStyle("-fx-text-fill: green; -fx-background-color: #e0f7e0; -fx-font-weight: bold; -fx-padding: 5px; -fx-background-radius: 15;");
                                        } else if (item == ReservationLogStatus.COMPLETE) {
                                            label.setStyle("-fx-text-fill: blue; -fx-background-color: #e0e0f7; -fx-font-weight: bold; -fx-padding: 5px; -fx-background-radius: 15;");
                                        }
                                        else if (item == ReservationLogStatus.CANCELED) {
                                            label.setStyle("-fx-text-fill: red; -fx-background-color: #f7e0e0; -fx-font-weight: bold; -fx-padding: 5px; -fx-background-radius: 15;");
                                        }else {
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
    //                loadingStage.close();
                    Platform.runLater(() -> {
                        Utils.showAlertDialog(Alert.AlertType.ERROR,apiResponse.getResponseHeader().getResponseMessage(),apiResponse.getError() );
                    });
                }

            } catch (Exception e) {
    //            loadingStage.close();
                Platform.runLater(() -> {
                    System.out.println(e);
                    Utils.showGeneralErrorDialog();
                });
            }
        }).start();
    }

    private Node createPage(int pageIndex) {
        loadPage(pageIndex, pageSize);
        return reservationLogTable;
    }

    private Callback<TableColumn<ReservationRow, Void>, TableCell<ReservationRow, Void>> createViewMoreButtonCellFactory() {
        return new Callback<>() {
            @Override
            public TableCell<ReservationRow, Void> call(TableColumn<ReservationRow, Void> param) {
                final TableCell<ReservationRow, Void> cell = new TableCell<ReservationRow, Void>() {

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
                            ReservationRow reservation = getTableView().getItems().get(getIndex());
                            showGuestDetails(reservation);  // Call a method to handle the button click
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
    private void showGuestDetails(ReservationRow reservationRow) {
//        System.out.println("Viewing more details for: " + guest.getGuestName());
        Reservation reservation  = reservationRow.getReservation();
        if(reservation.getStatus() == ReservationLogStatus.ACTIVE) {
            try {
                Utils utils = new Utils();
                utils.switchScreenWithData("/com/bmh/hotelmanagementsystem/room/single-reservation-log-view.fxml", primaryStage, reservation, "/com/bmh/hotelmanagementsystem/room/reservation-log-view.fxml");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            Utils.showAlertDialog(Alert.AlertType.INFORMATION, "Reservation not active", "This reservation has been completed or canceled");
        }
    }

    private void apply() {
        Stage loadingStage = Utils.showLoadingScreen(primaryStage);
        loadingStage.show();

        new Thread(() -> {

            try {

                Integer selectedRoom = room_comboBox.getSelectionModel().getSelectedItem();

                ReservationLogFilterRequest request = new ReservationLogFilterRequest();
                request.setStatus(status_comboBox.getSelectionModel().getSelectedItem() != null? ReservationLogStatus.valueOf(status_comboBox.getSelectionModel().getSelectedItem()) : null);

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

                String response = RestClient.post("/reservation/filter?page=" + page + "&size=" + pageSize, jsonString);

                ApiResponse<Reservation> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponse<Reservation>>() {});

                if (apiResponse.getResponseHeader().getResponseCode().equals("00")) {
                    Platform.runLater(() -> {
                    loadingStage.close();
                        reservationLogs = apiResponse.getData();
                        data.clear();
                        for (Reservation reservation : reservationLogs){
                            StringBuilder room = new StringBuilder();

                            int count = reservation.getRoomNumbers().size();
                            int index = 0;

                            for (Room item : reservation.getRoomNumbers()) {
                                room.append(item.getRoomNumber());

                                if (index < count - 1) {
                                    room.append(", ");
                                }
                                index++;
                            }

                            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                            String date = "";
                            if (reservation.getCreatedDateTime() != null) {
                                date = reservation.getCreatedDateTime().format(dateTimeFormatter);
                            }

                            ReservationRow reservationRow = new ReservationRow(reservation.getGuestName(), room.toString(),
                                    reservation.getGuestPhoneNumber(), reservation.getStatus(), date, reservation);

                            data.add(reservationRow);
                        }

                        reservationLogTable.setItems(data);
                        pagination.setPageCount(apiResponse.getTotalPages());
                        pagination.setCurrentPageIndex(page);

                        noOfEntries.setText("Showing Data 1 to " + apiResponse.getTotalPages() + " of " + apiResponse.getTotalElements() + " entries");

                        status.setCellFactory(column -> {
                            return new TableCell<ReservationRow, ReservationLogStatus>() {
                                @Override
                                protected void updateItem(ReservationLogStatus item, boolean empty) {
                                    super.updateItem(item, empty);
                                    if (empty || item == null) {
                                        setText(null);
                                        setGraphic(null);  // Reset cell content
                                    } else {
                                        // Create a Label for the status
                                        Label label = new Label(item.toJson());
                                        label.setStyle("-fx-font-weight: bold; -fx-padding: 5px;");

                                        // Apply styles based on the status
                                        if (item == ReservationLogStatus.ACTIVE) {
                                            label.setStyle("-fx-text-fill: green; -fx-background-color: #e0f7e0; -fx-font-weight: bold; -fx-padding: 5px; -fx-background-radius: 15;");
                                        } else if (item == ReservationLogStatus.COMPLETE) {
                                            label.setStyle("-fx-text-fill: blue; -fx-background-color: #e0e0f7; -fx-font-weight: bold; -fx-padding: 5px; -fx-background-radius: 15;");
                                        }
                                        else if (item == ReservationLogStatus.CANCELED) {
                                            label.setStyle("-fx-text-fill: red; -fx-background-color: #f7e0e0; -fx-font-weight: bold; -fx-padding: 5px; -fx-background-radius: 15;");
                                        }else {
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
    }

    public void createReservation(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bmh/hotelmanagementsystem/room/create_reservation.fxml"));
            Region form = loader.load();

            Stage formStage = new Stage();
            formStage.initModality(Modality.APPLICATION_MODAL);
            formStage.setTitle("Fill out the Form");

            Controller controller = loader.getController();
            controller.setPrimaryStage(formStage);

            controller.setData(null, "/com/bmh/hotelmanagementsystem/room/reservation-log-view.fxml");


            Scene formScene = new Scene(form);
            formStage.setScene(formScene);
            formStage.showAndWait();

            apply();

        } catch (Exception e) {
            e.printStackTrace();
            Utils.showGeneralErrorDialog();
        }
    }

}
