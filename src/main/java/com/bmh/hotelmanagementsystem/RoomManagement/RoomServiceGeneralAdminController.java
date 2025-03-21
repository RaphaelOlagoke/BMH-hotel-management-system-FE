package com.bmh.hotelmanagementsystem.RoomManagement;

import com.bmh.hotelmanagementsystem.BackendService.RestClient;
import com.bmh.hotelmanagementsystem.BackendService.entities.ApiResponse;
import com.bmh.hotelmanagementsystem.BackendService.entities.Room.RoomService;
import com.bmh.hotelmanagementsystem.Controller;
import com.bmh.hotelmanagementsystem.HouseKeeping.UpdateCleaningLogController;
import com.bmh.hotelmanagementsystem.Utils;
import com.bmh.hotelmanagementsystem.dto.room.RoomServiceRow;
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
import java.text.DecimalFormat;

import static com.bmh.hotelmanagementsystem.Utils.showLoadingScreen;

public class RoomServiceGeneralAdminController extends Controller {

    private Stage primaryStage;

    private String previousLocation;

    private Object data;

    @FXML
    private TableView<RoomServiceRow> roomServiceTable;
    @FXML
    private TableColumn<RoomServiceRow, String> name_column;
    @FXML
    private TableColumn<RoomServiceRow, String> price_column;
//    @FXML
//    private TableColumn<RoomServiceRow, Void> edit_column;

    @FXML
    private Button create;


    private ObservableList<RoomServiceRow> roomServiceData = FXCollections.observableArrayList();


    DecimalFormat formatter = new DecimalFormat("#,###.00");

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setData(Object data, String previousLocation) {
        this.data =  data;
        this.previousLocation = previousLocation;
    }

    public void initialize() {

        roomServiceData.clear();

        Stage loadingStage = showLoadingScreen(primaryStage);

        Platform.runLater(() -> loadingStage.show());

        new Thread(() -> {
            try {
                String response = RestClient.get("/room/service/filter?query=" + "");

                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());

                // Convert JSON string to ApiResponse
                ApiResponse<RoomService> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponse<RoomService>>() {
                });


                if (apiResponse.getResponseHeader().getResponseCode().equals("00")) {
                    Platform.runLater(() -> {
                        loadingStage.close();

                        name_column.setCellValueFactory(cellData -> cellData.getValue().name_columnProperty());
                        price_column.setCellValueFactory(cellData -> cellData.getValue().price_columnProperty());
//                        edit_column.setCellFactory(createViewMoreButtonCellFactory());

                        for (RoomService roomService : apiResponse.getData()){
                            RoomServiceRow roomServiceRow = new RoomServiceRow(roomService.getService(), formatter.format(roomService.getPrice()), roomService);
                            roomServiceData.add(roomServiceRow);
                        }

                        roomServiceTable.setItems(roomServiceData);

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
                    System.out.println(e);
                    Utils.showGeneralErrorDialog();
                });
            }
        }).start();

//        create.setOnAction(event -> createRoomService());

    }

    private Callback<TableColumn<RoomServiceRow, Void>, TableCell<RoomServiceRow, Void>> createViewMoreButtonCellFactory() {
        return new Callback<>() {
            @Override
            public TableCell<RoomServiceRow, Void> call(TableColumn<RoomServiceRow, Void> param) {
                final TableCell<RoomServiceRow, Void> cell = new TableCell<RoomServiceRow, Void>() {

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
                            RoomServiceRow roomServiceRow = getTableView().getItems().get(getIndex());
//                            editRoomService(roomServiceRow);
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

//    public void createRoomService(){
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bmh/hotelmanagementsystem/room/create_room_service.fxml"));
//            Region form = loader.load();
//
//            Stage formStage = new Stage();
//            formStage.initModality(Modality.APPLICATION_MODAL);
//            formStage.setTitle("Fill out the Form");
//
//            Controller controller = loader.getController();
//            controller.setPrimaryStage(formStage);
//
//            controller.setData(null, "/com/bmh/hotelmanagementsystem/room/admin-room-service-view.fxml");
//
//
//            Scene formScene = new Scene(form);
//            formStage.setScene(formScene);
//            formStage.showAndWait();
//
//            initialize();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            Utils.showGeneralErrorDialog();
//        }
//    }

//    public void editRoomService(RoomServiceRow roomServiceRow){
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bmh/hotelmanagementsystem/room/update_room_service.fxml"));
//            Region form = loader.load();
//
//            Stage formStage = new Stage();
//            formStage.initModality(Modality.APPLICATION_MODAL);
//            formStage.setTitle("Fill out the Form");
//
//            Controller controller = loader.getController();
//            controller.setPrimaryStage(formStage);
//            controller.setData(roomServiceRow.getRoomService(), "/com/bmh/hotelmanagementsystem/room/admin-room-service-view.fxml");
//
//            Scene formScene = new Scene(form);
//            formStage.setScene(formScene);
//            formStage.showAndWait();
//
//            initialize();
//        } catch (Exception e) {
//            e.printStackTrace();
//            Utils.showGeneralErrorDialog();
//        }
//
//    }
}
