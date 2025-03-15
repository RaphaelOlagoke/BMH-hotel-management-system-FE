package com.bmh.hotelmanagementsystem.restaurant;

import com.bmh.hotelmanagementsystem.BackendService.RestClient;
import com.bmh.hotelmanagementsystem.BackendService.enums.StockItemCategory;
import com.bmh.hotelmanagementsystem.BackendService.utils.AuthFileCache;
import com.bmh.hotelmanagementsystem.TokenStorage;
import com.bmh.hotelmanagementsystem.dto.restaurant.OrderDetails;
import com.bmh.hotelmanagementsystem.BackendService.entities.ApiResponse;
import com.bmh.hotelmanagementsystem.BackendService.entities.Restaurant.BillItem;
import com.bmh.hotelmanagementsystem.BackendService.entities.Restaurant.MenuItemDto;
import com.bmh.hotelmanagementsystem.BackendService.enums.MenuItemType;
import com.bmh.hotelmanagementsystem.BackendService.enums.PaymentMethod;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RestaurantController extends Controller {

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
    private Label user;

    @FXML
    private FlowPane menu_flowPane;
    @FXML
    private VBox bill_flowPane;

    @FXML
    private Button appetizerCategory;
    @FXML
    private Button mainCategory;
    @FXML
    private Button dessertCategory;
    @FXML
    private Button sidesCategory;
    @FXML
    private Button beverageCategory;
    @FXML
    private Button specialCategory;

    @FXML
    private VBox appetizerVBox;
    @FXML
    private VBox mainVBox;
    @FXML
    private VBox dessertVBox;
    @FXML
    private VBox sidesVBox;
    @FXML
    private VBox beverageVBox;
    @FXML
    private VBox specialVBox;

    @FXML
    private Label appetizerLabel;
    @FXML
    private Label mainLabel;
    @FXML
    private Label dessertLabel;
    @FXML
    private Label sidesLabel;
    @FXML
    private Label beverageLabel;
    @FXML
    private Label specialLabel;

    @FXML
    private Label menuCategory;

    @FXML
    private TextField searchField;
    @FXML
    private Button search;
    @FXML
    private Label subtotalLabel;
    @FXML
    private Label discountLabel;
    @FXML
    private Label taxLabel;
    @FXML
    private Label totalLabel;

    @FXML
    private Button cash;
    @FXML
    private Button card;
    @FXML
    private Button transfer;
    @FXML
    private Button chargeToRoom;

    @FXML
    private VBox cashVbox;
    @FXML
    private VBox cardVbox;
    @FXML
    private VBox transferVbox;

    @FXML
    private Label cashLabel;
    @FXML
    private Label cardLabel;
    @FXML
    private Label transferLabel;

    @FXML
    private Button create;

    @FXML
    private Button payment;

    @FXML
    private ScrollPane scrollPane;

    private VBox currentlySelectedVBox = null;
    private Label currentlySelectedLabel = null;

    private VBox currentlySelectedPaymentMethod = null;
    private Label currentlySelectedPaymentMethodLabel = null;

    List<BillItem> billItems = new ArrayList<>();
    double subTotal;
    double discount;
    double tax;
    double total;

    PaymentMethod paymentMethod;

    DecimalFormat formatter = new DecimalFormat("#,###.00");
    @FXML
    public void initialize() throws IOException {
        String[] credentials = TokenStorage.loadCredentials();
        String username = "";
        if (credentials != null) {
            username = credentials[0];
        }
        user.setText("Hello " + username);

        appetizerCategory.setOnAction(event -> onCategorySelected(appetizerVBox, appetizerLabel, MenuItemType.APPETIZER.toJson()));
        mainCategory.setOnAction(event -> onCategorySelected(mainVBox, mainLabel, MenuItemType.MAINS.toJson()));
        dessertCategory.setOnAction(event -> onCategorySelected(dessertVBox, dessertLabel, MenuItemType.DESSERT.toJson()));
        sidesCategory.setOnAction(event -> onCategorySelected(sidesVBox, sidesLabel, MenuItemType.SIDES.toJson()));
        beverageCategory.setOnAction(event -> onCategorySelected(beverageVBox, beverageLabel, MenuItemType.BEVERAGE.toJson()));
        specialCategory.setOnAction(event -> onCategorySelected(specialVBox, specialLabel, MenuItemType.SPECIALS.toJson()));

        cash.setOnAction(event -> onSelectedPaymentMethod(cashVbox, cashLabel, PaymentMethod.CASH));
        card.setOnAction(event -> onSelectedPaymentMethod(cardVbox, cardLabel, PaymentMethod.CARD));
        transfer.setOnAction(event -> onSelectedPaymentMethod(transferVbox, transferLabel, PaymentMethod.TRANSFER));
        chargeToRoom.setOnAction(event -> chargeToRoom());

        create.setOnAction(event -> create());
//        List<Menu> menuList = new ArrayList<>();
//        menuList.add(new Menu("Burger", 5000.0));
//
//        for (Menu menuItem : menuList){
//            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/bmh/hotelmanagementsystem/Restaurant/menu_item.fxml"));
//            VBox vBox = fxmlLoader.load();
//
//            Label name = (Label) vBox.lookup("#name");
//            Label price = (Label) vBox.lookup("#price");
//
//            name.setText(menuItem.getName());
//            price.setText("$" + menuItem.getPrice());
//
//            menu_flowPane.getChildren().add(vBox);
//        }

        search.setOnAction(event -> search(searchField.getText()));
        searchField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                search(searchField.getText());
            }
        });
        payment.setOnAction(event -> createOrder());

