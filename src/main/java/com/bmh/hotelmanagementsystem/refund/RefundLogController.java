package com.bmh.hotelmanagementsystem.refund;

import com.bmh.hotelmanagementsystem.BackendService.RestClient;
import com.bmh.hotelmanagementsystem.BackendService.entities.Invoice.InvoiceSummary;
import com.bmh.hotelmanagementsystem.BackendService.entities.refund.Refund;
import com.bmh.hotelmanagementsystem.BackendService.entities.refund.RefundLogFilterRequest;
import com.bmh.hotelmanagementsystem.dto.invoice.InvoiceRow;
import com.bmh.hotelmanagementsystem.BackendService.entities.*;
import com.bmh.hotelmanagementsystem.BackendService.entities.Invoice.Invoice;
import com.bmh.hotelmanagementsystem.BackendService.entities.Invoice.InvoiceLogFilterRequest;
import com.bmh.hotelmanagementsystem.BackendService.enums.*;
import com.bmh.hotelmanagementsystem.Controller;
import com.bmh.hotelmanagementsystem.Utils;
import com.bmh.hotelmanagementsystem.dto.refund.RefundRow;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static com.bmh.hotelmanagementsystem.Utils.showLoadingScreen;

public class RefundLogController extends Controller {

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
    private TextField searchField;
    @FXML
    private Button search;
    @FXML
    private Pagination pagination;

    @FXML
    private TableView<RefundRow> refundTable;
    @FXML
    private TableColumn<RefundRow, String> ref_column;
    @FXML
    private TableColumn<RefundRow, String> amount_column;
    @FXML
    private TableColumn<RefundRow, String> date_column;
    @FXML
    private TableColumn<RefundRow, String> reason_column;
    @FXML
    private TableColumn<RefundRow, String> invoice_ref_column;
    @FXML
    private TableColumn<RefundRow, Void> viewMore;

    @FXML
    private DatePicker start_datePicker;
    @FXML
    private DatePicker end_datePicker;
    @FXML
    private Button apply;
    @FXML
    private Button create;

    private ObservableList<RefundRow> refundData = FXCollections.observableArrayList();

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

        ref_column.setCellValueFactory(cellData -> cellData.getValue().ref_columnProperty());
        amount_column.setCellValueFactory(cellData -> cellData.getValue().amount_columnProperty());
        date_column.setCellValueFactory(cellData -> cellData.getValue().date_columnProperty());
        reason_column.setCellValueFactory(cellData -> cellData.getValue().reason_columnProperty());
        invoice_ref_column.setCellValueFactory(cellData -> cellData.getValue().invoice_ref_columnProperty());

        viewMore.setCellFactory(createViewMoreButtonCellFactory());

        setWrapCellFactory(reason_column);

//        invoice_ref_column.setCellFactory(TextFieldTableCell.forTableColumn());

        pagination.setPageFactory(this::createPage);


        apply.setOnAction(event -> apply(page, pageSize));
        search.setOnAction(event -> apply(page, pageSize));
        searchField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                apply(page, pageSize);
            }
        });
        create.setOnAction(event -> create());
    }

    private void loadPage(int page, int size) {
        apply(page, size);
    }

    private Node createPage(int pageIndex) {
        loadPage(pageIndex, pageSize);
        return refundTable;
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

    private Callback<TableColumn<RefundRow, Void>, TableCell<RefundRow, Void>> createViewMoreButtonCellFactory() {
        return new Callback<>() {
            @Override
            public TableCell<RefundRow, Void> call(TableColumn<RefundRow, Void> param) {
                final TableCell<RefundRow, Void> cell = new TableCell<RefundRow, Void>() {

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
                            RefundRow refundRow = getTableView().getItems().get(getIndex());
                            showInvoiceDetails(refundRow);  // Call a method to handle the button click
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
                RefundLogFilterRequest request = new RefundLogFilterRequest();
                request.setQuery(searchField.getText());
                request.setStartDate(start_datePicker.getValue() != null ? start_datePicker.getValue().atStartOfDay() : null);
                request.setEndDate(end_datePicker.getValue() != null ? end_datePicker.getValue().atTime(23, 59, 0) : null);


                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());

                String jsonString = objectMapper.writeValueAsString(request);

                String response = RestClient.post("/refund/filter?page=" + page + "&size=" + size, jsonString);
                ApiResponse<Refund> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponse<Refund>>() {});


                Platform.runLater(() -> {
                    try {
                        if (apiResponse.getResponseHeader().getResponseCode().equals("00")) {
                            // Close the loading screen
                            loadingStage.close();

                            refundData.clear();
                            for (Refund refund : apiResponse.getData()){

                                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                                String date = "";
                                if (refund.getDate() != null) {
                                    date = refund.getDate().format(dateTimeFormatter);
                                }
                                Double amount_paid = refund.getAmount() != null ? refund.getAmount() : 0.0;
                                String amount = "₦" + priceFormatter.format(amount_paid);
                                RefundRow refundRow = new RefundRow(refund.getRef(), amount, date, refund.getReason(),
                                        refund.getInvoice().getRef(), refund);

                                refundData.add(refundRow);
                            }
                            refundTable.setItems(refundData);
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

    public void showInvoiceDetails(RefundRow refundRow){
        Invoice invoice  = refundRow.getRefund().getInvoice();
        try {
            Utils utils = new Utils();
            utils.switchScreenWithData("/com/bmh/hotelmanagementsystem/invoice/single-invoice-log-view.fxml", primaryStage,invoice, "/com/bmh/hotelmanagementsystem/refund/refund-log-view.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void create(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bmh/hotelmanagementsystem/refund/create_refund.fxml"));
            Region form = loader.load();

            Stage formStage = new Stage();
            formStage.initModality(Modality.APPLICATION_MODAL);
            formStage.setTitle("Fill out the Form");

            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            formStage.setX(screenBounds.getMinX());
            formStage.setY(screenBounds.getMinY());
            formStage.setWidth(screenBounds.getWidth());
            formStage.setHeight(screenBounds.getHeight());

            Controller controller = loader.getController();
            controller.setPrimaryStage(formStage);
            controller.setData(null, "/com/bmh/hotelmanagementsystem/refund/refund-log-view.fxml");

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
