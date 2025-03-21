package com.bmh.hotelmanagementsystem.RoomManagement;

import com.bmh.hotelmanagementsystem.BackendService.RestClient;
import com.bmh.hotelmanagementsystem.BackendService.enums.PaymentStatus;
import com.bmh.hotelmanagementsystem.BackendService.enums.StockItemCategory;
import com.bmh.hotelmanagementsystem.dto.restaurant.OrderDetails;
import com.bmh.hotelmanagementsystem.dto.room.GuestReservation;
import com.bmh.hotelmanagementsystem.dto.room.RoomManagement;
import com.bmh.hotelmanagementsystem.BackendService.entities.*;
import com.bmh.hotelmanagementsystem.BackendService.entities.Room.Room;
import com.bmh.hotelmanagementsystem.BackendService.entities.Room.RoomFilterRequest;
import com.bmh.hotelmanagementsystem.BackendService.enums.RoomStatus;
import com.bmh.hotelmanagementsystem.BackendService.enums.RoomType;
import com.bmh.hotelmanagementsystem.Controller;
import com.bmh.hotelmanagementsystem.Utils;
import com.bmh.hotelmanagementsystem.restaurant.CreateOrderController;
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
import java.util.ArrayList;
import java.util.List;

import static com.bmh.hotelmanagementsystem.Utils.showLoadingScreen;

public class GeneralAdminRoomManagementController extends Controller {

    private Stage primaryStage;

    private String previousLocation;
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    private TableView<RoomManagement> rooms_table;
    @FXML
    private TableColumn<RoomManagement, String> room_number_column;
    @FXML
    private TableColumn<RoomManagement, RoomType> type_column;
    @FXML
    private TableColumn<RoomManagement, RoomStatus> status_column;
    @FXML
    private TableColumn<RoomManagement, Boolean> needs_cleaning_column;
    @FXML
    private TableColumn<RoomManagement, Boolean> needs_maintenance_column;
    @FXML
    private TableColumn<RoomManagement, Boolean> archive;
    @FXML
    private TableColumn<RoomManagement, Void> viewMore;

    @FXML
    private Pagination pagination;

    @FXML
    private ComboBox<String> room_type;

    @FXML
    private ComboBox<String> room_status;
    @FXML
    private ComboBox<Integer> room_number;
    @FXML
    private ComboBox<Boolean> needs_cleaning;
    @FXML
    private ComboBox<Boolean> needs_maintenance;
    @FXML
    private ComboBox<Boolean> archive_comboBox;
    @FXML
    private Button apply;
//    @FXML
//    private Button create;
    @FXML
    private Button inventory;

    private int pageSize = 10;
    private int page = 0;


    List<Room> rooms = new ArrayList<>();
    List<Room> allRooms = new ArrayList<>();

    private ObservableList<RoomManagement> data = FXCollections.observableArrayList();

