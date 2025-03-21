package com.bmh.hotelmanagementsystem.RoomManagement;

import com.bmh.hotelmanagementsystem.BMHApplication;
import com.bmh.hotelmanagementsystem.BackendService.enums.LoginDepartment;
import com.bmh.hotelmanagementsystem.TokenStorage;
import com.bmh.hotelmanagementsystem.dto.room.MaintenanceRow;
import com.bmh.hotelmanagementsystem.BackendService.entities.Room.Maintenance;
import com.bmh.hotelmanagementsystem.BackendService.entities.Room.Room;
import com.bmh.hotelmanagementsystem.BackendService.enums.MaintenanceStatus;
import com.bmh.hotelmanagementsystem.Controller;
import com.bmh.hotelmanagementsystem.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;
import java.util.Collections;

public class MaintenanceHistoryController extends Controller {

    private Stage primaryStage;

    private String previousLocation;
    private Room data;

    @FXML
    private Label maintenance_roomNumber;
    @FXML
    private Button back;
    @FXML
    private TableView<MaintenanceRow> maintenance_table;
    @FXML
    private TableColumn<MaintenanceRow, String> ref_column;
    @FXML
    private TableColumn<MaintenanceRow, String> description_column;
    @FXML
    private TableColumn<MaintenanceRow, Double> cost_column;
    @FXML
    private TableColumn<MaintenanceRow, MaintenanceStatus> status_column;
    @FXML
    private TableColumn<MaintenanceRow, String> created_on_column;
    @FXML
    private TableColumn<MaintenanceRow, String> completed_on_column;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    private ObservableList<MaintenanceRow> tableData = FXCollections.observableArrayList();

    public void setData(Object data, String previousLocation){
        this.data = (Room) data;
        this.previousLocation = previousLocation;

        maintenance_roomNumber.setText("Maintenance History( " + this.data.getRoomNumber() + " )");
        ref_column.setCellValueFactory(cellData -> cellData.getValue().ref_columnProperty());
        description_column.setCellValueFactory(cellData -> cellData.getValue().description_columnProperty());
        cost_column.setCellValueFactory(cellData -> cellData.getValue().cost_columnProperty().asObject());
        status_column.setCellValueFactory(cellData -> cellData.getValue().status_columnProperty());
        created_on_column.setCellValueFactory(cellData -> cellData.getValue().created_on_columnProperty());
        completed_on_column.setCellValueFactory(cellData -> cellData.getValue().completed_on_columnProperty());


        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Collections.reverse(this.data.getMaintenance());
        for (Maintenance item : this.data.getMaintenance()){
            String createdOnColumn = item.getCreatedDateTime().format(dateTimeFormatter);
            String completedOnColumn = "";
            if (item.getLastModifiedDateTime() != null) {
                completedOnColumn = item.getLastModifiedDateTime().format(dateTimeFormatter);
            }
            MaintenanceRow maintenanceRow = new MaintenanceRow(item.getRef(), item.getDescription(), (item.getCost() != null ? item.getCost() : 0.0), item.getStatus(), createdOnColumn, completedOnColumn);
            tableData.add(maintenanceRow);
        }

        for (TableColumn<MaintenanceRow, ?> column : maintenance_table.getColumns()) {
            column.setStyle("-fx-pref-height: 70px;");
        }

        status_column.setCellFactory(column -> {
            return new TableCell<MaintenanceRow, MaintenanceStatus>() {
                @Override
                protected void updateItem(MaintenanceStatus item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        Label label = new Label(item.toJson());
                        label.setStyle("-fx-font-weight: bold; -fx-padding: 5px;");

                        if (item == MaintenanceStatus.ACTIVE) {
                            label.setStyle("-fx-text-fill: green; -fx-background-color: #e0f7e0; -fx-font-weight: bold; -fx-padding: 5px; -fx-background-radius: 15;");
                        } else if (item == MaintenanceStatus.COMPLETE) {
                            label.setStyle("-fx-text-fill: blue; -fx-background-color: #e0e0f7; -fx-font-weight: bold; -fx-padding: 5px; -fx-background-radius: 15;");
                        } else {
                            label.setStyle("-fx-padding: 5px;");
                        }

                        setGraphic(label);
                    }
                }
            };
        });

        maintenance_table.setItems(tableData);

    }

    public void initialize() {
        back.setOnAction(event -> goBack());
    }

    public void goBack(){
        try {
            Utils utils = new Utils();
//            utils.switchScreen(previousLocation, primaryStage);
            String[] credentials = TokenStorage.loadCredentials();
            String department = "";
            if (credentials != null) {
                department = credentials[2];
            }

            if(LoginDepartment.valueOf(department) == LoginDepartment.SUPER_ADMIN){
                utils.switchScreenWithData("/com/bmh/hotelmanagementsystem/room/singleRoom-view.fxml", primaryStage,data, "/com/bmh/hotelmanagementsystem/room/room-management-view.fxml");
            }
            else{
                utils.switchScreenWithData("/com/bmh/hotelmanagementsystem/room/general-admin-singleRoom-view.fxml", primaryStage,data, "/com/bmh/hotelmanagementsystem/room/general-admin-room-management-view.fxml");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
