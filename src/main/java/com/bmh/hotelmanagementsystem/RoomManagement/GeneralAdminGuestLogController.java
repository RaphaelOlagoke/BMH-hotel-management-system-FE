package com.bmh.hotelmanagementsystem.RoomManagement;

import com.bmh.hotelmanagementsystem.BackendService.RestClient;
import com.bmh.hotelmanagementsystem.BackendService.entities.ApiResponse;
import com.bmh.hotelmanagementsystem.BackendService.entities.ApiResponseSingleData;
import com.bmh.hotelmanagementsystem.BackendService.entities.Room.*;
import com.bmh.hotelmanagementsystem.BackendService.enums.GuestLogStatus;
import com.bmh.hotelmanagementsystem.BackendService.enums.PaymentStatus;
import com.bmh.hotelmanagementsystem.BackendService.utils.AuthFileCache;
import com.bmh.hotelmanagementsystem.Controller;
import com.bmh.hotelmanagementsystem.TokenStorage;
import com.bmh.hotelmanagementsystem.Utils;
import com.bmh.hotelmanagementsystem.dto.room.GuestReservation;
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
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
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

public class GeneralAdminGuestLogController extends Controller {

    private Stage primaryStage;

    @FXML
    private Label user;

    @FXML
    private Label todayDate;

    @FXML
    private Label availableRoomsCount;
    @FXML
    private Label occupiedRoomsCount;
    @FXML
    private Label cleaningRoomsCount;
    @FXML
    private Label maintenanceRoomsCount;
    @FXML
    private Pagination pagination;

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
    private TableColumn<GuestReservation, String> expectedCheckOutDate;
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

    @FXML
    private Label noOfEntries;

    private List<GuestLog> guestLogs = new ArrayList<>();


    private ObservableList<GuestReservation> data = FXCollections.observableArrayList();


