package com.bmh.hotelmanagementsystem.invoice;

import com.bmh.hotelmanagementsystem.BackendService.RestClient;
import com.bmh.hotelmanagementsystem.dto.invoice.InvoiceRow;
import com.bmh.hotelmanagementsystem.BackendService.entities.*;
import com.bmh.hotelmanagementsystem.BackendService.entities.Invoice.Invoice;
import com.bmh.hotelmanagementsystem.BackendService.entities.Invoice.InvoiceLogFilterRequest;
import com.bmh.hotelmanagementsystem.BackendService.enums.*;
import com.bmh.hotelmanagementsystem.Controller;
import com.bmh.hotelmanagementsystem.Utils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static com.bmh.hotelmanagementsystem.Utils.showLoadingScreen;

public class InvoiceLogController extends Controller {

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

    @FXML
    private TableView<InvoiceRow> invoiceTable;
    @FXML
    private TableColumn<InvoiceRow, String> ref_column;
    @FXML
    private TableColumn<InvoiceRow, String> issue_date_column;
    @FXML
    private TableColumn<InvoiceRow, String> payment_date_column;
    @FXML
    private TableColumn<InvoiceRow, PaymentMethod> payment_method_column;
    @FXML
    private TableColumn<InvoiceRow, PaymentStatus> payment_status_column;
    @FXML
    private TableColumn<InvoiceRow, ServiceType> service_column;
    @FXML
    private TableColumn<InvoiceRow, String> service_details_column;
    @FXML
    private TableColumn<InvoiceRow, Double> amount_paid_column;

    @FXML
    private TableColumn<InvoiceRow, Void> viewMore;

    @FXML
    private ComboBox<String> payment_method_comboBox;
    @FXML
    private ComboBox<String> service_comboBox;
    @FXML
    private ComboBox<String> payment_status_comboBox;
    @FXML
    private DatePicker start_datePicker;
    @FXML
    private DatePicker end_datePicker;
    @FXML
    private Button apply;

    private ObservableList<InvoiceRow> invoiceData = FXCollections.observableArrayList();


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

        Stage loadingStage = showLoadingScreen(primaryStage);

        Platform.runLater(() -> loadingStage.show());

