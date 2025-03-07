package com.bmh.hotelmanagementsystem;

import com.bmh.hotelmanagementsystem.BackendService.entities.Room.CheckIn;
import com.bmh.hotelmanagementsystem.BackendService.entities.Room.GuestLog;
import com.bmh.hotelmanagementsystem.BackendService.entities.Room.Room;
import com.bmh.hotelmanagementsystem.RoomManagement.CheckInInvoiceController;
import com.bmh.hotelmanagementsystem.RoomManagement.SingleRoomController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class Utils {


    @FXML
    public void setControllerPrimaryStage(FXMLLoader fxmlLoader, Stage primaryStage){
        Controller controller = fxmlLoader.getController();
        controller.setPrimaryStage(primaryStage);
    }

    @FXML
    public void switchScreen(String view, Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(view));
        Scene scene = new Scene(loader.load());
        primaryStage.setScene(scene);
        setControllerPrimaryStage(loader, primaryStage);
    }

    @FXML
    public void switchScreenWithRoom(String view, Stage primaryStage, Room room) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(view));
        Scene scene = new Scene(loader.load());

        SingleRoomController singleRoomController = loader.getController();
        singleRoomController.setRoom(room);

        primaryStage.setScene(scene);
        setControllerPrimaryStage(loader, primaryStage);
    }

    @FXML
    public void switchScreenWithData(String view, Stage primaryStage, Object data, String previousLocation) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(view));
        Scene scene = new Scene(loader.load());

        Controller controller = loader.getController();
        controller.setData(data,previousLocation);

        primaryStage.setScene(scene);
        setControllerPrimaryStage(loader, primaryStage);
    }

    @FXML
    public void switchScreenWithCheckInData(String view, Stage primaryStage, CheckIn checkInData) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(view));
        Scene scene = new Scene(loader.load());

        CheckInInvoiceController checkInInvoiceController = loader.getController();
        checkInInvoiceController.setCheckInData(checkInData);

        primaryStage.setScene(scene);
        setControllerPrimaryStage(loader, primaryStage);
    }

    @FXML
    public void switchScreenWithGuestLog(String view, Stage primaryStage, GuestLog guestLog, String previousLocation) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(view));
        Scene scene = new Scene(loader.load());

        Controller controller = loader.getController();
        controller.setGuestLog(guestLog, previousLocation);

        primaryStage.setScene(scene);
        setControllerPrimaryStage(loader, primaryStage);
    }

    public static Stage showLoadingScreen(Stage primaryStage){
        Stage loadingStage = new Stage();
        ProgressIndicator progressIndicator = new ProgressIndicator();
        StackPane loadingRoot = new StackPane();
        loadingRoot.getChildren().add(progressIndicator);
        Scene loadingScene = new Scene(loadingRoot, 200, 200);
        loadingStage.setScene(loadingScene);
        loadingStage.setTitle("Processing...");
        loadingStage.initOwner(primaryStage);
        loadingStage.initModality(Modality.APPLICATION_MODAL);
//        loadingStage.show();

        return loadingStage;
    }

    public static void showAlertDialog(Alert.AlertType alertType, String title, String content){
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void showGeneralErrorDialog(){
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setTitle("Error");
        errorAlert.setContentText("Something went wrong. Please try again.");
        errorAlert.showAndWait();
    }

    public static void downloadPDF(String invoiceRef) {
        try {
            // Fetch the PDF file from the API
            File tempFile = PDFDownloader.downloadPDF("/invoice/download?ref=" + invoiceRef);

            // Open a file chooser for the user to select where to save the file
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Invoice PDF");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            fileChooser.setInitialFileName("invoice.pdf");

            File saveFile = fileChooser.showSaveDialog(null);
            if (saveFile != null) {
                Files.copy(tempFile.toPath(), saveFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                showAlertDialog(Alert.AlertType.INFORMATION, "Download Successful", "PDF saved successfully at: " + saveFile.getAbsolutePath());
                System.out.println("PDF saved successfully at: " + saveFile.getAbsolutePath());
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlertDialog(Alert.AlertType.INFORMATION, "Download Failed", "Failed to download PDF.");
            System.out.println("Failed to download PDF.");
        }
    }


}
