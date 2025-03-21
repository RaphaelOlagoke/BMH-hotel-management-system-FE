package com.bmh.hotelmanagementsystem.inventory;

import com.bmh.hotelmanagementsystem.BackendService.RestClient;
import com.bmh.hotelmanagementsystem.BackendService.entities.ApiResponse;
import com.bmh.hotelmanagementsystem.BackendService.entities.inventory.StockItem;
import com.bmh.hotelmanagementsystem.BackendService.entities.inventory.StockItemFilterRequest;
import com.bmh.hotelmanagementsystem.BackendService.enums.*;
import com.bmh.hotelmanagementsystem.Controller;
import com.bmh.hotelmanagementsystem.TokenStorage;
import com.bmh.hotelmanagementsystem.Utils;
import com.bmh.hotelmanagementsystem.dto.inventory.RefreshInventoryPageData;
import com.bmh.hotelmanagementsystem.dto.inventory.StockRow;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static com.bmh.hotelmanagementsystem.Utils.showLoadingScreen;

public class InventoryController extends Controller {

    private Stage primaryStage;

    private String previousLocation;

    private RefreshInventoryPageData data;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setData(Object data, String previousLocation){
        if(data != null){
            this.data = (RefreshInventoryPageData) data;
            currentlySelectedVBox = this.data.getCurrentlySelectedVBox();
            currentlySelectedLabel = this.data.getCurrentlySelectedLabel();

            onCategorySelected(roomVBox, roomLabel, this.data.getStockItemCategory());
        }
        this.previousLocation = previousLocation;
    }

    @FXML
    private Label user;


    @FXML
    private TextField searchField;
    @FXML
    private Button search;
    @FXML
    private Button roomCategory;
    @FXML
    private Button restaurantCategory;
    @FXML
    private Button houseKeepingCategory;
    @FXML
    private Button maintenanceCategory;
    @FXML
    private Button officeCategory;
    @FXML
    private Button othersCategory;
    @FXML
    private VBox roomVBox;
    @FXML
    private VBox restaurantVBox;
    @FXML
    private VBox houseKeepingVBox;
    @FXML
    private VBox maintenanceVBox;
    @FXML
    private VBox officeVBox;
    @FXML
    private VBox othersVBox;

    @FXML
    private Label roomLabel;
    @FXML
    private Label restaurantLabel;
    @FXML
    private Label houseKeepingLabel;
    @FXML
    private Label maintenanceLabel;
    @FXML
    private Label officeLabel;
    @FXML
    private Label othersLabel;

    @FXML
    private Label stockCategory;
    @FXML
    private DatePicker start_datePicker;
    @FXML
    private DatePicker end_datePicker;
    @FXML
    private DatePicker start_expirationDatePicker;
    @FXML
    private DatePicker end_expirationDatePicker;
    @FXML
    private Button create;

    @FXML
    private Button apply;

    @FXML
    private CheckBox lowInStock;

    @FXML
    private TableView<StockRow> stockItemTable;
    @FXML
    private TableColumn<StockRow, String> name_column;
    @FXML
    private TableColumn<StockRow, StockItemCategory> category_column;
    @FXML
    private TableColumn<StockRow, Integer> quantity_column;
    @FXML
    private TableColumn<StockRow, String> unit_column;
    @FXML
    private TableColumn<StockRow, String> created_date_column;
    @FXML
    private TableColumn<StockRow, String> expiration_date_column;
    @FXML
    private TableColumn<StockRow, Void> edit_column;

    @FXML
    private Pagination pagination;

    private VBox currentlySelectedVBox = null;
    private Label currentlySelectedLabel = null;

    private StockItemCategory currentlySelectedCategory = null;

    private final int lowStockQuantityThreshold = 100;

    private ObservableList<StockRow> stockData = FXCollections.observableArrayList();

    private int pageSize = 10;
    private int page = 0;