        new Thread(() -> {
            ObservableList<Integer> listOfRooms = FXCollections.observableArrayList();
            listOfRooms.add(null);

            try {
                String response = RestClient.get("/invoice/");

                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());

                ApiResponse<Invoice> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponse<Invoice>>() {
                });

                if (apiResponse.getResponseHeader().getResponseCode().equals("00")) {
                    Platform.runLater(() -> {
                        loadingStage.close();

                        ref_column.setCellValueFactory(cellData -> cellData.getValue().ref_columnProperty());
                        issue_date_column.setCellValueFactory(cellData -> cellData.getValue().issue_date_columnProperty());
                        payment_date_column.setCellValueFactory(cellData -> cellData.getValue().payment_date_columnProperty());
                        payment_method_column.setCellValueFactory(cellData -> cellData.getValue().payment_method_columnProperty());
                        payment_status_column.setCellValueFactory(cellData -> cellData.getValue().payment_status_columnProperty());
                        service_column.setCellValueFactory(cellData -> cellData.getValue().service_columnProperty());
                        service_details_column.setCellValueFactory(cellData -> cellData.getValue().service_details_columnProperty());
                        amount_paid_column.setCellValueFactory(cellData -> cellData.getValue().amount_paid_columnProperty().asObject());

                        viewMore.setCellFactory(createViewMoreButtonCellFactory());

                        setWrapCellFactory(ref_column);
                        setWrapCellFactory(issue_date_column);
                        setWrapCellFactory(payment_date_column);
//                        setWrapCellFactory(payment_method_column);
//                        setWrapCellFactory(payment_status_column);
//                        setWrapCellFactory(service_column);
                        setWrapCellFactory(service_details_column);

                        invoiceTable.setRowFactory(tableView -> {
                            TableRow<InvoiceRow> row = new TableRow<>();
                            row.setOnMouseClicked(event -> {
                                if (!row.isEmpty()) {
                                    InvoiceRow clickedRow = row.getItem();
                                    showInvoiceDetails(clickedRow);
                                }
                            });

                            return row;
                        });

                        for (Invoice invoice : apiResponse.getData()) {

                            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                            String issueDate = invoice.getIssueDate().format(dateTimeFormatter);
                            String paymentDate = "";
                            if (invoice.getPaymentDate() != null) {
                                paymentDate = invoice.getPaymentDate().format(dateTimeFormatter);
                            }
                            Double amount_paid = invoice.getTotalAmount() != null ? invoice.getTotalAmount() : 0.0;
                            InvoiceRow invoiceRow = new InvoiceRow(invoice.getRef(), issueDate,
                                    paymentDate, invoice.getPaymentMethod(), invoice.getPaymentStatus(),
                                    invoice.getService(), invoice.getServiceDetails(), amount_paid, invoice);

                            invoiceData.add(invoiceRow);
                        }

                        invoiceTable.setItems(invoiceData);

                        payment_status_column.setCellFactory(column -> {
                            return new TableCell<InvoiceRow, PaymentStatus>() {
                                @Override
                                protected void updateItem(PaymentStatus item, boolean empty) {
                                    super.updateItem(item, empty);
                                    if (empty || item == null) {
                                        setText(null);
                                        setGraphic(null);  // Reset cell content
                                    } else {

                                        Label label = new Label(item.toJson());
                                        label.setStyle("-fx-font-weight: bold; -fx-padding: 5px;");


                                        if (item == PaymentStatus.COMPLETE) {
                                            label.setStyle("-fx-text-fill: green; -fx-background-color: #e0f7e0; -fx-font-weight: bold; -fx-padding: 5px; -fx-background-radius: 15;");
                                        } else if (item == PaymentStatus.DUE) {
                                            label.setStyle("-fx-text-fill: red; -fx-background-color: #f7e0e0; -fx-font-weight: bold; -fx-padding: 7px 15px; -fx-background-radius: 15;");
                                        } else {
                                            label.setStyle("-fx-padding: 5px;");
                                        }

                                        setGraphic(label);  // Set the Label as the graphic for this cell
                                    }
                                }
                            };
                        });


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

        apply.setOnAction(event -> apply());
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

    private Callback<TableColumn<InvoiceRow, Void>, TableCell<InvoiceRow, Void>> createViewMoreButtonCellFactory() {
        return new Callback<>() {
            @Override
            public TableCell<InvoiceRow, Void> call(TableColumn<InvoiceRow, Void> param) {
                final TableCell<InvoiceRow, Void> cell = new TableCell<InvoiceRow, Void>() {

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
                            InvoiceRow invoiceRow = getTableView().getItems().get(getIndex());
                            showInvoiceDetails(invoiceRow);  // Call a method to handle the button click
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

    public void apply(){
        Stage loadingStage = Utils.showLoadingScreen(primaryStage);
        loadingStage.show();

        new Thread(() -> {

            try {
                InvoiceLogFilterRequest request = new InvoiceLogFilterRequest();
                request.setPaymentMethod(payment_method_comboBox.getSelectionModel().getSelectedItem() != null? PaymentMethod.valueOf(payment_method_comboBox.getSelectionModel().getSelectedItem()) : null);
                request.setPaymentStatus(payment_status_comboBox.getSelectionModel().getSelectedItem() != null? PaymentStatus.valueOf(payment_status_comboBox.getSelectionModel().getSelectedItem()) : null);
                request.setService(service_comboBox.getSelectionModel().getSelectedItem() != null? ServiceType.valueOf(service_comboBox.getSelectionModel().getSelectedItem()) : null);

                request.setStartDate(start_datePicker.getValue() != null ? start_datePicker.getValue().atStartOfDay() : null);
                request.setEndDate(end_datePicker.getValue() != null ? end_datePicker.getValue().atTime(23, 59, 0) : null);


                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());

                String jsonString = objectMapper.writeValueAsString(request);

                String response = RestClient.post("/invoice/filter", jsonString);
                ApiResponse<Invoice> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponse<Invoice>>() {});


                Platform.runLater(() -> {
                    try {
                        if (apiResponse.getResponseHeader().getResponseCode().equals("00")) {
                            // Close the loading screen
                            loadingStage.close();

                            invoiceData.clear();
                            for (Invoice invoice : apiResponse.getData()){

                                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                                String issueDate = invoice.getIssueDate().format(dateTimeFormatter);
                                String paymentDate = "";
                                if (invoice.getPaymentDate() != null) {
                                    paymentDate = invoice.getPaymentDate().format(dateTimeFormatter);
                                }
                                Double amount_paid = invoice.getTotalAmount() != null ? invoice.getTotalAmount() : 0.0;
                                InvoiceRow invoiceRow = new InvoiceRow(invoice.getRef(), issueDate,
                                        paymentDate, invoice.getPaymentMethod(), invoice.getPaymentStatus(),
                                        invoice.getService(), invoice.getServiceDetails(), amount_paid, invoice);

                                invoiceData.add(invoiceRow);
                            }
                            invoiceTable.setItems(invoiceData);

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

    public void showInvoiceDetails(InvoiceRow invoiceRow){
        Invoice invoice  = invoiceRow.getInvoice();
        try {
            Utils utils = new Utils();
            utils.switchScreenWithData("/com/bmh/hotelmanagementsystem/invoice/single-invoice-log-view.fxml", primaryStage,invoice, "/com/bmh/hotelmanagementsystem/invoice/invoice-log-view.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