    private int pageSize = 10;
    private int page = 0;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    public void initialize()  {
        guestLogTable.setPrefWidth(510);
        pagination.getStylesheets().add(getClass().getResource("/com/bmh/hotelmanagementsystem/css/styles.css").toExternalForm());

//        String username = AuthFileCache.getUsername();
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

        ObservableList<String> guest_log_status = FXCollections.observableArrayList();

        guest_log_status.add(null);
        for (GuestLogStatus guestLogStatus : GuestLogStatus.values()) {
            guest_log_status.add(guestLogStatus.toJson());
        }

        status_comboBox.setItems(guest_log_status);


        ObservableList<String> payment_status = FXCollections.observableArrayList();

        payment_status.add(null);
        payment_status.add(PaymentStatus.PAID.toJson());
        payment_status.add(PaymentStatus.UNPAID.toJson());

        paymentStatus_comboBox.setItems(payment_status);

        apply.setOnAction(event -> apply());


        Stage loadingStage = showLoadingScreen(primaryStage);

        Platform.runLater(() -> loadingStage.show());

        new Thread(() -> {
            ObservableList<Integer> listOfRooms = FXCollections.observableArrayList();
            listOfRooms.add(null);

            try {
                String responseAvailable = RestClient.get("/room/roomSummary");

                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());

                // Convert JSON string to ApiResponse
                ApiResponseSingleData<RoomSummary> apiResponse = objectMapper.readValue(responseAvailable, new TypeReference<ApiResponseSingleData<RoomSummary>>() {});

                Platform.runLater(() -> {
                    if (apiResponse.getData() != null) {
                        availableRoomsCount.setText(String.valueOf(apiResponse.getData().getNoOfAvailableRooms()));
                    }
                    if (apiResponse.getData() != null) {
                        occupiedRoomsCount.setText(String.valueOf(apiResponse.getData().getNoOfOccupiedRooms()));
                    }
                    if (apiResponse.getData() != null) {
                        cleaningRoomsCount.setText(String.valueOf(apiResponse.getData().getNoOfRoomsThatNeedCleaning()));
                    }
                    if (apiResponse.getData() != null) {
                        maintenanceRoomsCount.setText(String.valueOf(apiResponse.getData().getNoOfRoomsThatNeedMaintenance()));
                    }
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    System.out.println(e);
                    Utils.showGeneralErrorDialog();
                });
            }

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
                        checkInDate.setCellValueFactory(cellData -> cellData.getValue().checkInDateProperty());
                        checkOutDate.setCellValueFactory(cellData -> cellData.getValue().checkOutDateProperty());
                        expectedCheckOutDate.setCellValueFactory(cellData -> cellData.getValue().expectedCheckOutDateProperty());
                        paymentStatus.setCellValueFactory(cellData -> cellData.getValue().paymentStatusProperty());
                        status.setCellValueFactory(cellData -> cellData.getValue().statusProperty());

                        viewMore.setCellFactory(createViewMoreButtonCellFactory());

                        setWrapCellFactory(checkOutDate);
                        setWrapCellFactory(expectedCheckOutDate);
                        setWrapCellFactory(checkInDate);

//                        guestLogTable.setRowFactory(tableView -> {
//                            TableRow<GuestReservation> row = new TableRow<>();
//
//                            // Add a mouse click event listener to each row
//                            row.setOnMouseClicked(event -> {
//                                // Check if the row is not empty (i.e., not the header)
//                                if (!row.isEmpty()) {
//                                    GuestReservation clickedRow = row.getItem();  // Get the item (data) in the clicked row
//                                    showGuestDetails(clickedRow);
//                                }
//                            });
//
//                            return row;
//                        });



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

//            try {
//                String response = RestClient.get("/guestLog/?page=" + page + "&size=" + pageSize);
//
//                ObjectMapper objectMapper = new ObjectMapper();
//                objectMapper.registerModule(new JavaTimeModule());
//
//                ApiResponse<GuestLog> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponse<GuestLog>>() {});
//
//                if (apiResponse.getResponseHeader().getResponseCode().equals("00")) {
//                    Platform.runLater(() -> {
//                        loadingStage.close();
//                        guestLogs = apiResponse.getData();
//                        for (GuestLog guestLog : guestLogs){
//                            StringBuilder room = new StringBuilder();
//
//                            int size = guestLog.getGuestLogRooms().size();
//                            int index = 0;
//
//                            for (GuestLogRoom guestLogRoom : guestLog.getGuestLogRooms()) {
//                                room.append(guestLogRoom.getRoom().getRoomNumber());
//
//                                if (index < size - 1) {
//                                    room.append(", ");
//                                }
//                                index++;
//                            }
//
//                            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//                            String checkInDate = guestLog.getCheckInDate().format(dateTimeFormatter);
//                            String checkOutDate = "";
//                            if (guestLog.getCheckOutDate() != null) {
//                                checkOutDate = guestLog.getCheckOutDate().format(dateTimeFormatter);
//                            }
//                            GuestReservation guestReservation = new GuestReservation(guestLog.getGuestName(), room.toString(), checkInDate,
//                                    checkOutDate, guestLog.getPaymentStatus(), guestLog.getStatus(), guestLog);
//
//                            data.add(guestReservation);
//                        }
//                        //        data.add( new GuestReservation("Jane Smith", "105, 309", LocalDateTime.now(),
//                        //                LocalDateTime.now().plusDays(1), PaymentStatus.COMPLETE, GuestLogStatus.ACTIVE));
//
//                        guestLogTable.setItems(data);
//                        pagination.setPageCount(apiResponse.getTotalPages());
//
//
//                        //                        for (TableColumn<GuestReservation, ?> column : guestLogTable.getColumns()) {
//                        //                            column.setStyle("-fx-pref-height: 70px;");
//                        //                        }
//
//                        status.setCellFactory(column -> {
//                            return new TableCell<GuestReservation, GuestLogStatus>() {
//                                @Override
//                                protected void updateItem(GuestLogStatus item, boolean empty) {
//                                    super.updateItem(item, empty);
//                                    if (empty || item == null) {
//                                        setText(null);
//                                        setGraphic(null);  // Reset cell content
//                                    } else {
//                                        // Create a Label for the status
//                                        Label label = new Label(item.toJson());
//                                        label.setStyle("-fx-font-weight: bold; -fx-padding: 5px;");
//
//                                        // Apply styles based on the status
//                                        if (item == GuestLogStatus.COMPLETE) {
//                                            label.setStyle("-fx-text-fill: green; -fx-background-color: #e0f7e0; -fx-font-weight: bold; -fx-padding: 5px 10px; -fx-background-radius: 5;");
//                                        } else if (item == GuestLogStatus.ACTIVE) {
//                                            label.setStyle("-fx-text-fill: blue; -fx-background-color: #e0e0f7; -fx-font-weight: bold; -fx-padding: 5px; -fx-background-radius: 5;");
//                                        } else {
//                                            label.setStyle("-fx-padding: 5px;");  // Default padding if not ACTIVE or COMPLETE
//                                        }
//
//                                        setGraphic(label);  // Set the Label as the graphic for this cell
//                                    }
//                                }
//                            };
//                        });
//
//                        paymentStatus.setCellFactory(column -> {
//                            return new TableCell<GuestReservation, PaymentStatus>() {
//                                @Override
//                                protected void updateItem(PaymentStatus item, boolean empty) {
//                                    super.updateItem(item, empty);
//                                    if (empty || item == null) {
//                                        setText(null);
//                                        setGraphic(null);  // Reset cell content
//                                    } else {
//                                        // Create a Label for the status
//                                        Label label = new Label(item.toJson());
//                                        label.setStyle("-fx-font-weight: bold; -fx-padding: 5px;");
//
//                                        // Apply styles based on the status
//                                        if (item == PaymentStatus.COMPLETE) {
//                                            label.setStyle("-fx-text-fill: green; -fx-background-color: #e0f7e0; -fx-font-weight: bold; -fx-padding: 5px 10px; -fx-background-radius: 5;");
//                                        } else if (item == PaymentStatus.DUE) {
//                                            label.setStyle("-fx-text-fill: red; -fx-background-color: #f7e0e0; -fx-font-weight: bold; -fx-padding: 5px 15px; -fx-background-radius: 5;");
//                                        } else {
//                                            label.setStyle("-fx-padding: 5px;");  // Default padding if not ACTIVE or COMPLETE
//                                        }
//
//                                        setGraphic(label);  // Set the Label as the graphic for this cell
//                                    }
//                                }
//                            };
//                        });
//                    });
//                }
//                else{
//                    loadingStage.close();
//                    Platform.runLater(() -> {
//                        Utils.showAlertDialog(Alert.AlertType.ERROR,apiResponse.getResponseHeader().getResponseMessage(),apiResponse.getError() );
//                    });
//                }
//
//            } catch (Exception e) {
//                loadingStage.close();
//                Platform.runLater(() -> {
//                    System.out.println(e);
//                    Utils.showGeneralErrorDialog();
//                });
//            }

        }).start();