//        List<Bill> billList = new ArrayList<>();
//        billList.add(new Bill("Burger", 5000.0, 100));
//
//
//        for (Bill billItem : billList){
//            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/bmh/hotelmanagementsystem/Restaurant/bill_item.fxml"));
//            HBox hBox = fxmlLoader.load();
//
//            Label name = (Label) hBox.lookup("#name");
//            Label price = (Label) hBox.lookup("#price");
//            Label quantity = (Label) hBox.lookup("#quantity");
//
//            name.setText(billItem.getName());
//            price.setText("$" + billItem.getPrice());
//            quantity.setText("" + billItem.getQuantity());
//
//            bill_flowPane.getChildren().add(hBox);
//        }
    }

    private void onCategorySelected(VBox selectedVBox, Label selectedLabel, String category) {
        if (currentlySelectedVBox != null) {
            currentlySelectedVBox.getStyleClass().remove("selected-vbox");
            currentlySelectedLabel.getStyleClass().remove("selected-label");
        }

        selectedVBox.getStyleClass().add("selected-vbox");
        selectedLabel.getStyleClass().add("selected-label");
        menuCategory.setText(selectedLabel.getText() + " Menu");

        currentlySelectedVBox = selectedVBox;
        currentlySelectedLabel = selectedLabel;

        getMenu(category);
    }

    public void getMenu(String category){
        searchField.clear();
        Stage loadingStage = Utils.showLoadingScreen(primaryStage);
        Platform.runLater(loadingStage::show);

        new Thread(() -> {

            try {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());

                String response = RestClient.get("/restaurant/category?category=" + category);
                ApiResponse<MenuItemDto> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponse<MenuItemDto>>() {});


                Platform.runLater(() -> {
                    try {
                        if (apiResponse.getResponseHeader().getResponseCode().equals("00")) {
                            loadingStage.close();

                            menu_flowPane.getChildren().clear();

                            for (MenuItemDto menuItem : apiResponse.getData()){
                                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/bmh/hotelmanagementsystem/restaurant/menu_item.fxml"));
                                VBox vBox = fxmlLoader.load();

                                Button viewMore = (Button) vBox.lookup("#viewMore");
                                Label name = (Label) vBox.lookup("#name");
                                Label price = (Label) vBox.lookup("#price");
                                Button add = (Button) vBox.lookup("#add");
//                                ImageView image = (ImageView) vBox.lookup("#image");
//
//                                String imageRootPath = "/com/bmh/hotelmanagementsystem/images/restaurant/";
//                                Image img = new Image(imageRootPath + "Cake.png");
//
//                                if(category.equalsIgnoreCase(MenuItemType.APPETIZER.toJson())) {
//                                    img = new Image(getClass().getClassLoader().getResource(imageRootPath + "Cake.png").toExternalForm());
//                                }
//                                else if (category.equalsIgnoreCase(MenuItemType.MAINS.toJson())) {
//                                    img = new Image(getClass().getResource(imageRootPath +"Union (1).png").toExternalForm());
//                                }
//                                else if (category.equalsIgnoreCase(MenuItemType.DESSERT.toJson())) {
//                                    img = new Image(getClass().getResource(imageRootPath +"Union (2).png").toExternalForm());
//                                }
//                                else if (category.equalsIgnoreCase(MenuItemType.SIDES.toJson())) {
//                                    img = new Image(getClass().getResource(imageRootPath +"/Vector.png").toExternalForm());
//                                }
//                                else if (category.equalsIgnoreCase(MenuItemType.BEVERAGE.toJson())) {
//                                    img = new Image(getClass().getResource(imageRootPath +"Union.png").toExternalForm());
//                                }
//                                else if (category.equalsIgnoreCase(MenuItemType.SPECIALS.toJson())) {
//                                    img = new Image(getClass().getResource(imageRootPath +"Star_fill.png").toExternalForm());
//                                }
//
//                                image.setImage(img);

                                add.setOnAction(event -> {
                                    try {
                                        addToBill(menuItem);
                                    } catch (IOException e) {
                                        Utils.showGeneralErrorDialog();
                                        throw new RuntimeException(e);
                                    }
                                });

                                name.setText(menuItem.getName());
                                price.setText("$" + formatter.format(menuItem.getPrice()));
                                viewMore.setOnAction(event -> editMenuItem(menuItem));

                                menu_flowPane.getChildren().add(vBox);
                            }

                        } else {
                            loadingStage.close();
                            Utils.showAlertDialog(Alert.AlertType.ERROR, apiResponse.getResponseHeader().getResponseMessage(), apiResponse.getError());
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

    public void search(String query){
        searchField.clear();
        if (currentlySelectedVBox != null) {
            currentlySelectedVBox.getStyleClass().remove("selected-vbox");
            currentlySelectedLabel.getStyleClass().remove("selected-label");
        }
        Stage loadingStage = Utils.showLoadingScreen(primaryStage);
        Platform.runLater(loadingStage::show);

        new Thread(() -> {

            try {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());

                String response = RestClient.get("/restaurant/searchMenuItems?query=" + query);
                ApiResponse<MenuItemDto> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponse<MenuItemDto>>() {});


                Platform.runLater(() -> {
                    try {
                        if (apiResponse.getResponseHeader().getResponseCode().equals("00")) {
                            loadingStage.close();

                            menu_flowPane.getChildren().clear();

                            menuCategory.setText("Search result");

                            for (MenuItemDto menuItem : apiResponse.getData()){
                                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/bmh/hotelmanagementsystem/Restaurant/menu_item.fxml"));
                                VBox vBox = fxmlLoader.load();

                                Label name = (Label) vBox.lookup("#name");
                                Label price = (Label) vBox.lookup("#price");
                                Button add = (Button) vBox.lookup("#add");

                                add.setOnAction(event -> {
                                    try {
                                        addToBill(menuItem);
                                    } catch (IOException e) {
                                        Utils.showGeneralErrorDialog();
                                        throw new RuntimeException(e);
                                    }
                                });

                                name.setText(menuItem.getName());
                                price.setText("₦" + formatter.format(menuItem.getPrice()));

                                menu_flowPane.getChildren().add(vBox);
                            }

                        } else {
                            loadingStage.close();
                            Utils.showAlertDialog(Alert.AlertType.ERROR, apiResponse.getResponseHeader().getResponseMessage(), apiResponse.getError());
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

    public void addToBill(MenuItemDto menuItem) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/bmh/hotelmanagementsystem/restaurant/bill_item.fxml"));
        HBox hBox = fxmlLoader.load();

        Label name = (Label) hBox.lookup("#name");
        Label price = (Label) hBox.lookup("#price");
        Label quantity = (Label) hBox.lookup("#quantity");
        VBox vbox = (VBox) hBox.lookup("#button_vbox");
        Button remove = (Button) hBox.lookup("#remove");
        Button increaseQuantity = (Button) hBox.lookup("#increase_quantity");
        Button decreaseQuantity = (Button) hBox.lookup("#reduce_quantity");

        name.setText(menuItem.getName());
        price.setText("$" + formatter.format(menuItem.getPrice()));
        quantity.setText("1");

        BillItem billItem = new BillItem();
        billItem.setName(menuItem.getName());
        billItem.setPrice(menuItem.getPrice());
        billItem.setQuantity(1);
        billItems.add(billItem);

        subTotal = 0.0;
        discount = 0.0;
        tax = 0.0;

        for(BillItem item : billItems){
            double itemTotal = item.getPrice() * item.getQuantity();
            subTotal += itemTotal;

        }

        total = (subTotal - discount) + tax;

        subtotalLabel.setText(String.valueOf(formatter.format(subTotal)));
        totalLabel.setText(String.valueOf(formatter.format(total)));

        remove.setOnAction(event -> removeFromBill(hBox, billItem));
        increaseQuantity.setOnAction(event -> increaseQuantity(hBox, billItem));
        decreaseQuantity.setOnAction(event -> decreaseQuantity(hBox, billItem));

        bill_flowPane.getChildren().add(hBox);
    }

    public void removeFromBill(HBox hBox, BillItem billItem) {
        bill_flowPane.getChildren().remove(hBox);
        billItems.remove(billItem);

        subTotal = 0.0;
        discount = 0.0;
        tax = 0.0;

        for (BillItem item : billItems) {
            double itemTotal = item.getPrice() * item.getQuantity();
            subTotal += itemTotal;
        }

        total = (subTotal - discount) + tax;

        subtotalLabel.setText(String.valueOf(formatter.format(subTotal)));
        totalLabel.setText(String.valueOf(formatter.format(total)));
    }

    public void increaseQuantity(HBox hBox, BillItem billItem){
        Label quantity = (Label) hBox.lookup("#quantity");
        try {
            int quantityValue = billItem.getQuantity() + 1;
            quantity.setText(String.valueOf(quantityValue));

            billItem.setQuantity(quantityValue);

            subTotal = 0.0;
            discount = 0.0;
            tax = 0.0;

            for (BillItem item : billItems) {
                double itemTotal = item.getPrice() * item.getQuantity();
                subTotal += itemTotal;
            }

            total = (subTotal - discount) + tax;

            subtotalLabel.setText(String.valueOf(formatter.format(subTotal)));
            totalLabel.setText(String.valueOf(formatter.format(total)));
        }
        catch (Exception e){
            Utils.showGeneralErrorDialog();
        }
    }

    public void decreaseQuantity(HBox hBox, BillItem billItem){
        Label quantity = (Label) hBox.lookup("#quantity");
        try {
            int quantityValue = billItem.getQuantity() - 1;
            if(quantityValue <=0){
                bill_flowPane.getChildren().remove(hBox);
                billItems.remove(billItem);
            }
            else{
                quantity.setText(String.valueOf(quantityValue));

                billItem.setQuantity(quantityValue);
            }

            subTotal = 0.0;
            discount = 0.0;
            tax = 0.0;

            for (BillItem item : billItems) {
                double itemTotal = item.getPrice() * item.getQuantity();
                subTotal += itemTotal;
            }

            total = (subTotal - discount) + tax;

            subtotalLabel.setText(String.valueOf(formatter.format(subTotal)));
            totalLabel.setText(String.valueOf(formatter.format(total)));
        }
        catch (Exception e){
            Utils.showGeneralErrorDialog();
        }
    }

    public void onSelectedPaymentMethod(VBox selectedPaymentMethod,Label selectedPaymentLabel,PaymentMethod method){
        if (currentlySelectedPaymentMethod != null) {
            currentlySelectedPaymentMethod.getStyleClass().remove("selected-vbox");
            currentlySelectedPaymentMethodLabel.getStyleClass().remove("selected-label");
        }

        selectedPaymentMethod.getStyleClass().add("selected-vbox");
        selectedPaymentLabel.getStyleClass().add("selected-label");

        currentlySelectedPaymentMethod = selectedPaymentMethod;
        currentlySelectedPaymentMethodLabel = selectedPaymentLabel;
        paymentMethod = method;
    }

    public void create(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bmh/hotelmanagementsystem/restaurant/create_menuItem.fxml"));
            Region form = loader.load();

            Stage formStage = new Stage();
            formStage.initModality(Modality.APPLICATION_MODAL);
            formStage.setTitle("Fill out the Form");

            CreateMenuItemController controller = loader.getController();
            controller.setPrimaryStage(formStage);
            controller.setData(null, "/com/bmh/hotelmanagementsystem/restaurant/restaurant-view.fxml");

            Scene formScene = new Scene(form);
            formStage.setScene(formScene);
            formStage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            Utils.showGeneralErrorDialog();
        }
    }

    public void createOrder(){
        if(currentlySelectedPaymentMethodLabel == null){
            Utils.showAlertDialog(Alert.AlertType.ERROR, "Invalid Request", "Payment method must be selected");
            return;
        }
        if(total == 0){
            Utils.showAlertDialog(Alert.AlertType.ERROR, "Invalid Request", "No item Selected");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bmh/hotelmanagementsystem/restaurant/confirm_order.fxml"));
            Region form = loader.load();

            Stage formStage = new Stage();
            formStage.initModality(Modality.APPLICATION_MODAL);
            formStage.setTitle("Fill out the Form");

            CreateOrderController controller = loader.getController();
            controller.setPrimaryStage(formStage);

            OrderDetails orderDetails = new OrderDetails();
            orderDetails.setPaymentMethod(paymentMethod);
            orderDetails.setBillItems(billItems);

            controller.setData(orderDetails, "/com/bmh/hotelmanagementsystem/restaurant/restaurant-view.fxml");


            Scene formScene = new Scene(form);
            formStage.setScene(formScene);
            formStage.showAndWait();

            bill_flowPane.getChildren().clear();
            billItems.clear();

            subTotal = 0.0;
            discount = 0.0;
            tax = 0.0;
            total= 0.0;

            subtotalLabel.setText(String.valueOf(formatter.format(subTotal)));
            totalLabel.setText(String.valueOf(formatter.format(total)));

        } catch (Exception e) {
            e.printStackTrace();
            Utils.showGeneralErrorDialog();
        }
    }

    public void editMenuItem(MenuItemDto menuItemDto){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bmh/hotelmanagementsystem/restaurant/update_menuItem.fxml"));
            Region form = loader.load();

            Stage formStage = new Stage();
            formStage.initModality(Modality.APPLICATION_MODAL);
            formStage.setTitle("Fill out the Form");

            UpdateMenuItemController controller = loader.getController();
            controller.setPrimaryStage(formStage);
            controller.setData(menuItemDto, "/com/bmh/hotelmanagementsystem/restaurant/restaurant-view.fxml");

            Scene formScene = new Scene(form);
            formStage.setScene(formScene);
            formStage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            Utils.showGeneralErrorDialog();
        }
    }

    public void chargeToRoom(){
        if(total == 0){
            Utils.showAlertDialog(Alert.AlertType.ERROR, "Invalid Request", "No item Selected");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bmh/hotelmanagementsystem/restaurant/charge_to_room.fxml"));
            Region form = loader.load();

            Stage formStage = new Stage();
            formStage.initModality(Modality.APPLICATION_MODAL);
            formStage.setTitle("Charge to room");

            Controller controller = loader.getController();
            controller.setPrimaryStage(formStage);

            OrderDetails orderDetails = new OrderDetails();
            orderDetails.setPaymentMethod(paymentMethod);
            orderDetails.setBillItems(billItems);

            controller.setData(orderDetails, "/com/bmh/hotelmanagementsystem/restaurant/restaurant-view.fxml");

            Scene formScene = new Scene(form);
            formStage.setScene(formScene);
            formStage.showAndWait();

            bill_flowPane.getChildren().clear();
            billItems.clear();

            subTotal = 0.0;
            discount = 0.0;
            tax = 0.0;
            total= 0.0;

            subtotalLabel.setText(String.valueOf(formatter.format(subTotal)));
            totalLabel.setText(String.valueOf(formatter.format(total)));

        } catch (Exception e) {
            e.printStackTrace();
            Utils.showGeneralErrorDialog();
        }
    }

}
