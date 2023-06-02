package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import main.java.others.Customer;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class CustomerController implements Initializable {
    @FXML
    private AnchorPane cusTomerPane;
    @FXML
    private AnchorPane customerPane;
    @FXML
    private JFXTextField txtFName;
    @FXML
    private JFXTextField txtLName;
    @FXML
    private JFXTextField address;
    @FXML
    private JFXTextField phone;
    @FXML
    private JFXTextField email;
    @FXML
    private Label memberSince;
    @FXML
    private JFXToggleButton btnEditMode;
    @FXML
    private JFXButton btnPrevEntry;
    @FXML
    private JFXButton btnNextEntry;
    @FXML
    private Label customerID, customerDue, lblSearchResults, lblMode;
    @FXML
    private Label lblPageIndex;
    @FXML
    private JFXTextField txtSearch;
    @FXML
    private JFXButton btnSearch;
    @FXML
    private Circle imgCustomerPhoto;
    @FXML
    private JFXRadioButton radioMale;
    @FXML
    private ToggleGroup gender;
    @FXML
    private JFXRadioButton radioFemale;
    @FXML
    private AnchorPane customerListPane;
    @FXML
    private JFXButton btnLViewAllCustomers, btnGoBack;
    @FXML
    private FontAwesomeIconView btnSeachIcon;
    @FXML
    private JFXButton btnAddNew, btnSave;
    @FXML
    private JFXButton btnPurchases;
    @FXML
    private JFXButton btnRentals, btnDelete;
    @FXML
    private FontAwesomeIconView btnAddIcon;
    @FXML
    private TableView<main.java.others.Customer> tbl;
    @FXML
    private TableColumn<main.java.others.Customer, Integer> columnID;
    @FXML
    private TableColumn<main.java.others.Customer, String> columnFirstName;
    @FXML
    private TableColumn<main.java.others.Customer, String> columnLastName;
    @FXML
    private TableColumn<main.java.others.Customer, String> columnGender;
    @FXML
    private TableColumn<main.java.others.Customer, String> columnAddress;
    @FXML
    private TableColumn<main.java.others.Customer, String> columnPhone;
    @FXML
    private TableColumn<main.java.others.Customer, String> columnEmail;

    private static int recordIndex = 0;
    private static int recordSize = 0;
    public static ObservableList<main.java.others.Customer> customersList = FXCollections.observableArrayList(); //This field will auto set from InitializerController Class
    public static ObservableList<main.java.others.Customer> tempList = FXCollections.observableArrayList(); //Will hold the main list while searching
    public static ArrayList<String> customerNames = new ArrayList<>();
    /**
     * onView field is used to hold the Customer object
     * that is currently loaded on screen
     */
    private Customer onView = null;
    private static boolean searchDone = false;
    /**
     * addFlag will differentiate b/w Adding a new entry
     * and updating an existing entry.
     * True: New Record Entry Mode
     * False: Updating an Existing Entry
     */
    private static boolean addFlag = false;
    private static String imgPath = null;

    @FXML
    public void btnEditModeToggle(ActionEvent event) {
    }

    public void btnAddMode(ActionEvent event) {
    }

    public void btnSearchAction(ActionEvent event) {
    }

    public void btnSaveAction(ActionEvent event) {
    }

    public void showPurchases(ActionEvent event) {
    }

    public void showrentals(ActionEvent event) {
    }

    public void listAllCustomers(ActionEvent event) {
    }

    public void btnDelAction(ActionEvent event) {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
