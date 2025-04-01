package com.bmh.hotelmanagementsystem.employee;

import com.bmh.hotelmanagementsystem.BackendService.RestClient;
import com.bmh.hotelmanagementsystem.BackendService.entities.ApiResponseSingleData;
import com.bmh.hotelmanagementsystem.BackendService.entities.Restaurant.CreateMenuItemRequest;
import com.bmh.hotelmanagementsystem.BackendService.entities.Restaurant.MenuItemDto;
import com.bmh.hotelmanagementsystem.BackendService.entities.UpdateUserRequest;
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

public class UpdateEmployeeController extends Controller {

    private Stage primaryStage;
    private String previousLocation;

    private User data;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setData(Object data, String previousLocation){
        this.data = (User) data;
        this.previousLocation = previousLocation;
        username.setText(this.data.getUsername());
        email.setText(this.data.getEmail());

        role_comboBox.setValue(this.data.getRole());
        enabled_comboBox.setValue(this.data.isEnabled());
        access_comboBox.setValue(this.data.getDepartment().toJson());
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

    @FXML
    private Button resendLink;


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
            accessLevels.add(access.toJson());
        }

        access_comboBox.setItems(accessLevels);

        create.setOnAction(event -> updateUser());
        password.setDisable(true);
        password.setVisible(false);
        resendLink.setOnAction(event -> resendPasswordLink());
    }

    public void updateUser(){
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirm action");
        confirmationAlert.setHeaderText("Are you sure you want to update this user ?");
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

                    UpdateUserRequest request = new UpdateUserRequest();
                    request.setUsername(username.getText());

//                    if (password.getText() == null || password.getText().equals("")) {
//                        request.setNewPassword(null);
//                    }
//                    else{
//                        request.setNewPassword(password.getText());
//                    }

                    request.setOldEmail(this.data.getEmail());
                    request.setNewEmail(email.getText());
                    request.setNewRole(role_comboBox.getSelectionModel().getSelectedItem());
                    request.setEnabled(enabled_comboBox.getSelectionModel().getSelectedItem());
                    request.setDepartment(LoginDepartment.valueOf(access_comboBox.getSelectionModel().getSelectedItem()));

                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.registerModule(new JavaTimeModule());

                    String jsonString = objectMapper.writeValueAsString(request);

                    String response = RestClient.post("/admin/update", jsonString);
                    ApiResponseSingleData<User> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponseSingleData<User>>() {
                    });

                    if (apiResponse.getResponseHeader().getResponseCode().equals("00")) {
                        Platform.runLater(() -> {
                            loadingStage.close();
                            Utils.showAlertDialog(Alert.AlertType.INFORMATION, "Updated Successfully", "User Updated successfully");
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


    public void resendPasswordLink(){
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirm action");
        confirmationAlert.setHeaderText("Are you sure you want to send reset password link?");
        confirmationAlert.setContentText("Please confirm your action.");

        Optional<ButtonType> result = confirmationAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            Stage loadingStage = Utils.showLoadingScreen(primaryStage);
            Platform.runLater(() -> loadingStage.show());

            new Thread(() -> {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.registerModule(new JavaTimeModule());

                    User request = new User();
                    request.setEmail( this.data.getEmail());

                    String jsonString = objectMapper.writeValueAsString(request);


                    String response = RestClient.post("/admin/send-reset-password-link", jsonString);
                    ApiResponseSingleData<User> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponseSingleData<User>>() {
                    });

                    if (apiResponse.getResponseHeader().getResponseCode().equals("00")) {
                        Platform.runLater(() -> {
                            loadingStage.close();
                            Utils.showAlertDialog(Alert.AlertType.INFORMATION, "Sent Successfully", "Password reset link sent successfully");
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
