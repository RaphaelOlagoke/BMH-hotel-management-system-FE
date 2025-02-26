package com.bmh.hotelmanagementsystem.invoice;

import com.bmh.hotelmanagementsystem.BackendService.RestClient;
import com.bmh.hotelmanagementsystem.BackendService.entities.ApiResponseSingleData;
import com.bmh.hotelmanagementsystem.BackendService.entities.HouseKeeping.CleaningLog;
import com.bmh.hotelmanagementsystem.BackendService.entities.HouseKeeping.CreateCleaningLogRequest;
import com.bmh.hotelmanagementsystem.BackendService.entities.Invoice.CreateInvoiceRequest;
import com.bmh.hotelmanagementsystem.BackendService.entities.Invoice.Invoice;
import com.bmh.hotelmanagementsystem.BackendService.entities.Invoice.Item;
import com.bmh.hotelmanagementsystem.BackendService.entities.Room.Room;
import com.bmh.hotelmanagementsystem.BackendService.enums.PaymentMethod;
import com.bmh.hotelmanagementsystem.BackendService.enums.PaymentStatus;
import com.bmh.hotelmanagementsystem.BackendService.enums.ServiceType;
import com.bmh.hotelmanagementsystem.Controller;
import com.bmh.hotelmanagementsystem.Utils;
import com.bmh.hotelmanagementsystem.dto.invoice.InvoiceRow;
import com.bmh.hotelmanagementsystem.dto.invoice.ItemRow;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CreateInvoiceController  extends Controller {

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
    private ComboBox<String> payment_method_comboBox;
    @FXML
    private ComboBox<String> service_comboBox;
    @FXML
    private ComboBox<String> payment_status_comboBox;

    @FXML
    private TextField serviceDetails;

    @FXML
    private TableView<ItemRow> invoiceItemTable;
    @FXML
    private TableColumn<ItemRow, String> name_column;
    @FXML
    private TableColumn<ItemRow, Integer> quantity_column;
    @FXML
    private TableColumn<ItemRow, String> price_column;

    @FXML
    private TableColumn<ItemRow, Void> removeItem;

    @FXML
    private Button create;


    @FXML
    private TextField itemName;
    @FXML
    private TextField itemQuantity;
    @FXML
    private TextField itemPrice;
    @FXML
    private Button addItem;

    private ObservableList<ItemRow> invoiceItemData = FXCollections.observableArrayList();


    private List<Item> items = new ArrayList<>();

    DecimalFormat formatter = new DecimalFormat("#,###.00");

    public void initialize() {
        ObservableList<String> payment_status = FXCollections.observableArrayList();

        payment_status.add(null);
        for (PaymentStatus status : PaymentStatus.values()) {
            payment_status.add(status.toJson());
        }

        payment_status_comboBox.setItems(payment_status);

        ObservableList<String> payment_method = FXCollections.observableArrayList();

        payment_method.add(null);
        for (PaymentMethod status : PaymentMethod.values()) {
            payment_method.add(status.toJson());
        }

        payment_method_comboBox.setItems(payment_method);

        ObservableList<String> service_type = FXCollections.observableArrayList();

        service_type.add(null);
        for (ServiceType status : ServiceType.values()) {
            service_type.add(status.toJson());
        }

        service_comboBox.setItems(service_type);

        name_column.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        quantity_column.setCellValueFactory(cellData -> cellData.getValue().quantityProperty().asObject());
        price_column.setCellValueFactory(cellData -> cellData.getValue().priceProperty());
        removeItem.setCellFactory(createViewMoreButtonCellFactory());

        create.setOnAction(event -> createInvoice());
        addItem.setOnAction(event -> addInvoiceItem());
    }

    private Callback<TableColumn<ItemRow, Void>, TableCell<ItemRow, Void>> createViewMoreButtonCellFactory() {
        return new Callback<>() {
            @Override
            public TableCell<ItemRow, Void> call(TableColumn<ItemRow, Void> param) {
                final TableCell<ItemRow, Void> cell = new TableCell<ItemRow, Void>() {

//                    final FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bmh/hotelmanagementsystem/components/remove_item_button.fxml"));
//                    final Button button;
//
//                    {
//                        try {
//                            button = loader.load();
//                        } catch (IOException e) {
//                            throw new RuntimeException(e);
//                        }
//                    }

                    private final Button btn = new Button("Remove");

                    {
                        btn.setStyle("-fx-text-fill: red; -fx-background-color: #f7e0e0; -fx-font-weight: bold; -fx-padding: 5px 15px; -fx-background-radius: 5;");
                        btn.setOnAction(event -> {
                            ItemRow itemRow = getTableView().getItems().get(getIndex());
                            removeInvoiceItem(itemRow);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
                return cell;
            }
        };
    }


    public void addInvoiceItem(){
        int quantity;
        Double price;
        String formattedPrice;
        try{
            quantity = Integer.valueOf(itemQuantity.getText());
            price = Double.valueOf(itemPrice.getText());
            formattedPrice = formatter.format(price);
        }
        catch (Exception e){
            Utils.showAlertDialog(Alert.AlertType.INFORMATION,"Invalid request","Invalid Price or quantity");
            return;
        }
        Item item = new Item();
        item.setName(itemName.getText());
        item.setQuantity(quantity);
        item.setPrice(price);

        ItemRow itemRow = new ItemRow(itemName.getText(), quantity, formattedPrice,item);

        invoiceItemData.add(itemRow);
        items.add(item);
        invoiceItemTable.setItems(invoiceItemData);

        itemName.clear();
        itemQuantity.clear();
        itemPrice.clear();
    }

    public void removeInvoiceItem(ItemRow itemRow){
        invoiceItemData.remove(itemRow);
        items.remove(itemRow.getItem());
        invoiceItemTable.setItems(invoiceItemData);
    }

    public void createInvoice(){
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirm Payment");
        confirmationAlert.setHeaderText("Are you sure you want to create this invoice?");
        confirmationAlert.setContentText("Please confirm your action.");

        Optional<ButtonType> result = confirmationAlert.showAndWait();


        if (result.isPresent() && result.get() == ButtonType.OK) {

            Stage loadingStage = Utils.showLoadingScreen(primaryStage);
            Platform.runLater(() -> loadingStage.show());

            new Thread(() -> {
                try {
                    if (payment_status_comboBox.getSelectionModel().getSelectedItem() == null) {
                        Platform.runLater(() -> {
                            loadingStage.close();
                            Utils.showAlertDialog(Alert.AlertType.INFORMATION, "Invalid request", "Payment Status cannot be empty");
                        });
                        return;
                    }
                    if (payment_method_comboBox.getSelectionModel().getSelectedItem() == null) {
                        Platform.runLater(() -> {
                            loadingStage.close();
                            Utils.showAlertDialog(Alert.AlertType.INFORMATION, "Invalid request", "Payment Method cannot be empty");
                        });
                        return;
                    }
                    if (service_comboBox.getSelectionModel().getSelectedItem() == null) {
                        Platform.runLater(() -> {
                            loadingStage.close();
                            Utils.showAlertDialog(Alert.AlertType.INFORMATION, "Invalid request", "Service type cannot be null");
                        });
                        return;
                    }

                    if (items.isEmpty()) {
                        Platform.runLater(() -> {
                            loadingStage.close();
                            Utils.showAlertDialog(Alert.AlertType.INFORMATION, "Invalid request", "Invoice items cannot be empty");
                        });
                        return;
                    }

                    CreateInvoiceRequest request = new CreateInvoiceRequest();
                    request.setPaymentStatus(PaymentStatus.valueOf(payment_status_comboBox.getSelectionModel().getSelectedItem()));
                    request.setPaymentMethod(PaymentMethod.valueOf(payment_method_comboBox.getSelectionModel().getSelectedItem()));
                    request.setService(ServiceType.valueOf(service_comboBox.getSelectionModel().getSelectedItem()));
                    request.setServiceDetails(serviceDetails.getText());
                    request.setItems(items);

                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.registerModule(new JavaTimeModule());

                    String jsonString = objectMapper.writeValueAsString(request);

                    String response = RestClient.post("/invoice/", jsonString);
                    ApiResponseSingleData<Invoice> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponseSingleData<Invoice>>() {
                    });

                    if (apiResponse.getResponseHeader().getResponseCode().equals("00")) {
                        Platform.runLater(() -> {
                            loadingStage.close();
                            Utils.showAlertDialog(Alert.AlertType.INFORMATION, apiResponse.getResponseHeader().getResponseMessage(), apiResponse.getError());
                            primaryStage.close();

                        });
                    } else {
                        Platform.runLater(() -> {
                            loadingStage.close();
                            Utils.showAlertDialog(Alert.AlertType.ERROR, apiResponse.getResponseHeader().getResponseMessage(), apiResponse.getError());
                            primaryStage.close();
                        });

                    }

                } catch (Exception e) {
                    Platform.runLater(() -> {
                        loadingStage.close();
                        e.printStackTrace();
                        Utils.showGeneralErrorDialog();
                        primaryStage.close();
                    });
                }
            }).start();
        }
        else {
            confirmationAlert.close();
        }

    }
}
