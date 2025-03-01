package com.bmh.hotelmanagementsystem.employee;

import com.bmh.hotelmanagementsystem.BackendService.RestClient;
import com.bmh.hotelmanagementsystem.BackendService.entities.ApiResponse;
import com.bmh.hotelmanagementsystem.BackendService.entities.User;
import com.bmh.hotelmanagementsystem.BackendService.entities.UserFilterRequest;
import com.bmh.hotelmanagementsystem.BackendService.enums.GuestLogStatus;
import com.bmh.hotelmanagementsystem.BackendService.enums.LoginDepartment;
import com.bmh.hotelmanagementsystem.BackendService.enums.PaymentStatus;
import com.bmh.hotelmanagementsystem.BackendService.enums.UserRoles;
import com.bmh.hotelmanagementsystem.BackendService.utils.AuthFileCache;
import com.bmh.hotelmanagementsystem.Controller;
import com.bmh.hotelmanagementsystem.RoomManagement.CreateRoomController;
import com.bmh.hotelmanagementsystem.Utils;
import com.bmh.hotelmanagementsystem.dto.UserRow;
import com.bmh.hotelmanagementsystem.dto.room.GuestReservation;
import com.bmh.hotelmanagementsystem.dto.room.RoomManagement;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class EmployeeLogController extends Controller {

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
    private Label todayDate;

    @FXML
    private Pagination pagination;

    @FXML
    private TableView<UserRow> userLogTable;
    @FXML
    private TableColumn<UserRow, String> email_column;
    @FXML
    private TableColumn<UserRow, String> username_column;
//    @FXML
//    private TableColumn<UserRow, String> password_column;
    @FXML
    private TableColumn<UserRow, String> role_column;
    @FXML
    private TableColumn<UserRow, Boolean> isEnabled_column;
    @FXML
    private TableColumn<UserRow, String> access_column;
    @FXML
    private TableColumn<UserRow, String> created_by_column;
    @FXML
    private TableColumn<UserRow, String> created_date_column;
    @FXML
    private TableColumn<UserRow, String> modified_by_column;
    @FXML
    private TableColumn<UserRow, String> modified_date_column;

    @FXML
    private TableColumn<UserRow, Void> viewMore;

    @FXML
    private ComboBox<String> role_comboBox;
    @FXML
    private ComboBox<Boolean> enabled_comboBox;
    @FXML
    private ComboBox<String> access_comboBox;

    @FXML
    private Button apply;

    @FXML
    private Button create;

    private ObservableList<UserRow> userData = FXCollections.observableArrayList();


    private int pageSize = 10;
    private int page = 0;

    @FXML
    public void initialize()  {

        String username = AuthFileCache.getUsername();
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("d, MMMM, yyyy", Locale.ENGLISH);
        String formattedDate = currentDate.format(dateTimeFormatter);
        user.setText("Hello " + username);
        todayDate.setText(formattedDate);

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

        email_column.setCellValueFactory(cellData -> cellData.getValue().email_columnProperty());
        username_column.setCellValueFactory(cellData -> cellData.getValue().username_columnProperty());

       role_column.setCellValueFactory(cellData -> cellData.getValue().role_columnProperty());
       isEnabled_column.setCellValueFactory(cellData -> cellData.getValue().isEnabled_columnProperty());
       access_column.setCellValueFactory(cellData -> cellData.getValue().access_columnProperty());
       created_by_column.setCellValueFactory(cellData -> cellData.getValue().created_by_columnProperty());
       created_date_column.setCellValueFactory(cellData -> cellData.getValue().created_date_columnProperty());
       modified_by_column.setCellValueFactory(cellData -> cellData.getValue().modified_by_columnProperty());
       modified_date_column.setCellValueFactory(cellData -> cellData.getValue().modified_date_columnProperty());

        viewMore.setCellFactory(createViewMoreButtonCellFactory());

        setWrapCellFactory(created_by_column);
        setWrapCellFactory(created_date_column);
        setWrapCellFactory(modified_by_column);
        setWrapCellFactory(modified_date_column);

        apply.setOnAction(event -> apply(page,pageSize));
        create.setOnAction(event -> createUser());
        pagination.setPageFactory(this::createPage);

    }

    private void loadPage(int page, int size) {
        apply(page, size);
    }

    private Node createPage(int pageIndex) {
        loadPage(pageIndex, pageSize);
        return userLogTable;
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

    private Callback<TableColumn<UserRow, Void>, TableCell<UserRow, Void>> createViewMoreButtonCellFactory() {
        return new Callback<>() {
            @Override
            public TableCell<UserRow, Void> call(TableColumn<UserRow, Void> param) {
                final TableCell<UserRow, Void> cell = new TableCell<UserRow, Void>() {

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
                            UserRow userRow = getTableView().getItems().get(getIndex());
                            editUser(userRow);  // Call a method to handle the button click
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

    private void apply(int page, int size) {
        Stage loadingStage = Utils.showLoadingScreen(primaryStage);

        Platform.runLater(() -> {
            loadingStage.show();
        });

        new Thread(() -> {
            try {

                UserFilterRequest request = new UserFilterRequest();
                request.setRole(role_comboBox.getSelectionModel().getSelectedItem() != null? role_comboBox.getSelectionModel().getSelectedItem() : null);
                request.setEnabled(enabled_comboBox.getSelectionModel().getSelectedItem() != null? enabled_comboBox.getSelectionModel().getSelectedItem() : true);
                request.setDepartment(access_comboBox.getSelectionModel().getSelectedItem() != null? LoginDepartment.valueOf(access_comboBox.getSelectionModel().getSelectedItem()) : null);

                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());

                String jsonString = objectMapper.writeValueAsString(request);

                String response = RestClient.post("/admin/filter?page=" + page + "&size=" + size, jsonString);

                ApiResponse<User> apiResponse = objectMapper.readValue(response, new TypeReference<ApiResponse<User>>() {});

                if (apiResponse.getResponseHeader().getResponseCode().equals("00")) {
                    Platform.runLater(() -> {
                        loadingStage.close();

                        userData.clear();


                        for (User item : apiResponse.getData()){

                            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                            String createdByDate = "";
                            String modifiedByDate = "";

                            if (item.getCreatedDateTime() != null) {
                                createdByDate = item.getCreatedDateTime().format(dateTimeFormatter);
                            }
                            if (item.getLastModifiedDateTime() != null) {
                                modifiedByDate = item.getLastModifiedDateTime().format(dateTimeFormatter);
                            }

                            UserRow userRow = new UserRow(item.getEmail(), item.getUsername(), item.getRole(), item.isEnabled(),
                                    item.getDepartment().toJson(), item.getCreatedBy(), createdByDate, item.getLastModifiedBy(), modifiedByDate, item);

                            userData.add(userRow);
                        }

                        userLogTable.setItems(userData);
                        pagination.setPageCount(apiResponse.getTotalPages());
                        pagination.setCurrentPageIndex(page);

                        isEnabled_column.setCellFactory(column -> {
                            return new TableCell<UserRow, Boolean>() {
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
                    });
                }
                else{
                    loadingStage.close();
                    Platform.runLater(() -> {
                        Utils.showAlertDialog(Alert.AlertType.ERROR,apiResponse.getResponseHeader().getResponseMessage(),apiResponse.getError() );
                    });
                }

            } catch (Exception e) {
                loadingStage.close();
                Platform.runLater(() -> {
                    System.out.println(e);
                    Utils.showGeneralErrorDialog();
                });
            }

        }).start();
    }

    public void createUser(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bmh/hotelmanagementsystem/employee/create_employee.fxml"));
            Region form = loader.load();

            Stage formStage = new Stage();
            formStage.initModality(Modality.APPLICATION_MODAL);
            formStage.setTitle("Fill out the Form");

            Controller controller = loader.getController();
            controller.setPrimaryStage(formStage);

            controller.setData(null, "/com/bmh/hotelmanagementsystem/employee/employee-log-view.fxml");


            Scene formScene = new Scene(form);
            formStage.setScene(formScene);
            formStage.showAndWait();

            apply(page,pageSize);

        } catch (Exception e) {
            e.printStackTrace();
            Utils.showGeneralErrorDialog();
        }
    }

    public void editUser(UserRow userRow){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bmh/hotelmanagementsystem/employee/update_employee.fxml"));
            Region form = loader.load();

            Stage formStage = new Stage();
            formStage.initModality(Modality.APPLICATION_MODAL);
            formStage.setTitle("Fill out the Form");

            Controller controller = loader.getController();
            controller.setPrimaryStage(formStage);

            controller.setData(userRow.getUser(), "/com/bmh/hotelmanagementsystem/employee/employee-log-view.fxml");


            Scene formScene = new Scene(form);
            formStage.setScene(formScene);
            formStage.showAndWait();

            apply(page,pageSize);

        } catch (Exception e) {
            e.printStackTrace();
            Utils.showGeneralErrorDialog();
        }
    }

}
