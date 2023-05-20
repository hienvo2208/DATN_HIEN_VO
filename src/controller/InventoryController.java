package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import main.java.others.Item;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.TreeMap;

public class InventoryController implements Initializable {
    @FXML
    private JFXTextField txtItemName;
    @FXML
    private JFXComboBox<String> txtType;
    @FXML
    private JFXTextField txtRentRate;
    @FXML
    private Label itemID;
    @FXML
    private Circle imgCustomerPhoto;
    @FXML
    private JFXTextField txtStock;
    @FXML
    private JFXTextField txtPrice;
    @FXML
    private Label lblPageIndex, lblMode, lblSearchResults;
    @FXML
    private JFXButton btnPrevEntry;
    @FXML
    private JFXButton btnNextEntry;
    @FXML
    private JFXButton btnListAll;
    @FXML
    private JFXButton btnOutOfStock, btnGoBack, btnDelete;
    @FXML
    private JFXTextField txtSearch;
    @FXML
    private FontAwesomeIconView btnAddIcon;
    @FXML
    private JFXButton btnSearch;
    @FXML
    private AnchorPane itemPane;
    @FXML
    private AnchorPane itemListPane;
    @FXML
    private TableView<main.java.others.Item> tbl;
    @FXML
    private TableColumn<main.java.others.Item, Integer> columnItemID;
    @FXML
    private TableColumn<main.java.others.Item, String> columnItemName;
    @FXML
    private TableColumn<main.java.others.Item, String> columnItemType;
    @FXML
    private TableColumn<main.java.others.Item, Boolean> columnForRent;
    @FXML
    private TableColumn<main.java.others.Item, Boolean> columnForSale;
    @FXML
    private TableColumn<main.java.others.Item, Double> columnRentalRate;
    @FXML
    private TableColumn<main.java.others.Item, Double> columnPrice;
    @FXML
    private TableColumn<main.java.others.Item, Integer> columnStock;
    @FXML
    private JFXCheckBox chkRent, chkSale;
    @FXML
    private FontAwesomeIconView btnSearchIcon;
    private static int recordIndex = 0;
    private static int recordSize = 0;
    private main.java.others.Item onView = null;
    /**
     * addFlag will differentiate b/w Adding a new entry
     * and updating an existing entry.
     * True: New Record Entry Mode
     * False: Updating an Existing Entry
     */
    private static boolean addFlag = false;
    private static boolean searchDone = false;
    private static String imgPath = null;
    public static TreeMap<String, Integer> itemType = new TreeMap<>();
    public static ObservableList<main.java.others.Item> itemList = FXCollections.observableArrayList(); //This field will auto set from InitializerController Class
    public static ObservableList<Item> tempList = FXCollections.observableArrayList(); //Will hold the main list while searching
    public static ArrayList<String> itemNames = new ArrayList<>();
    public static ObservableList<String> itemTypeNames = FXCollections.observableArrayList();

    @FXML
    public void listAllItems(ActionEvent event) {
    }


    @FXML
    public void outOfStockList(ActionEvent event) {
    }


    @FXML
    public void btnSearchAction(ActionEvent event) {
    }

    @FXML
    public void btnAddMode(ActionEvent event) {
    }

    @FXML
    public void btnDelAction(ActionEvent event) {
    }

    @FXML
    public void btnSaveAction(ActionEvent event) {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
