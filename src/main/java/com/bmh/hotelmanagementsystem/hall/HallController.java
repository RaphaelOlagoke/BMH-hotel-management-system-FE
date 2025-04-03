package com.bmh.hotelmanagementsystem.hall;

import com.bmh.hotelmanagementsystem.BackendService.RestClient;
import com.bmh.hotelmanagementsystem.BackendService.entities.ApiResponse;
import com.bmh.hotelmanagementsystem.BackendService.entities.ApiResponseSingleData;
import com.bmh.hotelmanagementsystem.BackendService.entities.Room.*;
import com.bmh.hotelmanagementsystem.BackendService.entities.hall.HallLog;
import com.bmh.hotelmanagementsystem.BackendService.entities.hall.HallLogFilterRequest;
import com.bmh.hotelmanagementsystem.BackendService.enums.*;
import com.bmh.hotelmanagementsystem.BackendService.utils.AuthFileCache;
import com.bmh.hotelmanagementsystem.Controller;
import com.bmh.hotelmanagementsystem.TokenStorage;
import com.bmh.hotelmanagementsystem.Utils;
import com.bmh.hotelmanagementsystem.dto.hall.HallReservation;
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

public class HallController extends Controller {

    private Stage primaryStage;

    @FXML
    private Label user;

    @FXML
    private Label todayDate;

    @FXML
    private Pagination pagination;

    @FXML
    private TableView<HallReservation> hallLogTable;
    @FXML
    private TableColumn<HallReservation, String> guestName;
    @FXML
    private TableColumn<HallReservation, String> hall;
    @FXML
    private TableColumn<HallReservation, String> startDate;
    @FXML
    private TableColumn<HallReservation, String> endDate;
    @FXML
    private TableColumn<HallReservation, PaymentStatus> paymentStatus;
    @FXML
    private TableColumn<HallReservation, HallLogStatus> status;
    @FXML
    private TableColumn<HallReservation, Void> viewMore;

    @FXML
    private ComboBox<String> status_comboBox;
    @FXML
    private ComboBox<String> hall_comboBox;
    @FXML
    private ComboBox<String> paymentStatus_comboBox;

    @FXML
    private DatePicker start_date;
    @FXML
    private DatePicker end_date;

    @FXML
    private Button apply;

    @FXML
    private Button bookHall;

    @FXML
    private Label noOfEntries;

    private List<HallLog> hallLogs = new ArrayList<>();


    private ObservableList<HallReservation> data = FXCollections.observableArrayList();


    private int pageSize = 10;
    private int page = 0;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    public void initialize()  {
//        hallLogTable.setPrefWidth(510);
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

        ObservableList<String> hall_log_status = FXCollections.observableArrayList();

        hall_log_status.add(null);
        for (HallLogStatus hallLogStatus : HallLogStatus.values()) {
            hall_log_status.add(hallLogStatus.toJson());
        }

        status_comboBox.setItems(hall_log_status);


        ObservableList<String> payment_status = FXCollections.observableArrayList();

        payment_status.add(null);
        payment_status.add(PaymentStatus.PAID.toJson());
        payment_status.add(PaymentStatus.UNPAID.toJson());

        paymentStatus_comboBox.setItems(payment_status);

        ObservableList<String> hall_types = FXCollections.observableArrayList();

        hall_types.add(null);
        for (HallType hallType : HallType.values()) {
            hall_types.add(hallType.toJson());
        }

        hall_comboBox.setItems(hall_types);

        apply.setOnAction(event -> apply(page, pageSize));
        bookHall.setOnAction(event -> bookHall());


        guestName.setCellValueFactory(cellData -> cellData.getValue().guestNameProperty());
        hall.setCellValueFactory(cellData -> cellData.getValue().hallProperty());
        startDate.setCellValueFactory(cellData -> cellData.getValue().startDateProperty());
        endDate.setCellValueFactory(cellData -> cellData.getValue().endDateProperty());
        paymentStatus.setCellValueFactory(cellData -> cellData.getValue().paymentStatusProperty());
        status.setCellValueFactory(cellData -> cellData.getValue().statusProperty());

        viewMore.setCellFactory(createViewMoreButtonCellFactory());

        setWrapCellFactory(startDate);
        setWrapCellFactory(endDate);

        pagination.setPageFactory(this::createPage);
    }

    private void loadPage(int page, int size) {
//        Stage loadingStage = showLoadingScreen(primaryStage);
//
//        loadingStage.show();

        apply(page, size);
    }

