package com.bmh.hotelmanagementsystem.discount;

import com.bmh.hotelmanagementsystem.BackendService.RestClient;
import com.bmh.hotelmanagementsystem.BackendService.entities.Invoice.InvoiceSummary;
import com.bmh.hotelmanagementsystem.BackendService.entities.discount.Discount;
import com.bmh.hotelmanagementsystem.BackendService.entities.discount.DiscountLogFilterRequest;
import com.bmh.hotelmanagementsystem.BackendService.entities.refund.Refund;
import com.bmh.hotelmanagementsystem.BackendService.entities.refund.RefundLogFilterRequest;
import com.bmh.hotelmanagementsystem.dto.discount.DiscountRow;
import com.bmh.hotelmanagementsystem.dto.invoice.InvoiceRow;
import com.bmh.hotelmanagementsystem.BackendService.entities.*;
import com.bmh.hotelmanagementsystem.BackendService.entities.Invoice.Invoice;
import com.bmh.hotelmanagementsystem.BackendService.entities.Invoice.InvoiceLogFilterRequest;
import com.bmh.hotelmanagementsystem.BackendService.enums.*;
import com.bmh.hotelmanagementsystem.Controller;
import com.bmh.hotelmanagementsystem.Utils;
import com.bmh.hotelmanagementsystem.dto.refund.RefundRow;
import com.bmh.hotelmanagementsystem.dto.room.RoomManagement;
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
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static com.bmh.hotelmanagementsystem.Utils.showLoadingScreen;

public class DiscountLogController extends Controller {

    private Stage primaryStage;

    private String previousLocation;

    private Object data;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setData(Object data, String previousLocation) {
        this.data = data;
        this.previousLocation = previousLocation;
    }


//    @FXML
//    private TextField searchField;
//    @FXML
//    private Button search;
    @FXML
    private Pagination pagination;

    @FXML
    private TableView<DiscountRow> discountTable;
    @FXML
    private TableColumn<DiscountRow, String> discount_code_column;
    @FXML
    private TableColumn<DiscountRow, String> discount_percentage_column;
    @FXML
    private TableColumn<DiscountRow, String> start_date_column;
    @FXML
    private TableColumn<DiscountRow, String> end_date_column;
    @FXML
    private TableColumn<DiscountRow, Boolean> is_active_column;
    @FXML
    private TableColumn<DiscountRow, Boolean> is_one_time_use_column;
    @FXML
    private TableColumn<DiscountRow, Void> viewMore;

    @FXML
    private ComboBox<Boolean> isActive_comboBox;
    @FXML
    private ComboBox<Boolean> isOneTimeUse_comboBox;

    @FXML
    private DatePicker start_datePicker;
    @FXML
    private DatePicker end_datePicker;
    @FXML
    private Button apply;
    @FXML
    private Button create;

    private ObservableList<DiscountRow> discountData = FXCollections.observableArrayList();

    private int pageSize = 10;
    private int page = 0;

    DecimalFormat priceFormatter = new DecimalFormat("#,###.00");

    public void initialize() {

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

        ObservableList<Boolean> booleanObservableList = FXCollections.observableArrayList();

        booleanObservableList.add(null);
        booleanObservableList.add(true);
        booleanObservableList.add(false);


        isActive_comboBox.setItems(booleanObservableList);
        isOneTimeUse_comboBox.setItems(booleanObservableList);

        discount_code_column.setCellValueFactory(cellData -> cellData.getValue().discount_code_columnProperty());
        discount_percentage_column.setCellValueFactory(cellData -> cellData.getValue().discount_percentage_columnProperty());
        start_date_column.setCellValueFactory(cellData -> cellData.getValue().start_date_columnProperty());
        end_date_column.setCellValueFactory(cellData -> cellData.getValue().end_date_columnProperty());
        is_active_column.setCellValueFactory(cellData -> cellData.getValue().is_active_columnProperty());
        is_one_time_use_column.setCellValueFactory(cellData -> cellData.getValue().is_one_time_use_columnProperty());

        viewMore.setCellFactory(createViewMoreButtonCellFactory());

        is_active_column.setCellFactory(column -> {
            return new TableCell<DiscountRow, Boolean>() {
                @Override
                protected void updateItem(Boolean item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);  // Reset cell content
                    } else {
                        // Create a Label for the status
                        Label label = new Label(item.toString());
                        label.setStyle("-fx-font-weight: bold; -fx-padding: 5px;");

                        // Apply styles based on the status
                        if (item) {
                            label.setText("YES");
                            label.setStyle("-fx-text-fill: green; -fx-background-color: #e0f7e0; -fx-font-weight: bold; -fx-padding: 5px 10px; -fx-background-radius: 5;");
                        } else {
                            label.setText("NO");
                            label.setStyle("-fx-text-fill: red; -fx-background-color: #f7e0e0; -fx-font-weight: bold; -fx-padding: 5px 15px; -fx-background-radius: 5;");
                        }

                        setGraphic(label);  // Set the Label as the graphic for this cell
                    }
                }
            };
        });

        is_one_time_use_column.setCellFactory(column -> {
            return new TableCell<DiscountRow, Boolean>() {
                @Override
                protected void updateItem(Boolean item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);  // Reset cell content
                    } else {
                        // Create a Label for the status
                        Label label = new Label(item.toString());
                        label.setStyle("-fx-font-weight: bold; -fx-padding: 5px;");

                        // Apply styles based on the status
                        if (item) {
                            label.setText("YES");
                            label.setStyle("-fx-text-fill: green; -fx-background-color: #e0f7e0; -fx-font-weight: bold; -fx-padding: 5px 10px; -fx-background-radius: 5;");
                        } else {
                            label.setText("NO");
                            label.setStyle("-fx-text-fill: red; -fx-background-color: #f7e0e0; -fx-font-weight: bold; -fx-padding: 5px 15px; -fx-background-radius: 5;");
                        }

                        setGraphic(label);  // Set the Label as the graphic for this cell
                    }
                }
            };
        });

        pagination.setPageFactory(this::createPage);


        apply.setOnAction(event -> apply(page, pageSize));