        pagination.setPageFactory(this::createPage);
    }

    private void loadPage(int page, int size) {
//        Stage loadingStage = showLoadingScreen(primaryStage);
//
//        loadingStage.show();

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

                String response = RestClient.post("/guestLog/filter?page=" + page + "&size=" + size, jsonString);

                ApiResponse<GuestLog> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponse<GuestLog>>() {});

                if (apiResponse.getResponseHeader().getResponseCode().equals("00")) {
                    Platform.runLater(() -> {
//                        loadingStage.close();

                        data.clear();


                        for (GuestLog guestLog : apiResponse.getData()){
                            StringBuilder room = new StringBuilder();

                            int count = guestLog.getGuestLogRooms().size();
                            int index = 0;

                            for (GuestLogRoom guestLogRoom : guestLog.getGuestLogRooms()) {
                                if(guestLog.getStatus() == GuestLogStatus.COMPLETE){
                                    room.append(guestLogRoom.getRoom().getRoomNumber());

                                    if (index < count - 1) {
                                        room.append(", ");
                                    }
                                }
                                else{
                                    if(guestLogRoom.getGuestLogStatus() == GuestLogStatus.ACTIVE){
                                        room.append(guestLogRoom.getRoom().getRoomNumber());

                                        if (index < count - 1) {
                                            room.append(", ");
                                        }
                                    }
                                }

                                index++;
                            }

                            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                            String checkInDate = guestLog.getCheckInDate().format(dateTimeFormatter);
                            String checkOutDate = "";
                            String expectedCheckOutDate = "";
                            if (guestLog.getCheckOutDate() != null) {
                                checkOutDate = guestLog.getCheckOutDate().format(dateTimeFormatter);
                            }
                            if (guestLog.getExpectedCheckOutDate() != null) {
                                expectedCheckOutDate = guestLog.getExpectedCheckOutDate().format(dateTimeFormatter);
                            }
                            GuestReservation guestReservation = new GuestReservation(guestLog.getGuestName(), room.toString(), checkInDate,
                                    checkOutDate, expectedCheckOutDate,guestLog.getPaymentStatus(), guestLog.getStatus(), guestLog);

                            data.add(guestReservation);
                        }
                        //        data.add( new GuestReservation("Jane Smith", "105, 309", LocalDateTime.now(),
                        //                LocalDateTime.now().plusDays(1), PaymentStatus.COMPLETE, GuestLogStatus.ACTIVE));

                        guestLogTable.setItems(data);
                        pagination.setPageCount(apiResponse.getTotalPages());
                        pagination.setCurrentPageIndex(page);

                        noOfEntries.setText("Showing Data 1 to " + apiResponse.getTotalPages() + " of " + apiResponse.getTotalElements() + " entries");

                        //                        for (TableColumn<GuestReservation, ?> column : guestLogTable.getColumns()) {
                        //                            column.setStyle("-fx-pref-height: 70px;");
                        //                        }

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
                                        if (item == GuestLogStatus.COMPLETE) {
                                            label.setStyle("-fx-text-fill: green; -fx-background-color: #e0f7e0; -fx-font-weight: bold; -fx-padding: 5px 10px; -fx-background-radius: 5;");
                                        } else if (item == GuestLogStatus.ACTIVE) {
                                            label.setStyle("-fx-text-fill: blue; -fx-background-color: #e0e0f7; -fx-font-weight: bold; -fx-padding: 5px; -fx-background-radius: 5;");
                                        }
                                        else if (item == GuestLogStatus.OVERDUE) {
                                            label.setStyle("-fx-text-fill: red; -fx-background-color: #f7e0e0; -fx-font-weight: bold; -fx-padding: 5px; -fx-background-radius: 5;");
                                        }
                                        else {
                                            label.setStyle("-fx-padding: 5px;");  // Default padding if not ACTIVE or COMPLETE
                                        }

                                        setGraphic(label);  // Set the Label as the graphic for this cell
                                    }
                                }
                            };
                        });

                        paymentStatus.setCellFactory(column -> {
                            return new TableCell<GuestReservation, PaymentStatus>() {
                                @Override
                                protected void updateItem(PaymentStatus item, boolean empty) {
                                    super.updateItem(item, empty);
                                    if (empty || item == null) {
                                        setText(null);
                                        setGraphic(null);  // Reset cell content
                                    } else {
                                        // Create a Label for the status
                                        Label label = new Label(item.toJson());
                                        label.setStyle("-fx-font-weight: bold; -fx-padding: 5px;");

                                        // Apply styles based on the status
                                        if (item == PaymentStatus.PAID) {
                                            label.setStyle("-fx-text-fill: green; -fx-background-color: #e0f7e0; -fx-font-weight: bold; -fx-padding: 5px 10px; -fx-background-radius: 5;");
                                        } else if (item == PaymentStatus.UNPAID) {
                                            label.setStyle("-fx-text-fill: red; -fx-background-color: #f7e0e0; -fx-font-weight: bold; -fx-padding: 5px 15px; -fx-background-radius: 5;");
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
//                    loadingStage.close();
                    Platform.runLater(() -> {
                        Utils.showAlertDialog(Alert.AlertType.ERROR,apiResponse.getResponseHeader().getResponseMessage(),apiResponse.getError() );
                    });
                }

            } catch (Exception e) {
//                loadingStage.close();
                Platform.runLater(() -> {
                    System.out.println(e);
                    Utils.showGeneralErrorDialog();
                });
            }

        }).start();
    }

    private Node createPage(int pageIndex) {
        loadPage(pageIndex, pageSize);
        return guestLogTable;
    }

    private <T> void setWrapCellFactory(TableColumn<T, String> column) {
        column.setCellFactory(c -> {
            return new javafx.scene.control.TableCell<T, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        Text text = new Text(item);
                        text.setWrappingWidth(100); // Set the wrapping width of the text (adjust as needed)
                        setGraphic(text);
                    }
                }
            };
        });
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
            utils.switchScreenWithGuestLog("/com/bmh/hotelmanagementsystem/room/single-guest-log-view.fxml", primaryStage,guestLog, "/com/bmh/hotelmanagementsystem/room/general-admin-guest-logs-view.fxml");
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

                String response = RestClient.post("/guestLog/filter?page=" + page + "&size=" + pageSize, jsonString);
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
                                    if(guestLog.getStatus() == GuestLogStatus.COMPLETE){
                                        room.append(guestLogRoom.getRoom().getRoomNumber());

                                        if (index < size - 1) {
                                            room.append(", ");
                                        }
                                    }
                                    else{
                                        if(guestLogRoom.getGuestLogStatus() == GuestLogStatus.ACTIVE){
                                            room.append(guestLogRoom.getRoom().getRoomNumber());

                                            if (index < size - 1) {
                                                room.append(", ");
                                            }
                                        }
                                    }

                                    index++;
                                }

                                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                                String checkInDate = guestLog.getCheckInDate().format(dateTimeFormatter);
                                String checkOutDate = "";
                                String expectedCheckOutDate = "";
                                if (guestLog.getCheckOutDate() != null) {
                                    checkOutDate = guestLog.getCheckOutDate().format(dateTimeFormatter);
                                }
                                if (guestLog.getExpectedCheckOutDate() != null) {
                                    expectedCheckOutDate = guestLog.getExpectedCheckOutDate().format(dateTimeFormatter);
                                }
                                GuestReservation guestReservation = new GuestReservation(guestLog.getGuestName(), room.toString(), checkInDate,
                                        checkOutDate, expectedCheckOutDate,guestLog.getPaymentStatus(), guestLog.getStatus(), guestLog);

                                data.add(guestReservation);
                            }
                            guestLogTable.setItems(data);
                            pagination.setPageCount(apiResponse.getTotalPages());
                            pagination.setCurrentPageIndex(page);
                            noOfEntries.setText("Showing Data 1 to " + apiResponse.getTotalPages() + " of " + apiResponse.getTotalElements() + " entries");
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


    @FXML
    protected void checkIn(){
        try {
            Utils utils = new Utils();
            utils.switchScreen("/com/bmh/hotelmanagementsystem/room/checkIn-view.fxml", primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void checkOut(){
        try {
            Utils utils = new Utils();
            utils.switchScreen("/com/bmh/hotelmanagementsystem/room/checkOut-view.fxml", primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void guestLog(){
        try {
            Utils utils = new Utils();
            utils.switchScreen("/com/bmh/hotelmanagementsystem/room/guest-log-view.fxml", primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