    private Node createPage(int pageIndex) {
        loadPage(pageIndex, pageSize);
        return hallLogTable;
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

    private Callback<TableColumn<HallReservation, Void>, TableCell<HallReservation, Void>> createViewMoreButtonCellFactory() {
        return new Callback<>() {
            @Override
            public TableCell<HallReservation, Void> call(TableColumn<HallReservation, Void> param) {
                final TableCell<HallReservation, Void> cell = new TableCell<HallReservation, Void>() {

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
                            HallReservation hallReservation = getTableView().getItems().get(getIndex());
                            showDetails(hallReservation);  // Call a method to handle the button click
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
    private void showDetails(HallReservation hallReservation) {
//        System.out.println("Viewing more details for: " + guest.getGuestName());
        HallLog hallLog  = hallReservation.getHallLog();
        try {
            Utils utils = new Utils();
            utils.switchScreenWithData("/com/bmh/hotelmanagementsystem/hall/single-hall-log.fxml", primaryStage,hallLog, "/com/bmh/hotelmanagementsystem/hall/hall-logs-view.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void apply(int page, int size) {
        Stage loadingStage = Utils.showLoadingScreen(primaryStage);

        loadingStage.show();

        new Thread(() -> {
            try {
                HallLogFilterRequest request = new HallLogFilterRequest();
                request.setHallType(hall_comboBox.getSelectionModel().getSelectedItem() != null? HallType.valueOf(hall_comboBox.getSelectionModel().getSelectedItem()) : null);
                request.setStatus(status_comboBox.getSelectionModel().getSelectedItem() != null? HallLogStatus.valueOf(status_comboBox.getSelectionModel().getSelectedItem()) : null);
                request.setPaymentStatus(paymentStatus_comboBox.getSelectionModel().getSelectedItem() != null? PaymentStatus.valueOf(paymentStatus_comboBox.getSelectionModel().getSelectedItem()) : null);

                request.setStartDate(start_date.getValue() != null ? start_date.getValue().atStartOfDay() : null);
                request.setEndDate(end_date.getValue() != null ? end_date.getValue().atTime(23, 59, 0) : null);



                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());

                String jsonString = objectMapper.writeValueAsString(request);

                String response = RestClient.post("/hallLog/filter?page=" + page + "&size=" + size, jsonString);

                ApiResponse<HallLog> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponse<HallLog>>() {});

                if (apiResponse.getResponseHeader().getResponseCode().equals("00")) {
                    Platform.runLater(() -> {
                        loadingStage.close();

                        data.clear();


                        for (HallLog hallLog : apiResponse.getData()){

                            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                            String startDate = "";
                            String endDate = "";
                            if (hallLog.getStartDate() != null) {
                                startDate = hallLog.getStartDate().format(dateTimeFormatter);
                            }
                            if (hallLog.getEndDate() != null) {
                                endDate = hallLog.getEndDate().format(dateTimeFormatter);
                            }
                            HallReservation hallReservation = new HallReservation(hallLog.getGuestName(), hallLog.getHall(), startDate,
                                    endDate,hallLog.getPaymentStatus(), hallLog.getStatus(), hallLog);

                            data.add(hallReservation);
                        }
                        //        data.add( new GuestReservation("Jane Smith", "105, 309", LocalDateTime.now(),
                        //                LocalDateTime.now().plusDays(1), PaymentStatus.COMPLETE, GuestLogStatus.ACTIVE));

                        hallLogTable.setItems(data);
                        pagination.setPageCount(apiResponse.getTotalPages());
                        pagination.setCurrentPageIndex(page);

                        noOfEntries.setText("Showing Data 1 to " + apiResponse.getTotalPages() + " of " + apiResponse.getTotalElements() + " entries");

                        //                        for (TableColumn<GuestReservation, ?> column : guestLogTable.getColumns()) {
                        //                            column.setStyle("-fx-pref-height: 70px;");
                        //                        }

                        status.setCellFactory(column -> {
                            return new TableCell<HallReservation, HallLogStatus>() {
                                @Override
                                protected void updateItem(HallLogStatus item, boolean empty) {
                                    super.updateItem(item, empty);
                                    if (empty || item == null) {
                                        setText(null);
                                        setGraphic(null);  // Reset cell content
                                    } else {
                                        // Create a Label for the status
                                        Label label = new Label(item.toJson());
                                        label.setStyle("-fx-font-weight: bold; -fx-padding: 5px;");

                                        // Apply styles based on the status
                                        if (item == HallLogStatus.COMPLETE) {
                                            label.setStyle("-fx-text-fill: green; -fx-background-color: #e0f7e0; -fx-font-weight: bold; -fx-padding: 5px 10px; -fx-background-radius: 5;");
                                        } else if (item == HallLogStatus.ACTIVE) {
                                            label.setStyle("-fx-text-fill: blue; -fx-background-color: #e0e0f7; -fx-font-weight: bold; -fx-padding: 5px; -fx-background-radius: 5;");
                                        }
                                        else if (item == HallLogStatus.UPCOMING) {
                                            label.setStyle("-fx-text-fill: #8B8000; -fx-background-color: #fff9c4; -fx-font-weight: bold; -fx-padding: 5px 15px; -fx-background-radius: 5;");
                                        }
                                        else if (item == HallLogStatus.CANCELED) {
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
                            return new TableCell<HallReservation, PaymentStatus>() {
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
    }

    public void bookHall(){
        try {
            Utils utils = new Utils();
            utils.switchScreen("/com/bmh/hotelmanagementsystem/hall/book_hall.fxml", primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
