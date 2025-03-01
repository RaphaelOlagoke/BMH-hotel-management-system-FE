package com.bmh.hotelmanagementsystem.employee;

import com.bmh.hotelmanagementsystem.BackendService.RestClient;
import com.bmh.hotelmanagementsystem.BackendService.entities.ApiResponseSingleData;
import com.bmh.hotelmanagementsystem.BackendService.entities.Restaurant.CreateMenuItemRequest;
import com.bmh.hotelmanagementsystem.BackendService.entities.Restaurant.MenuItemDto;
import com.bmh.hotelmanagementsystem.BackendService.entities.User;
import com.bmh.hotelmanagementsystem.BackendService.enums.LoginDepartment;
import com.bmh.hotelmanagementsystem.BackendService.enums.MenuItemType;
import com.bmh.hotelmanagementsystem.BackendService.enums.UserRoles;
import com.bmh.hotelmanagementsystem.Controller;
import com.bmh.hotelmanagementsystem.Utils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.Optional;

public class CreateEmployeeController extends Controller {

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
    private TextField email;
    @FXML
    private TextField username;
    @FXML
    private TextField password;

    @FXML
    private ComboBox<String> role_comboBox;
    @FXML
    private ComboBox<Boolean> enabled_comboBox;
    @FXML
    private ComboBox<String> access_comboBox;
    @FXML
    private Button create;


    public void initialize() {
        ObservableList<String> roles = FXCollections.observableArrayList();

        for (UserRoles userRole : UserRoles.values()) {
            roles.add(userRole.toJson());
        }

        role_comboBox.setItems(roles);

        ObservableList<Boolean> booleanList = FXCollections.observableArrayList();

        booleanList.add(true);
        booleanList.add(false);

        enabled_comboBox.setItems(booleanList);

        ObservableList<String> accessLevels = FXCollections.observableArrayList();

        accessLevels.add(null);
        for (LoginDepartment access : LoginDepartment.values()) {
            if(access != LoginDepartment.SUPER_ADMIN) {
                accessLevels.add(access.toJson());
            }
        }

        access_comboBox.setItems(accessLevels);

        create.setOnAction(event -> createUser());
    }

    public void createUser(){
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirm action");
        confirmationAlert.setHeaderText("Are you sure you want to create this user ?");
        confirmationAlert.setContentText("Please confirm your action.");

        Optional<ButtonType> result = confirmationAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            Stage loadingStage = Utils.showLoadingScreen(primaryStage);
            Platform.runLater(() -> loadingStage.show());

            new Thread(() -> {
                try {
                    if (role_comboBox.getSelectionModel().getSelectedItem() == null) {
                        Platform.runLater(() -> {
                            loadingStage.close();
                            Utils.showAlertDialog(Alert.AlertType.ERROR, "Invalid request", "User Role cannot be empty");
                        });
                        return;
                    }
                    if (enabled_comboBox.getSelectionModel().getSelectedItem() == null) {
                        Platform.runLater(() -> {
                            loadingStage.close();
                            Utils.showAlertDialog(Alert.AlertType.ERROR, "Invalid request", "User active status cannot be empty");
                        });
                        return;
                    }
                    if (access_comboBox.getSelectionModel().getSelectedItem() == null) {
                        Platform.runLater(() -> {
                            loadingStage.close();
                            Utils.showAlertDialog(Alert.AlertType.ERROR, "Invalid request", "User Access cannot be empty");
                        });
                        return;
                    }

                    if (username.getText() == null || username.getText().equals("")) {
                        Platform.runLater(() -> {
                            loadingStage.close();
                            Utils.showAlertDialog(Alert.AlertType.ERROR, "Invalid request", "User cannot be empty");
                        });
                        return;
                    }
                    if (email.getText() == null || email.getText().equals("")) {
                        Platform.runLater(() -> {
                            loadingStage.close();
                            Utils.showAlertDialog(Alert.AlertType.ERROR, "Invalid request", "Email cannot be empty");
                        });
                        return;
                    }
                    if (password.getText() == null || password.getText().equals("")) {
                        Platform.runLater(() -> {
                            loadingStage.close();
                            Utils.showAlertDialog(Alert.AlertType.ERROR, "Invalid request", "Password cannot be empty");
                        });
                        return;
                    }

                    User request = new User();
                    request.setUsername(username.getText());
                    request.setPassword(password.getText());
                    request.setEmail(email.getText());
                    request.setRole(role_comboBox.getSelectionModel().getSelectedItem());
                    request.setEnabled(enabled_comboBox.getSelectionModel().getSelectedItem());
                    request.setDepartment(LoginDepartment.valueOf(access_comboBox.getSelectionModel().getSelectedItem()));

                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.registerModule(new JavaTimeModule());

                    String jsonString = objectMapper.writeValueAsString(request);

                    String response = RestClient.post("/admin/create", jsonString);
                    ApiResponseSingleData<User> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponseSingleData<User>>() {
                    });

                    if (apiResponse.getResponseHeader().getResponseCode().equals("00")) {
                        Platform.runLater(() -> {
                            loadingStage.close();
                            Utils.showAlertDialog(Alert.AlertType.INFORMATION, "Created Successfully", "User created successfully");
                            primaryStage.close();

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
        }else {
            confirmationAlert.close();
        }

    }
}