//        search.setOnAction(event -> apply(page, pageSize));
//        searchField.setOnKeyPressed(event -> {
//            if (event.getCode() == KeyCode.ENTER) {
//                apply(page, pageSize);
//            }
//        });
        create.setOnAction(event -> create());
    }

    private void loadPage(int page, int size) {
        apply(page, size);
    }

    private Node createPage(int pageIndex) {
        loadPage(pageIndex, pageSize);
        return discountTable;
    }

    private <T> void setWrapCellFactory(TableColumn<T, String> column) {
        column.setCellFactory(c -> {
            return new javafx.scene.control.TableCell<T, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        Text text = new Text(item);
                        text.setWrappingWidth(100); // Set the wrapping width of the text (adjust as needed)
                        setGraphic(text);
                    }
                }
            };
        });
    }

    private Callback<TableColumn<DiscountRow, Void>, TableCell<DiscountRow, Void>> createViewMoreButtonCellFactory() {
        return new Callback<>() {
            @Override
            public TableCell<DiscountRow, Void> call(TableColumn<DiscountRow, Void> param) {
                final TableCell<DiscountRow, Void> cell = new TableCell<DiscountRow, Void>() {

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
                            DiscountRow discountRow = getTableView().getItems().get(getIndex());
                            updateDiscount(discountRow);  // Call a method to handle the button click
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

    public void apply(int page, int size){
        Stage loadingStage = Utils.showLoadingScreen(primaryStage);
        loadingStage.show();

        new Thread(() -> {

            try {
                DiscountLogFilterRequest request = new DiscountLogFilterRequest();
                request.setActive(isActive_comboBox.getSelectionModel().getSelectedItem() != null ? isActive_comboBox.getSelectionModel().getSelectedItem() : true );
                request.setOneTimeUse(isOneTimeUse_comboBox.getSelectionModel().getSelectedItem() != null ? isOneTimeUse_comboBox.getSelectionModel().getSelectedItem() : true );

                request.setStartDate(start_datePicker.getValue() != null ? start_datePicker.getValue().atStartOfDay() : null);
                request.setEndDate(end_datePicker.getValue() != null ? end_datePicker.getValue().atTime(23, 59, 0) : null);


                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());

                String jsonString = objectMapper.writeValueAsString(request);

                String response = RestClient.post("/discount/filter?page=" + page + "&size=" + size, jsonString);
                ApiResponse<Discount> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponse<Discount>>() {});


                Platform.runLater(() -> {
                    try {
                        if (apiResponse.getResponseHeader().getResponseCode().equals("00")) {
                            // Close the loading screen
                            loadingStage.close();

                            discountData.clear();
                            for (Discount discount : apiResponse.getData()){

                                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                                String startDate = "";
                                String endDate = "";
                                if (discount.getStartDate() != null) {
                                    startDate = discount.getStartDate().format(dateTimeFormatter);
                                }
                                if (discount.getEndDate() != null) {
                                    endDate = discount.getEndDate().format(dateTimeFormatter);
                                }
                                DiscountRow discountRow = new DiscountRow(discount.getCode(), discount.getPercentage() + "%",
                                        startDate, endDate, discount.isActive(), discount.isOneTimeUse(), discount);

                                discountData.add(discountRow);
                            }
                            discountTable.setItems(discountData);
                            pagination.setPageCount(apiResponse.getTotalPages());
                            pagination.setCurrentPageIndex(page);

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
                    e.printStackTrace();
                    loadingStage.close();
                    Utils.showGeneralErrorDialog();
                });
            }

        }).start();
    }

    public void updateDiscount(DiscountRow discountRow){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bmh/hotelmanagementsystem/discount/update_discount.fxml"));
            Region form = loader.load();

            Stage formStage = new Stage();
            formStage.initModality(Modality.APPLICATION_MODAL);
            formStage.setTitle("Fill out the Form");

            Controller controller = loader.getController();
            controller.setPrimaryStage(formStage);
            controller.setData(discountRow.getDiscount(), "/com/bmh/hotelmanagementsystem/discount/discount-log-view.fxml");

            Scene formScene = new Scene(form);
            formStage.setScene(formScene);
            formStage.showAndWait();

            apply(page, pageSize);
        } catch (Exception e) {
            e.printStackTrace();
            Utils.showGeneralErrorDialog();
        }
    }

    public void create(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bmh/hotelmanagementsystem/discount/create_discount.fxml"));
            Region form = loader.load();

            Stage formStage = new Stage();
            formStage.initModality(Modality.APPLICATION_MODAL);
            formStage.setTitle("Fill out the Form");

            Controller controller = loader.getController();
            controller.setPrimaryStage(formStage);
            controller.setData(null, "/com/bmh/hotelmanagementsystem/discount/discount-log-view.fxml");

            Scene formScene = new Scene(form);
            formStage.setScene(formScene);
            formStage.showAndWait();

            apply(page, pageSize);
        } catch (Exception e) {
            e.printStackTrace();
            Utils.showGeneralErrorDialog();
        }
    }

}
