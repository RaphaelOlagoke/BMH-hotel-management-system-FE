package com.bmh.hotelmanagementsystem.RoomManagement;

import com.bmh.hotelmanagementsystem.BackendService.RestClient;
import com.bmh.hotelmanagementsystem.BackendService.dto.MaintenanceDTO;
import com.bmh.hotelmanagementsystem.BackendService.entities.*;
import com.bmh.hotelmanagementsystem.BackendService.enums.MaintenanceStatus;
import com.bmh.hotelmanagementsystem.Controller;
import com.bmh.hotelmanagementsystem.Utils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.application.Platform;
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
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class SingleRoomController extends Controller {

    private Stage primaryStage;

    private Room currentRoom;

    private String previousLocation;

    @FXML
    private Button back;

    @FXML
    private Label room_number;
    @FXML
    private Label room_type;
//    @FXML
//    private Label room_price;
    @FXML
    private Label room_status;
    @FXML
    private Label archived;
    @FXML
    private Label needs_cleaning;
    @FXML
    private Label needs_maintenance;
    @FXML
    private Button archiveRoom;

    @FXML
    private TextArea maintenance_textBox;

    @FXML
    private Button scheduleMaintenance;
    @FXML
    private Button maintenanceHistory;
    @FXML
    private FlowPane maintenance_flowpane;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setRoom(Room room) {
        this.currentRoom = room;
        updateRoomDetails();
    }

    public void setData(Object data, String previousLocation) {
        this.currentRoom = (Room) data;
        this.previousLocation = previousLocation;
        updateRoomDetails();
    }

    private void updateRoomDetails(){
        if (currentRoom != null) {
            room_number.setText("Room " + currentRoom.getRoomNumber());
            room_type.setText("Room Type: " + currentRoom.getRoomType());
            room_status.setText("Status: " + currentRoom.getRoomStatus());
            archived.setText("Archived: " + currentRoom.getArchived());
            needs_cleaning.setText("Needs Cleaning: " + currentRoom.getNeedsCleaning());
            needs_maintenance.setText("Needs Maintenance: " + currentRoom.getNeedsMaintenance());
            if (!currentRoom.getArchived()){
                archiveRoom.setText("Archive");
            }else {
                archiveRoom.setText("Unarchive");
            }

            for (Maintenance maintenance : currentRoom.getMaintenance()){
                if (maintenance.getStatus() == MaintenanceStatus.ACTIVE) {
                    try {
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/bmh/hotelmanagementsystem/components/maintenance_item.fxml"));
                        HBox hbox = fxmlLoader.load();
                        Label description = (Label) hbox.lookup("#description");
                        description.setText(maintenance.getDescription());

                        Button btn = (Button) hbox.lookup("#mark_as_done");
                        btn.setOnAction(event -> markAsDone(maintenance));

                        maintenance_flowpane.getChildren().add(hbox);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setContentText("Something went wrong");
                        alert.showAndWait();
                    }
                }
            }
        }
    }

    @FXML
    public void initialize() throws IOException {

        back.setOnAction(event -> goBack());
        archiveRoom.setOnAction(event -> archive());
        scheduleMaintenance.setOnAction(event -> {
            try {
                scheduleMaintenance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        maintenanceHistory.setOnAction(event -> viewMaintenanceHistory());
    }


    public void archive(){
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle(!currentRoom.getArchived() ? "Confirm Archive" : "Confirm Unarchive");
        confirmationAlert.setHeaderText(!currentRoom.getArchived() ? "Are you sure you want to archive room " +
                currentRoom.getRoomNumber() : "Are you sure you want to unarchive room " + currentRoom.getRoomNumber() );
        confirmationAlert.setContentText("Please confirm your action.");

        Optional<ButtonType> result = confirmationAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (!currentRoom.getArchived()) {
                try {
                    String response = RestClient.post("/room/archive?roomNumber=" + currentRoom.getRoomNumber(), "");

                    ObjectMapper objectMapper = new ObjectMapper();

                    ApiResponse<?> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponse<?>>() {
                    });

                    if (apiResponse.getResponseHeader().getResponseCode().equals("00")) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle(apiResponse.getResponseHeader().getResponseMessage());
                        alert.setContentText("Room has been successfully archived");
                        alert.showAndWait();

                        try {
                            currentRoom.setArchived(!currentRoom.getArchived());
                            Utils utils = new Utils();
                            utils.switchScreenWithData("/com/bmh/hotelmanagementsystem/room/singleRoom-view.fxml", primaryStage,currentRoom, "/com/bmh/hotelmanagementsystem/room-management-view.fxml");
                        } catch (Exception e) {
                            e.printStackTrace();
                            alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error");
                            alert.setContentText("Something went wrong");
                            alert.showAndWait();
                        }
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle(apiResponse.getResponseHeader().getResponseMessage());
                        alert.setContentText(apiResponse.getError());
                        alert.showAndWait();

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Server Error");
                    errorAlert.setContentText("Something went wrong");
                    errorAlert.showAndWait();
                }
            } else {
                try {
                    String response = RestClient.post("/room/unarchive?roomNumber=" + currentRoom.getRoomNumber(), "");

                    ObjectMapper objectMapper = new ObjectMapper();

                    ApiResponse<?> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponse<?>>() {
                    });

                    if (apiResponse.getResponseHeader().getResponseCode().equals("00")) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle(apiResponse.getResponseHeader().getResponseMessage());
                        alert.setContentText("Room has been successfully Unarchived");
                        alert.showAndWait();
                        try {
                            currentRoom.setArchived(!currentRoom.getArchived());
                            Utils utils = new Utils();
                            utils.switchScreenWithData("/com/bmh/hotelmanagementsystem/room/singleRoom-view.fxml", primaryStage,currentRoom, "/com/bmh/hotelmanagementsystem/room-management-view.fxml");
                        } catch (Exception e) {
                            e.printStackTrace();
                            alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error");
                            alert.setContentText("Something went wrong");
                            alert.showAndWait();
                        }
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle(apiResponse.getResponseHeader().getResponseMessage());
                        alert.setContentText(apiResponse.getError());
                        alert.showAndWait();

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Server Error");
                    errorAlert.setContentText("Something went wrong");
                    errorAlert.showAndWait();
                }
            }
        }
        else {
            confirmationAlert.close();
        }
    }


    public void scheduleMaintenance() throws Exception {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirm Maintenance Creation");
        confirmationAlert.setHeaderText("Are you sure you want to create this maintenance log?");
        confirmationAlert.setContentText("Please confirm your action.");

        Optional<ButtonType> result = confirmationAlert.showAndWait();


        if (result.isPresent() && result.get() == ButtonType.OK) {
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
                    CreateMaintenanceRequest request = new CreateMaintenanceRequest();
                    request.setDescription(maintenance_textBox.getText());
                    request.setRoomNumber(currentRoom.getRoomNumber());

                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.registerModule(new JavaTimeModule());
                    String jsonString = objectMapper.writeValueAsString(request);

                    String response = RestClient.post("/room/maintenance/", jsonString);

                    ApiResponseSingleData<Room> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponseSingleData<Room>>() {
                    });

                    Platform.runLater(() -> {
                        try {
                            if (apiResponse.getResponseHeader().getResponseCode().equals("00")) {
                                loadingStage.close();

                                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                                successAlert.setTitle("Maintenance created Successful");
                                successAlert.setContentText("The request was successful.");
                                successAlert.showAndWait();

                                Utils utils = new Utils();
                                utils.switchScreenWithData("/com/bmh/hotelmanagementsystem/room/singleRoom-view.fxml", primaryStage,apiResponse.getData(), "/com/bmh/hotelmanagementsystem/room-management-view.fxml");
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
                            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                            errorAlert.setTitle("Error");
                            errorAlert.setContentText("Something went wrong. Please try again.");
                            errorAlert.showAndWait();
                        }
                    });

                }
                catch (Exception e) {
                    Platform.runLater(() -> {
                        e.printStackTrace();
                        loadingStage.close();
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Server Error");
                        alert.setContentText("Something went wrong. Please try again.");
                        alert.showAndWait();
                    });
                }
            }).start();
        }
        else {
            confirmationAlert.close();
        }
    }


    public void markAsDone(Maintenance maintenance){
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Cost");
        dialog.setHeaderText("Total Cost of maintenance");
        dialog.setContentText("cost:");
        AtomicReference<Double> price = new AtomicReference<>(0.0);

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(cost -> {
            try{
                price.set(Double.valueOf(cost));
            }
            catch (Exception e){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid input");
                alert.setContentText("Cost must be a number e.g(2000.00)");
                alert.showAndWait();
                return;
            }

            Stage loadingStage = Utils.showLoadingScreen(primaryStage);
            loadingStage.show();

            new Thread(() -> {
                try{
                    CompleteMaintenanceRequest request = new CompleteMaintenanceRequest();
                    request.setRef(maintenance.getRef());
                    request.setCost(price.get());

                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.registerModule(new JavaTimeModule());
                    String jsonString = objectMapper.writeValueAsString(request);

                    String response = RestClient.post("/room/maintenance/update", jsonString);

                    ApiResponseSingleData<Room> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponseSingleData<Room>>() {
                    });

                    Platform.runLater(() -> {
                        try {
                            if (apiResponse.getResponseHeader().getResponseCode().equals("00")) {
                                loadingStage.close();

                                Utils.showAlertDialog(Alert.AlertType.INFORMATION, "Maintenance completed Successfully", "The request was successful");

                                Utils utils = new Utils();
                                utils.switchScreenWithData("/com/bmh/hotelmanagementsystem/room/singleRoom-view.fxml", primaryStage,apiResponse.getData(), "/com/bmh/hotelmanagementsystem/room-management-view.fxml");
                            } else {
                                loadingStage.close();
                                Utils.showAlertDialog(Alert.AlertType.ERROR, apiResponse.getResponseHeader().getResponseMessage(), apiResponse.getError());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            loadingStage.close();
                            Utils.showServerErrorDialog();
                        }
                    });
                }
                catch (Exception e){
                    Platform.runLater(() -> {
                        e.printStackTrace();
                        loadingStage.close();
                        Utils.showServerErrorDialog();
                    });
                }
            }).start();


        });
    }

    public void viewMaintenanceHistory(){
        try {
            Utils utils = new Utils();
            utils.switchScreenWithData("/com/bmh/hotelmanagementsystem/maintenance-history-view.fxml", primaryStage,currentRoom, "/com/bmh/hotelmanagementsystem/room-management-view.fxml");
        } catch (Exception e) {
            e.printStackTrace();
            Utils.showServerErrorDialog();
        }
    }


    public void goBack(){
        try {
            Utils utils = new Utils();
            utils.switchScreen(previousLocation, primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


