package com.bmh.hotelmanagementsystem.restaurant;

import com.bmh.hotelmanagementsystem.BackendService.RestClient;
import com.bmh.hotelmanagementsystem.BackendService.entities.ApiResponseSingleData;
import com.bmh.hotelmanagementsystem.BackendService.entities.Restaurant.CreateMenuItemRequest;
import com.bmh.hotelmanagementsystem.BackendService.entities.Restaurant.MenuItemDto;
import com.bmh.hotelmanagementsystem.BackendService.entities.Restaurant.UpdateMenuItemRequest;
import com.bmh.hotelmanagementsystem.BackendService.enums.MenuItemType;
import com.bmh.hotelmanagementsystem.Controller;
import com.bmh.hotelmanagementsystem.Utils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.text.DecimalFormat;

public class UpdateMenuItemController extends Controller {

    private Stage primaryStage;
    private String previousLocation;

    private MenuItemDto data;

    DecimalFormat formatter = new DecimalFormat("#,###.00");
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setData(Object data, String previousLocation){
        this.data = (MenuItemDto) data;
        this.previousLocation = previousLocation;
        menu_item_ref.setText("Ref: " + this.data.getRef());
        name.setText(this.data.getName());
        price.setText(String.valueOf(this.data.getPrice()));
        category_comboBox.setValue(this.data.getCategory().toJson());
    }

    @FXML
    private Label menu_item_ref;
    @FXML
    private TextField name;
    @FXML
    private TextField price;
    @FXML
    private Button update;
    @FXML
    private ComboBox<String> category_comboBox;


    public void initialize() {
        ObservableList<String> categories = FXCollections.observableArrayList();

        for (MenuItemType itemType : MenuItemType.values()){
            categories.add(itemType.toJson());
        }

        category_comboBox.setItems(categories);

        update.setOnAction(event -> updateMenuItem());
    }

    public void updateMenuItem(){
        Stage loadingStage = Utils.showLoadingScreen(primaryStage);
        Platform.runLater(() -> loadingStage.show());

        new Thread(() -> {
            try {
                if(category_comboBox.getSelectionModel().getSelectedItem() == null){
                    Platform.runLater(() -> {
                        loadingStage.close();
                        Utils.showAlertDialog(Alert.AlertType.ERROR,"Invalid request", "Category cannot be empty" );
                    });
                    return;
                }

                if (name.getText() == null || name.getText().equals("")){
                    Platform.runLater(() -> {
                        loadingStage.close();
                        Utils.showAlertDialog(Alert.AlertType.ERROR,"Invalid request", "Name cannot be empty" );
                    });
                    return;
                }

                UpdateMenuItemRequest request = new UpdateMenuItemRequest();
                request.setRef(data.getRef());
                request.setName(name.getText());
                try{
                    request.setPrice(Double.valueOf(price.getText()));
                }
                catch (Exception e){
                    Platform.runLater(() -> {
                        loadingStage.close();
                        Utils.showAlertDialog(Alert.AlertType.ERROR,"Invalid request", "Invalid Price " );
                    });
                    return;
                }
                request.setCategory(MenuItemType.valueOf(category_comboBox.getSelectionModel().getSelectedItem()));

                ObjectMapper objectMapper = new ObjectMapper();

                String jsonString = objectMapper.writeValueAsString(request);

                String response = RestClient.post("/restaurant/menuItem/update", jsonString);
                ApiResponseSingleData<MenuItemDto> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponseSingleData<MenuItemDto>>() {});

                if (apiResponse.getResponseHeader().getResponseCode().equals("00")) {
                    Platform.runLater(() -> {
                        loadingStage.close();
                        Utils.showAlertDialog(Alert.AlertType.INFORMATION,"Updated Successfully","Menu Item updated successfully" );
                        primaryStage.close();

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
}
