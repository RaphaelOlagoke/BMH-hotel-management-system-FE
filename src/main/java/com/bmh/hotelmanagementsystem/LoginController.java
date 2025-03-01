package com.bmh.hotelmanagementsystem;

import com.bmh.hotelmanagementsystem.BackendService.RestClient;
import com.bmh.hotelmanagementsystem.BackendService.entities.ApiResponseSingleData;
import com.bmh.hotelmanagementsystem.BackendService.entities.Room.CreateGuestLogRequest;
import com.bmh.hotelmanagementsystem.BackendService.entities.UserLoginDetails;
import com.bmh.hotelmanagementsystem.BackendService.entities.UserLoginRequest;
import com.bmh.hotelmanagementsystem.BackendService.enums.LoginDepartment;
import com.bmh.hotelmanagementsystem.BackendService.utils.AuthFileCache;
import com.bmh.hotelmanagementsystem.components.SideBarController;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController extends Controller{


    private Stage primaryStage;
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }


    @FXML
    private Button login_button;

    @FXML
    private TextField username;

    @FXML
    private TextField password;

    @FXML
    protected void onLoginButtonClick() throws IOException {

        Stage loadingStage = Utils.showLoadingScreen(primaryStage);

        Platform.runLater(() ->{
                loadingStage.show();
        });

        new Thread(() -> {

            try {
                UserLoginRequest request = new UserLoginRequest();
                request.setEmail(username.getText());
                request.setPassword(password.getText());

                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());

                String jsonString = objectMapper.writeValueAsString(request);

                String response = RestClient.postWthoutToken("/auth/login", jsonString);
                ApiResponseSingleData<UserLoginDetails> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponseSingleData<UserLoginDetails>>() {
                });

                Platform.runLater(() -> {
                    try {
                        if (apiResponse.getResponseHeader().getResponseCode().equals("00")) {
                            loadingStage.close();

                            AuthFileCache.saveCredentials(apiResponse.getData().getToken(),apiResponse.getData().getUser().getUsername());

                            FXMLLoader fxmlLoader = new FXMLLoader(BMHApplication.class.getResource("home-view.fxml"));

                            if(apiResponse.getData().getUser().getDepartment() == LoginDepartment.RECEPTIONIST){
                                fxmlLoader = new FXMLLoader(BMHApplication.class.getResource("home-view.fxml"));
                            }
                            else if(apiResponse.getData().getUser().getDepartment() == LoginDepartment.ADMIN ) {
                                fxmlLoader = new FXMLLoader(BMHApplication.class.getResource("/com/bmh/hotelmanagementsystem/room/room-management-view.fxml"));
                            }
                            else if(apiResponse.getData().getUser().getDepartment() == LoginDepartment.RESTAURANT_BAR){
                                fxmlLoader = new FXMLLoader(BMHApplication.class.getResource("/com/bmh/hotelmanagementsystem/Restaurant/restaurant-view.fxml"));
                            }
                            else{
                                fxmlLoader = new FXMLLoader(BMHApplication.class.getResource("/com/bmh/hotelmanagementsystem/room/room-management-view.fxml"));
                            }

                            ScrollPane homeRoot = fxmlLoader.load();
                            Scene scene = new Scene(homeRoot);
                            scene.getStylesheets().add(getClass().getResource("css/styles.css").toExternalForm());

                            //        primaryStage.setTitle("BMH");
                            //        primaryStage.setHeight(1080);
                            //        primaryStage.setWidth(1920);
                                                        primaryStage.setScene(scene);
                            //        primaryStage.setMaximized(true);
                            //        primaryStage.setFullScreen(true);
                            //        primaryStage.setFullScreenExitHint("");

                            Controller homeController = fxmlLoader.getController();
                            homeController.setPrimaryStage(primaryStage);

                            primaryStage.show();
                        //        primaryStage.setFullScreen(true);

                        } else {
                            // Close the loading screen
                            loadingStage.close();
                            Utils.showAlertDialog(Alert.AlertType.ERROR,apiResponse.getResponseHeader().getResponseMessage(),apiResponse.getError());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        loadingStage.close();
                        Utils.showGeneralErrorDialog();
                    }
                });


            } catch (Exception e) {
                Platform.runLater(() -> {
                    e.printStackTrace();
                    loadingStage.close();
                    Utils.showGeneralErrorDialog();
                });
            }
        }).start();

    }
}