    public void initialize() {
        pagination.setPageFactory(this::createPage);

        room_number_column.setCellValueFactory(cellData -> cellData.getValue().room_number_columnProperty());
        type_column.setCellValueFactory(cellData -> cellData.getValue().type_columnProperty());
        status_column.setCellValueFactory(cellData -> cellData.getValue().status_columnProperty());
        needs_cleaning_column.setCellValueFactory(cellData -> cellData.getValue().needs_cleaning_columnProperty());
        needs_maintenance_column.setCellValueFactory(cellData -> cellData.getValue().needs_maintenance_columnProperty());
        archive.setCellValueFactory(cellData -> cellData.getValue().archiveProperty());

        viewMore.setCellFactory(createViewMoreButtonCellFactory());

        status_column.setCellFactory(column -> {
            return new TableCell<RoomManagement, RoomStatus>() {
                @Override
                protected void updateItem(RoomStatus item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);  // Reset cell content
                    } else {
                        // Create a Label for the status
                        Label label = new Label(item.toJson());
                        label.setStyle("-fx-font-weight: bold; -fx-padding: 5px;");

                        // Apply styles based on the status
                        if (item == RoomStatus.AVAILABLE) {
                            label.setStyle("-fx-text-fill: green; -fx-background-color: #e0f7e0; -fx-font-weight: bold; -fx-padding: 5px 10px; -fx-background-radius: 5;");
                        } else if (item == RoomStatus.OCCUPIED) {
                            label.setStyle("-fx-text-fill: blue; -fx-background-color: #e0e0f7; -fx-font-weight: bold; -fx-padding: 5px; -fx-background-radius: 5;");
                        } else {
                            label.setStyle("-fx-padding: 5px;");  // Default padding if not ACTIVE or COMPLETE
                        }

                        setGraphic(label);  // Set the Label as the graphic for this cell
                    }
                }
            };
        });

        needs_cleaning_column.setCellFactory(column -> {
            return new TableCell<RoomManagement, Boolean>() {
                @Override
                protected void updateItem(Boolean item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);  // Reset cell content
                    } else {
                        // Create a Label for the status
                        Label label = new Label(item.toString());
                        label.setStyle("-fx-font-weight: bold; -fx-padding: 5px;");

                        // Apply styles based on the status
                        if (!item) {
                            label.setText("NO");
                            label.setStyle("-fx-text-fill: green; -fx-background-color: #e0f7e0; -fx-font-weight: bold; -fx-padding: 5px 10px; -fx-background-radius: 5;");
                        } else {
                            label.setText("YES");
                            label.setStyle("-fx-text-fill: red; -fx-background-color: #f7e0e0; -fx-font-weight: bold; -fx-padding: 5px 15px; -fx-background-radius: 5;");
                        }

                        setGraphic(label);  // Set the Label as the graphic for this cell
                    }
                }
            };
        });

        needs_maintenance_column.setCellFactory(column -> {
            return new TableCell<RoomManagement, Boolean>() {
                @Override
                protected void updateItem(Boolean item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);  // Reset cell content
                    } else {
                        // Create a Label for the status
                        Label label = new Label(item.toString());
                        label.setStyle("-fx-font-weight: bold; -fx-padding: 5px;");

                        // Apply styles based on the status
                        if (!item) {
                            label.setText("NO");
                            label.setStyle("-fx-text-fill: green; -fx-background-color: #e0f7e0; -fx-font-weight: bold; -fx-padding: 5px 10px; -fx-background-radius: 5;");
                        } else {
                            label.setText("YES");
                            label.setStyle("-fx-text-fill: red; -fx-background-color: #f7e0e0; -fx-font-weight: bold; -fx-padding: 5px 15px; -fx-background-radius: 5;");
                        }

                        setGraphic(label);  // Set the Label as the graphic for this cell
                    }
                }
            };
        });

        archive.setCellFactory(column -> {
            return new TableCell<RoomManagement, Boolean>() {
                @Override
                protected void updateItem(Boolean item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);  // Reset cell content
                    } else {
                        // Create a Label for the status
                        Label label = new Label(item.toString());
                        label.setStyle("-fx-font-weight: bold; -fx-padding: 5px;");

                        // Apply styles based on the status
                        if (!item) {
                            label.setText("NO");
                            label.setStyle("-fx-text-fill: green; -fx-background-color: #e0f7e0; -fx-font-weight: bold; -fx-padding: 5px 10px; -fx-background-radius: 5;");
                        } else {
                            label.setText("YES");
                            label.setStyle("-fx-text-fill: red; -fx-background-color: #f7e0e0; -fx-font-weight: bold; -fx-padding: 5px 15px; -fx-background-radius: 5;");
                        }

                        setGraphic(label);  // Set the Label as the graphic for this cell
                    }
                }
            };
        });

        rooms_table.setRowFactory(tableView -> {
            TableRow<RoomManagement> row = new TableRow<>();

            // Add a mouse click event listener to each row
            row.setOnMouseClicked(event -> {
                // Check if the row is not empty (i.e., not the header)
                if (!row.isEmpty()) {
                    RoomManagement clickedRow = row.getItem();  // Get the item (data) in the clicked row
                    showRoomDetails(clickedRow);
                }
            });

            return row;
        });
    }

    private void loadPage(int page, int size) {

        Stage loadingStage = showLoadingScreen(primaryStage);

        Platform.runLater(() -> loadingStage.show());

        new Thread(() -> {
            try {
                Integer selectedRoom = room_number.getSelectionModel().getSelectedItem();

                RoomFilterRequest request = new RoomFilterRequest();
                request.setRoomType(room_type.getSelectionModel().getSelectedItem() != null? RoomType.valueOf(room_type.getSelectionModel().getSelectedItem()) : null);
                request.setRoomStatus(room_status.getSelectionModel().getSelectedItem() != null? RoomStatus.valueOf(room_status.getSelectionModel().getSelectedItem()) : null);
                request.setNeedsCleaning(needs_cleaning.getSelectionModel().getSelectedItem() != null? needs_cleaning.getSelectionModel().getSelectedItem() : null);
                request.setNeedsMaintenance(needs_maintenance.getSelectionModel().getSelectedItem() != null? needs_maintenance.getSelectionModel().getSelectedItem() : null);
                request.setArchived(archive_comboBox.getSelectionModel().getSelectedItem() != null? archive_comboBox.getSelectionModel().getSelectedItem() : null);

                try{
                    request.setRoomNumber(room_number.getSelectionModel().getSelectedItem());
                }
                catch (NullPointerException e){
                    request.setRoomNumber(0);
                }


                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());

                String jsonString = objectMapper.writeValueAsString(request);

                String response = RestClient.post("/room/find?page=" + page + "&size=" + pageSize, jsonString);

                ApiResponse<Room> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponse<Room>>() {});

                if (apiResponse.getResponseHeader().getResponseCode().equals("00")) {
                    Platform.runLater(() -> {
                        loadingStage.close();
                        ObservableList<String> roomStatus = FXCollections.observableArrayList();

                        roomStatus.add(null);
                        for (RoomStatus status : RoomStatus.values()) {
                            roomStatus.add(status.toJson());
                        }

                        room_status.setItems(roomStatus);


                        ObservableList<String> roomType = FXCollections.observableArrayList();

                        roomType.add(null);
                        for (RoomType type : RoomType.values()) {
                            roomType.add(type.toJson());
                        }

                        room_type.setItems(roomType);


                        ObservableList<Integer> listOfRooms = FXCollections.observableArrayList();
                        listOfRooms.add(null);

                        ObservableList<Boolean> booleanVales = FXCollections.observableArrayList();
                        booleanVales.add(null);
                        booleanVales.add(true);
                        booleanVales.add(false);

                        needs_cleaning.setItems(booleanVales);
                        needs_maintenance.setItems(booleanVales);
                        archive_comboBox.setItems(booleanVales);

                        //        List<Room> allRooms = new ArrayList<>();

                        apply.setOnAction(event -> apply());

//                        create.setOnAction(event -> createRoom());

                        inventory.setOnAction(event -> inventory());

                        allRooms = apiResponse.getData();
                        rooms = apiResponse.getData();

                        for (Room room : allRooms) {
                            listOfRooms.add(room.getRoomNumber());
                        }

                        room_number.setItems(listOfRooms);

                        pagination.setPageCount(apiResponse.getTotalPages());
                        pagination.setCurrentPageIndex(page);

                        data.clear();

                        for (Room room : rooms){
                            RoomManagement roomManagement = new RoomManagement(String.valueOf(room.getRoomNumber()), room.getRoomType(), room.getRoomStatus(), room.getNeedsCleaning(), room.getNeedsMaintenance(), room.getArchived(), room);
                            data.add(roomManagement);
                        }

                        rooms_table.setItems(data);

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

    private Node createPage(int pageIndex) {
        loadPage(pageIndex, pageSize);
        return rooms_table;
    }

    private Callback<TableColumn<RoomManagement, Void>, TableCell<RoomManagement, Void>> createViewMoreButtonCellFactory() {
        return new Callback<>() {
            @Override
            public TableCell<RoomManagement, Void> call(TableColumn<RoomManagement, Void> param) {
                final TableCell<RoomManagement, Void> cell = new TableCell<RoomManagement, Void>() {

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
                            RoomManagement room = getTableView().getItems().get(getIndex());
                            showRoomDetails(room);  // Call a method to handle the button click
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
    private void showRoomDetails(RoomManagement roomManagement) {
        Room room = roomManagement.getRoom();
        try {
            Utils utils = new Utils();
            utils.switchScreenWithData("/com/bmh/hotelmanagementsystem/room/general-admin-singleRoom-view.fxml", primaryStage,room, "/com/bmh/hotelmanagementsystem/room/general-admin-room-management-view.fxml");
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Something went wrong");
            alert.showAndWait();
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
                Integer selectedRoom = room_number.getSelectionModel().getSelectedItem();

                RoomFilterRequest request = new RoomFilterRequest();
                request.setRoomType(room_type.getSelectionModel().getSelectedItem() != null? RoomType.valueOf(room_type.getSelectionModel().getSelectedItem()) : null);
                request.setRoomStatus(room_status.getSelectionModel().getSelectedItem() != null? RoomStatus.valueOf(room_status.getSelectionModel().getSelectedItem()) : null);
                request.setNeedsCleaning(needs_cleaning.getSelectionModel().getSelectedItem() != null? needs_cleaning.getSelectionModel().getSelectedItem() : null);
                request.setNeedsMaintenance(needs_maintenance.getSelectionModel().getSelectedItem() != null? needs_maintenance.getSelectionModel().getSelectedItem() : null);
                request.setArchived(archive_comboBox.getSelectionModel().getSelectedItem() != null? archive_comboBox.getSelectionModel().getSelectedItem() : null);

                try{
                    request.setRoomNumber(room_number.getSelectionModel().getSelectedItem());
                }
                catch (NullPointerException e){
                    request.setRoomNumber(0);
                }


                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());

                String jsonString = objectMapper.writeValueAsString(request);

                String response = RestClient.post("/room/find?page=" + page + "&size=" + pageSize, jsonString);
                ApiResponse<Room> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponse<Room>>() {});


                Platform.runLater(() -> {
                    try {
                        if (apiResponse.getResponseHeader().getResponseCode().equals("00")) {
                            loadingStage.close();

                            rooms = apiResponse.getData();

                            data.clear();
                            for (Room room : rooms){
                                RoomManagement roomManagement = new RoomManagement(String.valueOf(room.getRoomNumber()), room.getRoomType(), room.getRoomStatus(), room.getNeedsCleaning(), room.getNeedsMaintenance(), room.getArchived(), room);
                                data.add(roomManagement);
                            }
                            rooms_table.setItems(data);
                            pagination.setPageCount(apiResponse.getTotalPages());
                            pagination.setCurrentPageIndex(page);
                        } else {
                            loadingStage.close();

                            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                            errorAlert.setTitle(apiResponse.getResponseHeader().getResponseMessage());
                            errorAlert.setContentText(apiResponse.getError());
                            errorAlert.showAndWait();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        loadingStage.close();

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

//    public void createRoom(){
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bmh/hotelmanagementsystem/room/create_room.fxml"));
//            Region form = loader.load();
//
//            Stage formStage = new Stage();
//            formStage.initModality(Modality.APPLICATION_MODAL);
//            formStage.setTitle("Fill out the Form");
//
//            CreateRoomController controller = loader.getController();
//            controller.setPrimaryStage(formStage);
//
//            controller.setData(null, "/com/bmh/hotelmanagementsystem/room/room-management-view.fxml");
//
//
//            Scene formScene = new Scene(form);
//            formStage.setScene(formScene);
//            formStage.showAndWait();
//
//            apply();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            Utils.showGeneralErrorDialog();
//        }
//    }

    public void inventory(){
        try {
            Utils utils = new Utils();
            utils.switchScreenWithData("/com/bmh/hotelmanagementsystem/components/request-inventory.fxml", primaryStage, StockItemCategory.ROOM, "/com/bmh/hotelmanagementsystem/room/general-admin-room-management-view.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
