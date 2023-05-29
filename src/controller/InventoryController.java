package controller;

import com.jfoenix.controls.*;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import entites.DBConnection;
import entites.Item;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import org.controlsfx.control.textfield.TextFields;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.TreeMap;

import static com.sun.xml.internal.ws.policy.sourcemodel.wspolicy.XmlToken.Optional;

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
    private TableView<entites.Item> tbl;
    @FXML
    private TableColumn<entites.Item, Integer> columnItemID;
    @FXML
    private TableColumn<entites.Item, String> columnItemName;
    @FXML
    private TableColumn<entites.Item, String> columnItemType;
    @FXML
    private TableColumn<entites.Item, Boolean> columnForRent;
    @FXML
    private TableColumn<entites.Item, Boolean> columnForSale;
    @FXML
    private TableColumn<entites.Item, Double> columnRentalRate;
    @FXML
    private TableColumn<entites.Item, Double> columnPrice;
    @FXML
    private TableColumn<entites.Item, Integer> columnStock;
    @FXML
    private JFXCheckBox chkRent, chkSale;
    @FXML
    private FontAwesomeIconView btnSearchIcon;
    private static int recordIndex = 0;
    private static int recordSize = 0;
    private Item onView = null;
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
    public static ObservableList<entites.Item> itemList = FXCollections.observableArrayList(); //This field will auto set from InitializerController Class
    public static ObservableList<entites.Item> tempList = FXCollections.observableArrayList(); //Will hold the main list while searching
    public static ArrayList<String> itemNames = new ArrayList<>();
    public static ObservableList<String> itemTypeNames = FXCollections.observableArrayList();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (LoginController.loggerAccessLevel.equals("Admin")) {
            btnDelete.setDisable(false);
        }
        txtType.setItems(itemTypeNames);
        TextFields.bindAutoCompletion(txtSearch, itemNames);
        setView();


    }


    private void setView() {
        lblMode.setText("Chế độ xem ITEMS");
        itemListPane.setVisible(false);
        recordIndex = 0; //Resetting index value
        recordSize = itemList.size();

        //Tooltip sẽ được kích hoạt trên ảnh của khách hàng nếu như di chuột đến.
        Tooltip tooltip = new Tooltip("Click chuột hai lần để thay đổi Avatar");
        Tooltip.install(imgCustomerPhoto, tooltip);

        imgCustomerPhoto.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                FileChooser fc = new FileChooser();
                fc.setTitle("Choose photos");

                File imgFile = (File) fc.showOpenMultipleDialog(imgCustomerPhoto.getScene().getWindow());

                if (imgFile != null) {
                    imgPath = imgFile.toURI().getPath();
                    if (imgPath.contains(".jpg") || imgPath.contains(".png") || imgPath.contains(".jpeg")) {
                        ImagePattern gg = new ImagePattern(new Image(imgPath));
                        imgCustomerPhoto.setFill(gg);
                    } else {
                        new PromptDialogController("File format Error !", "Please select a valid image file. You can select JPG, JPEG, PNG, GIF");
                    }
                }
            }
        });

        btnNextEntry.setOnAction(event -> {
            onView = itemList.get(++recordIndex);
            recordNavigator();
            lblPageIndex.setText("Trang " + (recordIndex + 1) + " của " + recordSize + ".");
            if (recordIndex + 1 == recordSize) {
                btnNextEntry.setDisable(true);
            }
            btnPrevEntry.setDisable(false);
        });


        btnPrevEntry.setOnAction(event -> {
            onView = itemList.get(--recordIndex);
            recordNavigator();
            lblPageIndex.setText("Trang " + (recordIndex + 1) + " của " + recordSize + ".");
            if (recordIndex == 0) {
                btnPrevEntry.setDisable(true);
            }
            btnNextEntry.setDisable(false);

        });
        btnNextEntry.setDisable(false);

        if (recordSize > 0) {
            onView = itemList.get(recordIndex); // Gia tri hien tai cua ban ghi
            recordNavigator();

            lblPageIndex.setText("Trang " + (recordIndex + 1) + "của" + recordSize + ".");

            if (recordSize > 1) {
                btnNextEntry.setDisable(false);
            }

        }
        btnPrevEntry.setDisable(true);

    }

    private void recordNavigator() {
        txtStock.setStyle("-fx-text-fill: #263238");

        chkRent.setSelected(false);
        chkSale.setSelected(false);
        txtRentRate.setText("0.0");
        txtPrice.setText("0.0");

        itemID.setText(Integer.toString(onView.getId()));
        txtItemName.setText(onView.getName());
        txtType.setValue(onView.getItemType());
        if (onView.isRent()) {
            chkRent.setSelected(true);
            txtRentRate.setText(Double.toString(onView.getRentRate()));
        }
        if (onView.isSale()) {
            chkSale.setSelected(true);
            txtPrice.setText(Double.toString(onView.getSalePrice()));
        }
        txtStock.setText(Integer.toString(onView.getStock()));

        // Set mau do neu stock < 5
        if (onView.getStock() <= 5) {
            txtStock.setStyle("-fx-text-fill: red");
        }

        //Load anh
        if (onView.getPhoto() == null) {
            ImagePattern img = new ImagePattern(new Image("/resource/icon/trolley.png"));
            imgCustomerPhoto.setFill(img);
        } else {
            try {
                imgPath = onView.getPhoto();

                File tmpPath = new File(imgPath.replace("file:", ""));

                if (tmpPath.exists()) {
                    ImagePattern img = new ImagePattern(new Image(imgPath));
                    imgCustomerPhoto.setFill(img);
                } else {
                    imgPath = null;
                    ImagePattern img = new ImagePattern(new Image("/resource/icon/trolley.png"));
                    imgCustomerPhoto.setFill(img);
                }

            } catch (Exception e) {
                //Fallback photo in case image not found
                ImagePattern img = new ImagePattern(new Image("/resource/icon/trolley.png"));
                imgCustomerPhoto.setFill(img);
            }
        }

    }

    @FXML
    public void listAllItems(ActionEvent event) {
        btnGoBack.setOnAction(e -> {
            itemListPane.setVisible(false);
            itemPane.setVisible(true);
        });
        tbl.setItems(itemList);
        listView();
    }

    private void listView() {
        itemPane.setVisible(false);
        itemListPane.setVisible(true);

        columnItemID.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnItemName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        columnItemType.setCellValueFactory(new PropertyValueFactory<>("itemType"));
        columnForRent.setCellValueFactory(new PropertyValueFactory<>("rent"));
        columnForSale.setCellValueFactory(new PropertyValueFactory<>("sale"));
        columnRentalRate.setCellValueFactory(new PropertyValueFactory<>("rentRate"));
        columnPrice.setCellValueFactory(new PropertyValueFactory<>("salePrice"));

    }


    @FXML
    public void outOfStockList(ActionEvent event) {
        btnGoBack.setOnAction(e -> {
            itemListPane.setVisible(false);
            itemPane.setVisible(true);
        });

        Connection connection = DBConnection.getConnection();
        ObservableList<Item> outStock = FXCollections.observableArrayList();
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM item INNER JOIN itemtype ON item.ItemType_itemTypeId = itemtype.itemTypeId WHERE stock <= 5");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Item item = new Item(rs.getInt("itemId"),
                        rs.getString("itemName"),
                        rs.getInt("stock"),
                        false,
                        false,
                        rs.getDouble("salePrice"),
                        rs.getDouble("rentRate"),
                        rs.getString("photo"),
                        rs.getString("typeName"));
                if (rs.getString("rentalOrSale").contains("Rental")) {
                    item.setRent(true);
                } else if (rs.getString("rentalOrSale").contains("Sale")) {
                    item.setSale(true);
                }
                outStock.add(item);
            }

            tbl.setItems(outStock);
            listView();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    @FXML
    public void btnSearchAction(ActionEvent event) {

    }

    private void addRecodeToDatabase() {
        Connection connection = repository.DBConnection.getConnection();
        try {
            PreparedStatement rs = connection.prepareStatement("INSERT INTO item VALUES(?, ?, ?, ?, ?, ?, ?, ?)");
            rs.setInt(1, Integer.valueOf(itemID.getText()));
            rs.setString(2, txtItemName.getText());
            rs.setInt(3, Integer.valueOf(txtStock.getText()));

            if (chkSale.isSelected() && chkRent.isSelected()) {
                rs.setString(4, "Rental,Sale");
            } else if (chkSale.isSelected()) {
                rs.setString(4, "Sale");
            } else if (chkRent.isSelected()) {
                rs.setString(4, "Rental");
            }

            double salePrice = 0.0;
            double rentRate = 0.0;

            if (!txtPrice.getText().equals("")) {
                salePrice = Double.valueOf(txtPrice.getText());
            }

            if (!txtRentRate.getText().equals("")) {
                rentRate = Double.valueOf(txtRentRate.getText());
            }
            rs.setDouble(5, salePrice);
            rs.setDouble(6, rentRate);

            rs.setString(7, imgPath);
            rs.setInt(8, itemType.get(txtType.getValue()));

            rs.executeUpdate();


            new exception.PromptDialogController("Thành công", "Item được thêm vào thành công");
            reloadRecord();
        } catch (SQLException e) {
            new exception.PromptDialogController("SQL Error!", "Error occured while executing Query.\nSQL Error Code: " + e.getErrorCode());
        }


    }

    private boolean checkFields() {
        boolean entryFlag = true;
        if (txtItemName.getText().equals("")) {
            txtItemName.setUnFocusColor(Color.web("red"));
            entryFlag = false;
        }

        if (chkSale.isSelected() && txtPrice.getText().equals("")) {
            txtPrice.setUnFocusColor(Color.web("red"));
            entryFlag = false;
        }

        if (!chkRent.isSelected() && !chkSale.isSelected()) {
            entryFlag = false;
        }

        if (chkRent.isSelected() && txtRentRate.getText().equals("")) {
            txtRentRate.setUnFocusColor(Color.web("red"));
            entryFlag = false;
        }

        if (txtType.getValue().equals("")) {
            txtType.setUnFocusColor(Color.web("red"));
            entryFlag = false;
            ;
        }

        if (txtStock.getText().equals("")) {
            txtStock.setUnFocusColor(Color.web("red"));
            entryFlag = false;
            ;
        }

        return entryFlag;
    }

    @FXML
    public void btnAddMode(ActionEvent event) {
        if (addFlag) {
            // reset addFlag

            addFlag = false;
            btnAddIcon.setGlyphName("PLUS");

            //enable các nút của itempane
            btnPrevEntry.setDisable(false);
            btnNextEntry.setDisable(false);
            btnOutOfStock.setDisable(false);
            btnListAll.setDisable(false);
            btnDelete.setDisable(false);
            btnSearch.setDisable(false);


            String defColor = "#263238";

            //Changing Focus Color
            txtItemName.setUnFocusColor(Color.web(defColor));
            txtPrice.setUnFocusColor(Color.web(defColor));
            txtRentRate.setUnFocusColor(Color.web(defColor));
            txtType.setUnFocusColor(Color.web(defColor));
            txtSearch.setUnFocusColor(Color.web(defColor));
            txtStock.setUnFocusColor(Color.web(defColor));

            // Hiển thị chế độ.
            lblMode.setText("Item");
            reloadRecord();


        } else {
            Connection connection = repository.DBConnection.getConnection();
            try {
                PreparedStatement pr = connection.prepareStatement("SELECT MAX(itemID) FROM item ");
                ResultSet rs = pr.executeQuery();
                while (rs.next()) {
                    itemID.setText(String.valueOf(Integer.valueOf(rs.getInt(1) + 1)));
                }
                addFlag = true;
                btnAddIcon.setGlyphName("UNDO");

                // Hiển thị chế độ.
                lblMode.setText("Thêm item mới");

                ImagePattern imgae = new ImagePattern(new Image("/main/resources/icons/trolley.png"));
                imgCustomerPhoto.setFill(imgae);

                // Vô hiệu hóa những nút bấm của ListPane.
                btnPrevEntry.setDisable(true);
                btnNextEntry.setDisable(true);
                btnOutOfStock.setDisable(true);
                btnListAll.setDisable(true);
                btnDelete.setDisable(true);
                btnSearch.setDisable(true);

                //Xóa các trường trong Item Pane
                txtItemName.setText("");
                txtType.setValue("");
                txtRentRate.setText("");
                txtPrice.setText("");
                imgPath = null;
                txtStock.setText("");

            } catch (SQLException e) {
                new PromptDialogController("SQL Error!", "Error occured while executing Query.\nSQL Error Code: " + e.getErrorCode());
            }
        }
    }

    private void reloadRecord() {
        itemList.clear();
        Connection connection = repository.DBConnection.getConnection();
        try {
            PreparedStatement pr = connection.prepareStatement("SELECT * FROM item INNER JOIN itemtype ON ItemType_itemTypeId = itemtype.itemTypeId GROUP BY itemID");
            ResultSet rs = pr.executeQuery();

            while (rs.next()) {
                Item item = new Item(rs.getInt("itemId"),
                        rs.getString("itemName"),
                        rs.getInt("stock"),
                        false,
                        false,
                        rs.getDouble("salePrice"),
                        rs.getDouble("rentRate"),
                        rs.getString("photo"),
                        rs.getString("typeName"));
                if (rs.getString("rentalOrSale").contains("Rental")) {
                    item.setRent(true);
                } else if (rs.getString("rentalOrSale").contains("Sale")) {
                    item.setSale(true);
                }
                itemList.add(item);
            }

            setView();

        } catch (SQLException e) {
            new PromptDialogController("SQL Error!", "Error occured while executing Query.\nSQL Error Code: " + e.getErrorCode());
        }
    }

    @FXML
    public void btnDelAction(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận xóa entry");
        alert.setGraphic(new ImageView(this.getClass().getResource("/resource/icon/question (2).png").toString()));

        alert.setHeaderText("Bạn xóa item này vào không ?");
        alert.setContentText("Nhấn OK để xác nhận, Cancel để hủy");

        java.util.Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == ButtonType.OK) {
            Connection connection = repository.DBConnection.getConnection();
            try {
                PreparedStatement pr = connection.prepareStatement("DELETE FROM item WHERE itemID =" + Integer.valueOf(itemID.getText()));
                pr.executeUpdate();
                new PromptDialogController("Thành công", "Bạn đã xóa thành công item");
                reloadRecord();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

    }

    @FXML
    public void btnSaveAction(ActionEvent event) {
        if (addFlag) {
            boolean filedNotEmpty = checkFields();
            if (filedNotEmpty) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Xác nhận Empty");
                alert.setGraphic(new ImageView(this.getClass().getResource("/resource/icon/question (2).png").toString()));

                alert.setHeaderText("Bạn muộn thêm item này vào không ?");
                alert.setContentText("Nhấn OK để xác nhận, Cancel để hủy");

                java.util.Optional<ButtonType> resultl = alert.showAndWait();

                if (resultl.get() == ButtonType.OK) {
                    addRecodeToDatabase();
                }


            } else {
                JFXSnackbar snackbar = new JFXSnackbar(itemPane);
                snackbar.show("Điền đầy đủ thông tin", 3000);
            }
        } else {
            boolean fieldNotEmpty = checkFields();
            if (fieldNotEmpty) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Xác nhận Edit");
                alert.setGraphic(new ImageView(this.getClass().getResource("/resource/icon/question (2).png").toString()));

                alert.setHeaderText("Bạn muốn cập nhật item này không ?");
                alert.setContentText("Nhấn OK để xác nhận, Cancel để hủy");

                java.util.Optional<ButtonType> resultl = alert.showAndWait();
                if (resultl.get() == ButtonType.OK) {
                    updateRecord();
                }

            }
        }

    }

    private void updateRecord() {
        Connection connection = repository.DBConnection.getConnection();
        try {
            PreparedStatement rs = connection.prepareStatement("UPDATE item SET itemID = ?, itemName = ?, stock = ?, rentalOrSale = ?," +
                    "salePrice = ?, rentRate = ?, photo = ?, ItemType_itemTypeId =  ?  WHERE  ItemID =" + Integer.valueOf(itemID.getText()));
            rs.setInt(1, Integer.valueOf(itemID.getText()));
            rs.setInt(1, Integer.valueOf(itemID.getText()));
            rs.setString(2, txtItemName.getText());
            rs.setInt(3, Integer.valueOf(txtStock.getText()));

            if (chkSale.isSelected() && chkRent.isSelected()) {
                rs.setString(4, "Rental,Sale");
            } else if (chkSale.isSelected()) {
                rs.setString(4, "Sale");
            } else if (chkRent.isSelected()) {
                rs.setString(4, "Rental");
            }

            double salePrice = 0.0;
            double rentRate = 0.0;

            if (!txtPrice.getText().equals("")) {
                salePrice = Double.valueOf(txtPrice.getText());
            }

            if (!txtRentRate.getText().equals("")) {
                rentRate = Double.valueOf(txtRentRate.getText());
            }
            rs.setDouble(5, salePrice);
            rs.setDouble(6, rentRate);

            rs.setString(7, imgPath);
            rs.setInt(8, itemType.get(txtType.getValue()));

            rs.executeUpdate();

            new PromptDialogController("Thành công", "Đã cập nhật item");
            reloadRecord();


        } catch (SQLException e) {
            new exception.PromptDialogController("SQL Error!", "Error occured while executing Query.\nSQL Error Code: " + e.getErrorCode());
        }

    }


}
