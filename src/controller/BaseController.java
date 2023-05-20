package controller;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.event.ActionEvent;
import javafx.stage.Stage;


import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;

public class BaseController implements Initializable {
    @FXML
    private AnchorPane paneRight;
    @FXML
    private AnchorPane paneLeft;
    @FXML
    private JFXButton btnDashboard;
    @FXML
    private Label lblAccessLevel;
    @FXML
    private Label lblUsername;
    @FXML
    private JFXButton btnCustomers;
    @FXML
    private JFXButton btnInventoryItem;
    @FXML
    private JFXButton btnAccounts;
    @FXML
    private JFXButton btnDueUpdate;
    @FXML
    private JFXButton btnAdmin;
    @FXML
    private JFXButton btn;
    @FXML
    private Label lblClock;
    @FXML
    private JFXButton btnRentals;
    @FXML
    private JFXButton btnSells;
    private static String username = "";
    private static String accessLevel = "";
    private AnchorPane newRightPane = null;
    private JFXButton temp = null;
    private JFXButton recover = null;
    private static boolean anchorFlag = false;

    /**
     * FXML_URL sẽ được sử dụng để lưu vị trí của
     * main.resources.view files và nó sẽ được sử dụng cho navigate
     * giữa các menu
     */
    private HashMap<String, String> FXML_URL = new HashMap<>();

    /**
     * This method will resize right pane size
     * relative to it's parent whenever window is resized
     */
    private void autoResizePane() {
        newRightPane.setPrefWidth(paneRight.getWidth());
        newRightPane.setPrefHeight(paneRight.getHeight());
    }

//    @FXML
//    public void btnNavigators(ActionEvent event) {
//        bolderSelector(event);
//        JFXButton btn = (JFXButton) event.getSource();
//
//        String btnText = btn.getText();
//
//    }


    // Khởi tạo Base với DashBoard.
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadFXMLMap();
        username = LoginController.loggerUsername;
        accessLevel = LoginController.loggerAccessLevel;
        lblUsername.setText(username.toLowerCase());
        lblAccessLevel.setText(accessLevel);

        if (accessLevel.equals("Employee")) {
            btnAdmin.setDisable(true);
        }
        ctrlRightPane("/resource/view/dashboard.fxml");
    }


    // Phuong thuc dieu khien Navigator phia ben phai.


    @FXML
    private void ctrlRightPane(String URL) {
        paneRight.getChildren().clear();
        try {
            newRightPane = FXMLLoader.load(getClass().getResource(URL));

            newRightPane.setPrefHeight(paneRight.getHeight());
            newRightPane.setPrefWidth(paneRight.getWidth());

            paneRight.getChildren().add(newRightPane);

            paneRight.layoutBoundsProperty().addListener((obs, oldVal, newVal) -> {
                autoResizePane();
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadFXMLMap() {
        FXML_URL.put("dashboard", "/resource/view/dashboard.fxml");

    }



    @FXML
    public void logOut(){
        Stage current = (Stage) lblUsername.getScene().getWindow();
        current.close();

        try {
            Parent root = FXMLLoader.load(getClass().getResource("/resource/view/login.fxml"));
            root.getStylesheets().add("/resource/css/login.css");
            Scene scene = new Scene(root);
            Stage loginPrompt = new Stage();
            loginPrompt.setScene(scene);
            loginPrompt.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    //
    public void bolderSelector(ActionEvent event) {
        JFXButton btn = (JFXButton) event.getSource();
        if (temp == null) {
            temp = btn;
        } else {
            temp.setStyle("");
            temp = btn;
        }
        btn.setStyle("-fx-background-color: #455A64");
    }


    @FXML
    public void btnNavigators(ActionEvent event) {
        bolderSelector(event);
        JFXButton btn = (JFXButton) event.getSource();
        String btnText = btn.getText();
    }




}
