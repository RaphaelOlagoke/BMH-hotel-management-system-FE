package com.bmh.hotelmanagementsystem.RoomManagement;

import com.bmh.hotelmanagementsystem.BackendService.RestClient;
import com.bmh.hotelmanagementsystem.BackendService.entities.ApiResponse;
import com.bmh.hotelmanagementsystem.BackendService.entities.ApiResponseSingleData;
import com.bmh.hotelmanagementsystem.BackendService.entities.Invoice.CreateInvoiceRequest;
import com.bmh.hotelmanagementsystem.BackendService.entities.Invoice.Invoice;
import com.bmh.hotelmanagementsystem.BackendService.entities.Invoice.Item;
import com.bmh.hotelmanagementsystem.BackendService.entities.Restaurant.CreateOrderRequest;
import com.bmh.hotelmanagementsystem.BackendService.entities.Restaurant.Order;
import com.bmh.hotelmanagementsystem.BackendService.entities.Room.Room;
import com.bmh.hotelmanagementsystem.BackendService.entities.Room.RoomService;
import com.bmh.hotelmanagementsystem.BackendService.enums.PaymentMethod;
import com.bmh.hotelmanagementsystem.BackendService.enums.PaymentStatus;
import com.bmh.hotelmanagementsystem.BackendService.enums.ServiceType;
import com.bmh.hotelmanagementsystem.Controller;
import com.bmh.hotelmanagementsystem.Utils;
import com.bmh.hotelmanagementsystem.dto.invoice.ItemRow;
import com.bmh.hotelmanagementsystem.dto.room.GuestReservation;
import com.bmh.hotelmanagementsystem.dto.room.RoomServiceRow;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.bmh.hotelmanagementsystem.Utils.showLoadingScreen;

public class RoomServiceController extends Controller {

    private Stage primaryStage;

    private String previousLocation;

    private Room data;

    @FXML
    private ComboBox<RoomService> room_service_comboBox;
    @FXML
    private TableView<RoomServiceRow> roomServiceTable;
    @FXML
    private TableColumn<RoomServiceRow, String> name_column;
    @FXML
    private TableColumn<RoomServiceRow, String> price_column;
    @FXML
    private TableColumn<RoomServiceRow, Void> removeItem;

    @FXML
    private Button charge;

    private ObservableList<RoomServiceRow> roomServiceDate = FXCollections.observableArrayList();


    private List<RoomService> roomServices = new ArrayList<>();
    private List<Item> items = new ArrayList<>();

    DecimalFormat formatter = new DecimalFormat("#,###.00");

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setData(Object data, String previousLocation){
        this.data = (Room) data;
        this.previousLocation = previousLocation;
    }

    public void initialize() {
        ObservableList<RoomService> room_services = FXCollections.observableArrayList();

        room_service_comboBox.setConverter(new StringConverter<RoomService>() {
            @Override
            public String toString(RoomService roomService) {
                return roomService != null ? roomService.getService() : "";
            }

            @Override
            public RoomService fromString(String string) {
                return null;
            }
        });

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

                        room_services.addAll(apiResponse.getData());

                        room_service_comboBox.setEditable(false);
                        room_service_comboBox.setItems(room_services);

//                        room_service_comboBox.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
//                            if (!newValue.equals(oldValue)) {
//                                ObservableList<RoomService> filteredItems = room_services.filtered(roomService ->
//                                        roomService.getService().toLowerCase().contains(newValue.toLowerCase())
//                                );
//                                room_service_comboBox.setItems(filteredItems);
//                            }
//                        });



                        room_service_comboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
                            if (newValue != null) {
                                RoomServiceRow roomServiceRow = new RoomServiceRow(newValue.getService(), "₦" + formatter.format(newValue.getPrice()), newValue);
                                roomServiceDate.add(roomServiceRow);
                                roomServices.add(newValue);
                                items.clear();
                                for (RoomService roomService : roomServices){
                                    Item invoiceItem = new Item();
                                    invoiceItem.setName(roomService.getService());
                                    invoiceItem.setPrice(roomService.getPrice());
                                    invoiceItem.setQuantity(1);
                                    items.add(invoiceItem);
                                }
                                roomServiceTable.setItems(roomServiceDate);
                            }
                        });

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


        name_column.setCellValueFactory(cellData -> cellData.getValue().name_columnProperty());
        price_column.setCellValueFactory(cellData -> cellData.getValue().price_columnProperty());
        removeItem.setCellFactory(createViewMoreButtonCellFactory());

        charge.setOnAction(event -> chargeToRoom());
    }

    private Callback<TableColumn<RoomServiceRow, Void>, TableCell<RoomServiceRow, Void>> createViewMoreButtonCellFactory() {
        return new Callback<>() {
            @Override
            public TableCell<RoomServiceRow, Void> call(TableColumn<RoomServiceRow, Void> param) {
                final TableCell<RoomServiceRow, Void> cell = new TableCell<RoomServiceRow, Void>() {

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
                            RoomServiceRow itemRow = getTableView().getItems().get(getIndex());
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

    public void removeInvoiceItem(RoomServiceRow roomServiceRow){
        roomServiceDate.remove(roomServiceRow);
        roomServices.remove(roomServiceRow.getRoomService());
        items.clear();
        for (RoomService roomService : roomServices){
            Item invoiceItem = new Item();
            invoiceItem.setName(roomService.getService());
            invoiceItem.setPrice(roomService.getPrice());
            invoiceItem.setQuantity(1);
            items.add(invoiceItem);
        }
        roomServiceTable.setItems(roomServiceDate);
    }

    public void chargeToRoom() {
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
                    if (items.isEmpty()) {
                        Platform.runLater(() -> {
                            loadingStage.close();
                            Utils.showAlertDialog(Alert.AlertType.INFORMATION, "Invalid request", "No Service");
                        });
                        return;
                    }

                    CreateInvoiceRequest request = new CreateInvoiceRequest();
                    request.setRoomNumber(data.getRoomNumber());
                    request.setPaymentStatus(PaymentStatus.UNPAID);
                    request.setPaymentMethod(PaymentMethod.NONE);
                    request.setService(ServiceType.ROOM);
                    request.setServiceDetails("Room Service");
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
