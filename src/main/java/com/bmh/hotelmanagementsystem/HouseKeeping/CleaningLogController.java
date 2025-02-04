package com.bmh.hotelmanagementsystem.HouseKeeping;

import com.bmh.hotelmanagementsystem.BackendService.RestClient;
import com.bmh.hotelmanagementsystem.dto.HouseKeeping.CleaningRow;
import com.bmh.hotelmanagementsystem.BackendService.entities.*;
import com.bmh.hotelmanagementsystem.BackendService.entities.HouseKeeping.CleaningLog;
import com.bmh.hotelmanagementsystem.BackendService.entities.HouseKeeping.CleaningLogFilterRequest;
import com.bmh.hotelmanagementsystem.BackendService.entities.Room.Room;
import com.bmh.hotelmanagementsystem.BackendService.enums.CleaningStatus;
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
import javafx.scene.layout.Region;
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

public class CleaningLogController extends Controller {

    private Stage primaryStage;

    private String previousLocation;

    private Object data;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setData(Object data, String previousLocation){
        this.data = data;
        this.previousLocation = previousLocation;
    }

    @FXML
    private TableView<CleaningRow> cleaning_logs_table;
    @FXML
    private TableColumn<CleaningRow, Integer> room_column;
    @FXML
    private TableColumn<CleaningRow, CleaningStatus> status_column;
    @FXML
    private TableColumn<CleaningRow, String> assigned_on_column;
    @FXML
    private TableColumn<CleaningRow, String> completed_on_column;
    @FXML
    private TableColumn<CleaningRow, Void> viewMore;
//    @FXML
//    private TableColumn<CleaningRow, Void> assign_column;

    @FXML
    private ComboBox<Integer> room_comboBox;
    @FXML
    private ComboBox<String> status_comboBox;

    @FXML
    private DatePicker start_datePicker;
    @FXML
    private DatePicker end_datePicker;
    @FXML
    private Button apply;

    @FXML
    private Button create;

    private ObservableList<CleaningRow> logs = FXCollections.observableArrayList();

    private List<Room> allRooms = new ArrayList<>();