    @FXML
    public void initialize() throws IOException {
        String[] credentials = TokenStorage.loadCredentials();
        String username = "";
        if (credentials != null) {
            username = credentials[0];
        }
        user.setText("Hello " + username);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        start_datePicker.setConverter(new javafx.util.StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                return date != null ? date.format(formatter) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                try {
                    return string != null && !string.isEmpty() ? LocalDate.parse(string, formatter) : null;
                } catch (DateTimeParseException e) {

                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Invalid Date");
                    alert.setContentText("Invalid date entered. Please enter a valid date (yyyy-MM-dd).");
                    alert.showAndWait();
                    return null;
                }
            }
        });

        end_datePicker.setConverter(new javafx.util.StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                return date != null ? date.format(formatter) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                try {
                    return string != null && !string.isEmpty() ? LocalDate.parse(string, formatter) : null;
                } catch (DateTimeParseException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Invalid Date");
                    alert.setContentText("Invalid date entered. Please enter a valid date (yyyy-MM-dd).");
                    alert.showAndWait();
                    return null;
                }
            }
        });

        start_expirationDatePicker.setConverter(new javafx.util.StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                return date != null ? date.format(formatter) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                try {
                    return string != null && !string.isEmpty() ? LocalDate.parse(string, formatter) : null;
                } catch (DateTimeParseException e) {

                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Invalid Date");
                    alert.setContentText("Invalid date entered. Please enter a valid date (yyyy-MM-dd).");
                    alert.showAndWait();
                    return null;
                }
            }
        });

        end_expirationDatePicker.setConverter(new javafx.util.StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                return date != null ? date.format(formatter) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                try {
                    return string != null && !string.isEmpty() ? LocalDate.parse(string, formatter) : null;
                } catch (DateTimeParseException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Invalid Date");
                    alert.setContentText("Invalid date entered. Please enter a valid date (yyyy-MM-dd).");
                    alert.showAndWait();
                    return null;
                }
            }
        });

        roomCategory.setOnAction(event -> onCategorySelected(roomVBox, roomLabel, StockItemCategory.ROOM));
        restaurantCategory.setOnAction(event -> onCategorySelected(restaurantVBox, restaurantLabel, StockItemCategory.RESTAURANT_BAR));
        houseKeepingCategory.setOnAction(event -> onCategorySelected(houseKeepingVBox, houseKeepingLabel, StockItemCategory.HOUSE_KEEPING));
        maintenanceCategory.setOnAction(event -> onCategorySelected(maintenanceVBox, maintenanceLabel, StockItemCategory.MAINTENANCE));
        officeCategory.setOnAction(event -> onCategorySelected(officeVBox, officeLabel, StockItemCategory.OFFICE_SUPPLIES));
        othersCategory.setOnAction(event -> onCategorySelected(othersVBox, othersLabel, StockItemCategory.OTHERS));

        create.setOnAction(event -> create());
        search.setOnAction(event -> getStockItems(currentlySelectedCategory, page, pageSize));
        apply.setOnAction(event -> getStockItems(currentlySelectedCategory, page, pageSize));
        searchField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                getStockItems(currentlySelectedCategory, page, pageSize);
            }
        });

        name_column.setCellValueFactory(cellData -> cellData.getValue().name_columnProperty());
        category_column.setCellValueFactory(cellData -> cellData.getValue().category_columnProperty());
        quantity_column.setCellValueFactory(cellData -> cellData.getValue().quantity_columnProperty().asObject());
        unit_column.setCellValueFactory(cellData -> cellData.getValue().unit_columnProperty());
        created_date_column.setCellValueFactory(cellData -> cellData.getValue().created_date_columnProperty());
        expiration_date_column.setCellValueFactory(cellData -> cellData.getValue().expiration_date_columnProperty());
        edit_column.setCellFactory(createViewMoreButtonCellFactory());


        stockItemTable.setRowFactory(tableView -> {
            TableRow<StockRow> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    StockRow clickedRow = row.getItem();
                    showDetails(clickedRow);
                }
            });

            return row;
        });

        quantity_column.setCellFactory(column -> {
            return new TableCell<StockRow, Integer>() {
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);  // Reset cell content
                    } else {

                        Label label = new Label(String.valueOf(item));
                        label.setStyle("-fx-font-weight: bold; -fx-padding: 5px;");


                        if (item >= lowStockQuantityThreshold) {
                            label.setStyle("-fx-text-fill: green; -fx-background-color: #e0f7e0; -fx-font-weight: bold; -fx-padding: 5px 10px; -fx-background-radius: 5;");
                        } else if (item < lowStockQuantityThreshold) {
                            label.setStyle("-fx-text-fill: red; -fx-background-color: #f7e0e0; -fx-font-weight: bold; -fx-padding: 5px 15px; -fx-background-radius: 5;");
                        } else {
                            label.setStyle("-fx-padding: 5px;");
                        }

                        setGraphic(label);  // Set the Label as the graphic for this cell
                    }
                }
            };
        });

        pagination.setPageFactory(this::createPage);
    }

    private void loadPage(int page, int size) {
        if (currentlySelectedCategory != null){
            getStockItems(currentlySelectedCategory, page, size);
        }
    }

    private Node createPage(int pageIndex) {
        loadPage(pageIndex, pageSize);
        return stockItemTable;
    }

    private void onCategorySelected(VBox selectedVBox, Label selectedLabel, StockItemCategory category) {
        if (currentlySelectedVBox != null) {
            currentlySelectedVBox.getStyleClass().remove("selected-vbox");
            currentlySelectedLabel.getStyleClass().remove("selected-label");
        }

        currentlySelectedCategory = category;
        selectedVBox.getStyleClass().add("selected-vbox");
        selectedLabel.getStyleClass().add("selected-label");
        stockCategory.setText(selectedLabel.getText());

        currentlySelectedVBox = selectedVBox;
        currentlySelectedLabel = selectedLabel;

        getStockItems(category, page, pageSize);
    }

    public void getStockItems(StockItemCategory category, int page, int size){
        Stage loadingStage = showLoadingScreen(primaryStage);

        Platform.runLater(() -> loadingStage.show());

        new Thread(() -> {
            ObservableList<Integer> listOfRooms = FXCollections.observableArrayList();
            listOfRooms.add(null);

            try {
                StockItemFilterRequest request = new StockItemFilterRequest();
                request.setCategory(category);
                request.setStartDate(start_datePicker.getValue() != null ? start_datePicker.getValue().atStartOfDay() : null);
                request.setEndDate(end_datePicker.getValue() != null ? end_datePicker.getValue().atTime(23, 59, 0) : null);
                request.setStartExpiryDate(start_expirationDatePicker.getValue() != null ? start_expirationDatePicker.getValue() : null);
                request.setEndExpiryDate(end_expirationDatePicker.getValue() != null ? end_expirationDatePicker.getValue() : null);
                if(lowInStock.isSelected()) {
                    request.setQuantity(lowStockQuantityThreshold);
                }

                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());

                String jsonString = objectMapper.writeValueAsString(request);

                String response = RestClient.post("/inventory/filterStockItems?page=" + page + "&size=" + size + "&query=" + searchField.getText(), jsonString);

                ApiResponse<StockItem> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponse<StockItem>>() {
                });

                if (apiResponse.getResponseHeader().getResponseCode().equals("00")) {
                    Platform.runLater(() -> {
                        loadingStage.close();

                        stockData.clear();

                        for (StockItem item : apiResponse.getData()) {

                            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                            String createdDate = item.getCreatedDateTime().format(dateTimeFormatter);
                            String expirationDate = "";
                            if (item.getExpiryDate() != null) {
                                expirationDate = item.getExpiryDate().format(dateFormatter);
                            }

                            StockRow stockRow = new StockRow(item.getName(), item.getCategory(), item.getQuantity(),
                                    item.getUnit(), createdDate, expirationDate,item);

                            stockData.add(stockRow);
                        }

                        stockItemTable.setItems(stockData);
                        pagination.setPageCount(apiResponse.getTotalPages());
                        pagination.setCurrentPageIndex(page);

                    });
                }
                else {
                    Platform.runLater(() -> {
                        loadingStage.close();
                        Utils.showAlertDialog(Alert.AlertType.ERROR,apiResponse.getResponseHeader().getResponseMessage(),apiResponse.getError() );
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
    }

    private Callback<TableColumn<StockRow, Void>, TableCell<StockRow, Void>> createViewMoreButtonCellFactory() {
        return new Callback<>() {
            @Override
            public TableCell<StockRow, Void> call(TableColumn<StockRow, Void> param) {
                final TableCell<StockRow, Void> cell = new TableCell<StockRow, Void>() {

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
                            StockRow stockRow = getTableView().getItems().get(getIndex());
                            showDetails(stockRow);
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

                String response = RestClient.get("/inventory/searchStockItems?query=" + query);
                ApiResponse<StockItem> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponse<StockItem>>() {});


                Platform.runLater(() -> {
                    try {
                        if (apiResponse.getResponseHeader().getResponseCode().equals("00")) {
                            Platform.runLater(() -> {
                                loadingStage.close();

                                stockData.clear();

                                for (StockItem item : apiResponse.getData()) {

                                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                                    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                                    String createdDate = item.getCreatedDateTime().format(dateTimeFormatter);
                                    String expirationDate = "";
                                    if (item.getExpiryDate() != null) {
                                        expirationDate = item.getExpiryDate().format(dateFormatter);
                                    }

                                    StockRow stockRow = new StockRow(item.getName(), item.getCategory(), item.getQuantity(),
                                            item.getUnit(), createdDate, expirationDate,item);

                                    stockData.add(stockRow);
                                }

                                stockItemTable.setItems(stockData);
                            });
                        }
                        else {
                            Platform.runLater(() -> {
                                loadingStage.close();
                                Utils.showAlertDialog(Alert.AlertType.ERROR,apiResponse.getResponseHeader().getResponseMessage(),apiResponse.getError() );
                            });
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

    public void showDetails(StockRow stockRow){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bmh/hotelmanagementsystem/inventory/update_item.fxml"));
            Region form = loader.load();

            Stage formStage = new Stage();
            formStage.initModality(Modality.APPLICATION_MODAL);
            formStage.setTitle("Fill out the Form");

            UpdateStockController controller = loader.getController();
            controller.setPrimaryStage(formStage);
            controller.setData(stockRow.getStockItem(), "/com/bmh/hotelmanagementsystem/inventory/inventory-view.fxml");

            Scene formScene = new Scene(form);
            formStage.setScene(formScene);
            formStage.showAndWait();

            refresh(stockRow.getStockItem().getCategory());


        } catch (Exception e) {
            e.printStackTrace();
            Utils.showGeneralErrorDialog();
        }
    }

    public void create(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bmh/hotelmanagementsystem/inventory/create-item.fxml"));
            Region form = loader.load();

            Stage formStage = new Stage();
            formStage.initModality(Modality.APPLICATION_MODAL);
            formStage.setTitle("Fill out the Form");

            CreateStockController controller = loader.getController();
            controller.setPrimaryStage(formStage);
            controller.setData(null, "/com/bmh/hotelmanagementsystem/inventory/inventory-view.fxml");

            Scene formScene = new Scene(form);
            formStage.setScene(formScene);
            formStage.showAndWait();

            refresh(StockItemCategory.ROOM);

        } catch (Exception e) {
            e.printStackTrace();
            Utils.showGeneralErrorDialog();
        }
    }

    public void refresh(StockItemCategory stockItemCategory){
        try {
            RefreshInventoryPageData refreshPageData = new RefreshInventoryPageData();
            refreshPageData.setCurrentlySelectedVBox(currentlySelectedVBox);
            refreshPageData.setCurrentlySelectedLabel(currentlySelectedLabel);
            refreshPageData.setStockItemCategory(stockItemCategory);
            Utils utils = new Utils();
            utils.switchScreenWithData("/com/bmh/hotelmanagementsystem/inventory/inventory-view.fxml",primaryStage, refreshPageData, previousLocation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