    public void initialize() {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        start_datePicker.setConverter(new javafx.util.StringConverter<LocalDate>() {
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

        end_datePicker.setConverter(new javafx.util.StringConverter<LocalDate>() {
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

        Stage loadingStage = showLoadingScreen(primaryStage);

        Platform.runLater(() -> loadingStage.show());

        new Thread(() -> {
            try {
                String response = RestClient.get("/room/");

                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());

                ApiResponse<Room> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponse<Room>>() {});

                if (apiResponse.getResponseHeader().getResponseCode().equals("00")) {
                    Platform.runLater(() -> {
                        ObservableList<Integer> roomNumber = FXCollections.observableArrayList();

                        roomNumber.add(null);
                        for (Room room : apiResponse.getData()) {
                            roomNumber.add(room.getRoomNumber());
                            allRooms.add(room);
                        }

                        room_comboBox.setItems(roomNumber);


                        ObservableList<String> cleaningStatus = FXCollections.observableArrayList();

                        cleaningStatus.add(null);
                        for (CleaningStatus status : CleaningStatus.values()) {
                            cleaningStatus.add(status.toJson());
                        }

                        status_comboBox.setItems(cleaningStatus);

                        apply.setOnAction(event -> apply());
                        create.setOnAction(event -> create());

                    });
                } else {
                    Platform.runLater(() -> {
                        Utils.showAlertDialog(Alert.AlertType.ERROR,apiResponse.getResponseHeader().getResponseMessage(),apiResponse.getError() );
                    });

                }

            } catch (Exception e) {
                Platform.runLater(() -> {
                    e.printStackTrace();
                    Utils.showGeneralErrorDialog();
                });
            }

            try {
                String response = RestClient.get("/cleaningLog/");

                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());

                ApiResponse<CleaningLog> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponse<CleaningLog>>() {});

                if (apiResponse.getResponseHeader().getResponseCode().equals("00")) {
                    Platform.runLater(() -> {
                        loadingStage.close();
                        room_column.setCellValueFactory(cellData -> cellData.getValue().room_columnProperty().asObject());
                        status_column.setCellValueFactory(cellData -> cellData.getValue().status_columnProperty());
                        assigned_on_column.setCellValueFactory(cellData -> cellData.getValue().assigned_on_columnProperty());
                        completed_on_column.setCellValueFactory(cellData -> cellData.getValue().completed_on_columnProperty());
                        viewMore.setCellFactory(assignCellFactory());

//                        cleaning_logs_table.setRowFactory(tableView -> {
//                            TableRow<CleaningRow> row = new TableRow<>();
//
//                            // Add a mouse click event listener to each row
//                            row.setOnMouseClicked(event -> {
//                                // Check if the row is not empty (i.e., not the header)
//                                if (!row.isEmpty()) {
//                                    CleaningRow clickedRow = row.getItem();  // Get the item (data) in the clicked row
//                                    showRoomDetails(clickedRow);
//                                }
//                            });
//
//                            return row;
//                        });
                        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        for (CleaningLog log : apiResponse.getData()){
                            String assignedOn = log.getAssignedOn().format(dateTimeFormatter);
                            String completedOn = "";
                            if (log.getCompletedOn() != null) {
                                completedOn = log.getCompletedOn().format(dateTimeFormatter);
                            }
                            CleaningRow cleaningRow = new CleaningRow(log.getRef(), log.getRoom().getRoomNumber(), log.getStatus(), assignedOn, completedOn, log);
                            logs.add(cleaningRow);
                        }

                        cleaning_logs_table.setItems(logs);

                    });
                } else {
                    Platform.runLater(() -> {
                        loadingStage.close();
                        Utils.showAlertDialog(Alert.AlertType.ERROR,apiResponse.getResponseHeader().getResponseMessage(),apiResponse.getError() );
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

    private Callback<TableColumn<CleaningRow, Void>, TableCell<CleaningRow, Void>> assignCellFactory() {
        return new Callback<>() {
            @Override
            public TableCell<CleaningRow, Void> call(TableColumn<CleaningRow, Void> param) {
                final TableCell<CleaningRow, Void> cell = new TableCell<CleaningRow, Void>() {

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
                            CleaningRow log = getTableView().getItems().get(getIndex());
                            assign(log);
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

    public void apply(){
        Stage loadingStage = Utils.showLoadingScreen(primaryStage);
        loadingStage.show();

        new Thread(() -> {

            try {
                Integer selectedRoom = room_comboBox.getSelectionModel().getSelectedItem();

                CleaningLogFilterRequest request = new CleaningLogFilterRequest();
                request.setStatus(status_comboBox.getSelectionModel().getSelectedItem() != null? CleaningStatus.valueOf(status_comboBox.getSelectionModel().getSelectedItem()) : null);
                request.setAssignedOn(start_datePicker.getValue() != null ? start_datePicker.getValue().atStartOfDay() : null);
                request.setCompletedOn(end_datePicker.getValue() != null ? end_datePicker.getValue().atTime(23, 59, 0) : null);
                try{
                    request.setRoomNumber(room_comboBox.getSelectionModel().getSelectedItem());
                }
                catch (NullPointerException e){
                    request.setRoomNumber(0);
                }


                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());

                String jsonString = objectMapper.writeValueAsString(request);

                String response = RestClient.post("/cleaningLog/filter", jsonString);
                ApiResponse<CleaningLog> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponse<CleaningLog>>() {});


                Platform.runLater(() -> {
                    try {
                        if (apiResponse.getResponseHeader().getResponseCode().equals("00")) {
                            loadingStage.close();

                            logs.clear();
                            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                            for (CleaningLog log : apiResponse.getData()){
                                String assignedOn = log.getAssignedOn().format(dateTimeFormatter);
                                String completedOn = "";
                                if (log.getCompletedOn() != null) {
                                    completedOn = log.getCompletedOn().format(dateTimeFormatter);
                                }
                                CleaningRow cleaningRow = new CleaningRow(log.getRef(), log.getRoom().getRoomNumber(), log.getStatus(), assignedOn, completedOn,log);
                                logs.add(cleaningRow);
                            }

                            cleaning_logs_table.setItems(logs);
                        } else {
                            loadingStage.close();
                            Utils.showAlertDialog(Alert.AlertType.ERROR, apiResponse.getResponseHeader().getResponseMessage(),apiResponse.getError() );
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        loadingStage.close();
                        Utils.showGeneralErrorDialog();
                    }
                });


            } catch (Exception e) {
                Platform.runLater(() -> {
                    loadingStage.close();
                    Utils.showGeneralErrorDialog();
                });
            }
        }).start();
    }


    public void create(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bmh/hotelmanagementsystem/HouseKeeping/create_cleaning_log.fxml"));
            Region form = loader.load();

            Stage formStage = new Stage();
            formStage.initModality(Modality.APPLICATION_MODAL);
            formStage.setTitle("Fill out the Form");

            CreateCleaningLogController controller = loader.getController();
            controller.setPrimaryStage(formStage);
            controller.setData(allRooms, "/com/bmh/hotelmanagementsystem/HouseKeeping/cleaning-log-view.fxml");

            Scene formScene = new Scene(form);
            formStage.setScene(formScene);
            formStage.showAndWait();

            refreshMainStage();
        } catch (Exception e) {
            e.printStackTrace();
            Utils.showGeneralErrorDialog();
        }
    }

    public void assign(CleaningRow cleaningRow){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bmh/hotelmanagementsystem/HouseKeeping/update_cleaning_log.fxml"));
            Region form = loader.load();

            Stage formStage = new Stage();
            formStage.initModality(Modality.APPLICATION_MODAL);
            formStage.setTitle("Fill out the Form");

            UpdateCleaningLogController controller = loader.getController();
            controller.setPrimaryStage(formStage);
            controller.setData(cleaningRow.getCleaningLog(), "/com/bmh/hotelmanagementsystem/HouseKeeping/cleaning-log-view.fxml");

            Scene formScene = new Scene(form);
            formStage.setScene(formScene);
            formStage.showAndWait();

            refreshMainStage();
        } catch (Exception e) {
            e.printStackTrace();
            Utils.showGeneralErrorDialog();
        }

    }

    private void refreshMainStage() {
        try {
            Utils utils = new Utils();
            utils.switchScreen("/com/bmh/hotelmanagementsystem/HouseKeeping/cleaning-log-view.fxml", primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
            Utils.showGeneralErrorDialog();
        }
    }
}
