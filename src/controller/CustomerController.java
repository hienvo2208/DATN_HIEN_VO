package controller;

import com.jfoenix.controls.*;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import entites.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import repository.DBConnection;


import javax.xml.transform.Result;
import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;
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
    private TableView<entites.Customer> tbl;
    @FXML
    private TableColumn<entites.Customer, Integer> columnID;
    @FXML
    private TableColumn<entites.Customer, String> columnFirstName;
    @FXML
    private TableColumn<entites.Customer, String> columnLastName;
    @FXML
    private TableColumn<entites.Customer, String> columnGender;
    @FXML
    private TableColumn<entites.Customer, String> columnAddress;
    @FXML
    private TableColumn<entites.Customer, String> columnPhone;
    @FXML
    private TableColumn<entites.Customer, String> columnEmail;

    private static int recordIndex = 0;
    private static int recordSize = 0;
    public static ObservableList<entites.Customer> customersList = FXCollections.observableArrayList(); //This field will auto set from InitializerController Class
    public static ObservableList<entites.Customer> tempList = FXCollections.observableArrayList(); //Will hold the main list while searching
    public static ArrayList<String> customerNames = new ArrayList<>();

    private entites.Customer onView = null;
    private static boolean searchDone = false;

    private static boolean addFlag = false;
    private static String imgPath = null;

    @FXML
    public void btnEditModeToggle(ActionEvent event) {
        if (btnEditMode.isSelected()) {
            txtFName.setEditable(true);
            txtLName.setEditable(true);
            address.setEditable(true);
            phone.setEditable(true);
            email.setEditable(true);
        } else {
            btnEditMode.setStyle("");
            txtFName.setEditable(false);
            txtLName.setEditable(false);
            address.setEditable(false);
            phone.setEditable(false);
            email.setEditable(false);
        }
    }

    private void listView() {

        columnID.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        columnLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        columnAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        columnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        columnGender.setCellValueFactory(new PropertyValueFactory<>("gender"));
        columnPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));

        tbl.setItems(customersList);

        btnGoBack.setOnAction(event -> {
            customerListPane.setVisible(false);
            customerPane.setVisible(true);
            setView();
        });
    }

    @FXML
    public void btnAddMode(ActionEvent event) {
        if(addFlag) {
            addFlag = false; //Resetting addFlag value.
            btnAddIcon.setGlyphName("PLUS");

            //Enabling other buttons
            btnPrevEntry.setDisable(false);
            btnNextEntry.setDisable(false);
            btnSearch.setDisable(false);
            btnRentals.setDisable(false);
            btnPurchases.setDisable(false);
            btnLViewAllCustomers.setDisable(false);
            btnEditMode.setSelected(false);
            btnDelete.setDisable(false);

            String defColor = "#263238";

            //Changing Focus Color
            txtFName.setUnFocusColor(Color.web(defColor));
            txtLName.setUnFocusColor(Color.web(defColor));
            address.setUnFocusColor(Color.web(defColor));
            phone.setUnFocusColor(Color.web(defColor));
            email.setUnFocusColor(Color.web(defColor));

            //Setting Label
            lblMode.setText("Navigation Mode");

            reloadRecord();

            btnEditModeToggle(new ActionEvent());

        } else {
            Connection con = repository.DBConnection.getConnection();
            try {
                PreparedStatement ps = con.prepareStatement("SELECT max(customerID) FROM customers");
                ResultSet rs = ps.executeQuery();

                while(rs.next()) {
                    customerID.setText(Integer.valueOf(rs.getInt(1) + 1).toString());
                }

                addFlag = true; //Setting flag true to enable exit mode
                btnAddIcon.setGlyphName("UNDO"); //Changing glyph

                //Setting Label
                lblMode.setText("Chế độ nhập khách hàng mới.");

                //Disabling other buttons
                btnPrevEntry.setDisable(true);
                btnNextEntry.setDisable(true);
                btnRentals.setDisable(true);
                btnPurchases.setDisable(true);
                btnLViewAllCustomers.setDisable(true);
                btnSearch.setDisable(true);
                btnDelete.setDisable(true);
                btnEditMode.setSelected(true);

                //Cleaning fields
                txtFName.setText("");
                txtLName.setText("");
                address.setText("");
                phone.setText("");
                email.setText("");
                memberSince.setText(LocalDate.now().toString());
                ImagePattern img = new ImagePattern(new Image("/resource/icon/user.png"));
                imgCustomerPhoto.setFill(img);
                imgPath = null;

                btnEditModeToggle(new ActionEvent()); //Changing mode into entry mode.. all fields will be available to edit

            } catch (SQLException e) {
                new exception.PromptDialogController("SQL Error!", "Error occured while executing Query.\nSQL Error Code: " + e.getErrorCode());
            }
        }

    }

    @FXML
    public void btnSearchAction(ActionEvent event) {
        if(searchDone){
            searchDone = false;
            lblSearchResults.setVisible(false);
            customersList = tempList;
            recordSize = customersList.size();
            btnSearch.setTooltip(new Tooltip("Tìm kiếm khách hàng bằng tên hoặc id"));
            btnSeachIcon.setGlyphName("SEARCH");
            setView();

        }else{
            ObservableList<Customer> searchResult = FXCollections.observableArrayList();

            try {
            }catch (NumberFormatException e){
               e.printStackTrace();

            }
            Integer id = Integer.valueOf(txtSearch.getText());
            searchResult = searchWithId(id);

        }

    }

    private ObservableList<Customer> searchWithId(Integer id) {
        String sql =  "SELECT *\n" +
                "FROM customers\n" +
                "WHERE customerID = ?";
        Connection connection = repository.DBConnection.getConnection();
        ObservableList<Customer> customerObservableList = FXCollections.observableArrayList();

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();
            while(rs.next()){
              Customer customer = new Customer(rs.getInt("customerID"),
                      rs.getString("fistName"),
                      rs.getString("lastName"),
                      rs.getString("address"),
                      rs.getString("phone"),
                      rs.getString("email"),
                      rs.getString("photo"),
                      rs.getString("gender"),
                      rs.getDate("memberSince"));
              customerObservableList.add(customer);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return customerObservableList;
    }

    @FXML
    public void btnSaveAction(ActionEvent event) {
        if(addFlag){
            boolean fieldsNotEmpty = checkFields();
            if(fieldsNotEmpty){
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Xác nhận");
                alert.setGraphic(new ImageView(this.getClass().getResource("/resource/icon/question (2).png").toString()));
                alert.setHeaderText("Bạn muốn thêm vào không ?");
                alert.setContentText("Nhấn Ok để xác nhận, nhấn cancel để hủy");

                Optional<ButtonType> result = alert.showAndWait();
                if(result.get()==ButtonType.OK){
                    addRecordToDB();
                }
            }else{
                JFXSnackbar snackbar = new JFXSnackbar(customerPane);
                snackbar.show("Trường thông tin bị thiếu",3000);
            }

        }else {
            boolean fieldsNotEmpty = checkFields();
            if(fieldsNotEmpty){
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Xác nhận edit");
                alert.setGraphic(new ImageView(this.getClass().getResource("/resource/icon/question (2).png").toString()));

                alert.setHeaderText("Bạn có muốn cập nhật không");
                alert.setContentText("Nhấn Ok để xác nhận, nhấn cancel để hủy");

                Optional<ButtonType> result = alert.showAndWait();

                if (result.get() == ButtonType.OK) {
                    updateRecord();
                }

            }

        }

    }

    private void updateRecord() {
        Connection con = repository.DBConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("UPDATE customers SET customerID = ?, firstName = ?, lastName = ?, address = ?," +
                    "phone = ?, email = ?, photo = ?, gender = ?, memberSince = ? WHERE customerID =" + Integer.valueOf(customerID.getText()));


            //Setting fields
            ps.setInt(1, Integer.valueOf(customerID.getText()));
            ps.setString(2, txtFName.getText());
            ps.setString(3, txtLName.getText());
            ps.setString(4, address.getText());
            ps.setString(5, phone.getText());
            ps.setString(6, email.getText());
            //ps.setString(7, "null");
            ps.setString(7, imgPath);
            if (radioMale.isSelected()) {
                ps.setString(8, "Male");
            } else if (radioFemale.isSelected()) {
                ps.setString(8, "Female");
            }
            ps.setDate(9, Date.valueOf(LocalDate.now()));

            ps.executeUpdate();
            new exception.PromptDialogController("Operation Successful!", "New Customer Added!");
            reloadRecord();
            con.close();


        } catch (SQLException e) {
            e.printStackTrace();
            new exception.PromptDialogController("SQL Error!", "Error occured while executing Query.\nSQL Error Code: " + e.getErrorCode());
        }
    }

    private void addRecordToDB() {
        Connection con = repository.DBConnection.getConnection();

        try {
            PreparedStatement ps = con.prepareStatement("INSERT INTO customers VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)");
            ps.setInt(1, Integer.valueOf(customerID.getText()));
            ps.setString(2, txtFName.getText());
            ps.setString(3, txtLName.getText());
            ps.setString(4, address.getText());
            ps.setString(5, phone.getText());
            ps.setString(6, email.getText());
            ps.setString(7, imgPath);
            if(radioMale.isSelected()) {
                ps.setString(8, "Male");
            } else if(radioFemale.isSelected()) {
                ps.setString(8, "Female");
            }

            ps.setDate(9, Date.valueOf(LocalDate.now()));

            ps.executeUpdate();
            new exception.PromptDialogController("Operation Successful!", "New Customer Added!");
            reloadRecord();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
            new exception.PromptDialogController("SQL Error!", "Error occured while executing Query.\nSQL Error Code: " + e.getErrorCode());
        }

    }

    private boolean checkFields() {
        boolean entryFlag = true;
        if (txtFName.getText().equals("")) {
            txtFName.setUnFocusColor(Color.web("red"));
            entryFlag = false;
        }

        if(txtLName.getText().equals("")) {
            txtLName.setUnFocusColor(Color.web("red"));
            entryFlag = false;
        }

        if(address.getText().equals("")) {
            address.setUnFocusColor(Color.web("red"));
            entryFlag = false;
        }

        if(phone.getText().equals("")) {
            phone.setUnFocusColor(Color.web("red"));
            entryFlag = false;
        }

        if(email.getText().equals("")) {
            email.setUnFocusColor(Color.web("red"));
            entryFlag = false;;
        }

        return entryFlag;
    }




    @FXML
    public void showPurchases(ActionEvent event) {
        CustomerPurchaseListController.customerID = Integer.valueOf(customerID.getText());
        try {
            Parent purchase = FXMLLoader.load(this.getClass().getResource("/resource/view/customerpurchase.fxml"));
            Scene scene = new Scene(purchase);
            Stage stage = new Stage();
            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @FXML
    public void showrentals(ActionEvent event) {
        CustomersRentalListController.customerID = Integer.valueOf(customerID.getText());
        try {
            Parent rental = FXMLLoader.load(this.getClass().getResource("/resource/view/customerrental.fxml"));
            Scene s = new Scene(rental);
            Stage stage = new Stage();
            stage.setResizable(false);
            stage.setScene(s);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void listAllCustomers(ActionEvent event) {
        customerPane.setVisible(false);
        customerListPane.setVisible(true);
        listView();
    }

    @FXML
    public void btnDelAction(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận xóa");
        alert.setGraphic(new ImageView(this.getClass().getResource("/resource/icon/x-button.png").toString()));

        alert.setHeaderText("Bạn có muốn xóa khách hàng này không ?");
        alert.setContentText("Nhấn Ok để xóa, Nhấn cancel để quay lại");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == ButtonType.OK) {
            String sql = "DELETE \n" +
                    "FROM customers \n" +
                    "WHERE customerID = ?";
            Connection connection = DBConnection.getConnection();
            try {
                PreparedStatement pr = connection.prepareStatement(sql);
                pr.setInt(1, Integer.valueOf(customerID.getText()));
                pr.executeUpdate();
                reloadRecord();
                new main.java.controllers.PromptDialogController("Thành công.", "Xóa thành công khách hàng.");

            } catch (SQLException e) {
                if (e.getErrorCode() == 1451) {
                    new main.java.controllers.PromptDialogController("Constraint Error", "Customer has accounts & may be transactions. You need to delete them first in Admin Panel if you want to delete this entry");
                }
            }
        }

    }

    private void reloadRecord() {
        ObservableList<Customer> customersListTemp = FXCollections.observableArrayList();

        String sql = "SELECT *" +
                "FROM customers";
        Connection connection = DBConnection.getConnection();
        try {
            PreparedStatement pr = connection.prepareStatement(sql);
            ResultSet customerResultSet = pr.executeQuery();
            while (customerResultSet.next()) {
                customersListTemp.add(new Customer(
                        customerResultSet.getInt("customerID"),
                        customerResultSet.getString("firstName"),
                        customerResultSet.getString("lastName"),
                        customerResultSet.getString("address"),
                        customerResultSet.getString("phone"),
                        customerResultSet.getString("email"),
                        customerResultSet.getString("photo"),
                        customerResultSet.getString("gender"),
                        customerResultSet.getDate("memberSince")));
            }
            customersList = customersListTemp;
            setView();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (LoginController.loggerAccessLevel.equals("Admin")) {
            btnDelete.setDisable(false);
        }
        setView();

    }

    private void setView() {
        lblMode.setText("Khách hàng");
        customerListPane.setVisible(false);


        recordIndex = 0;
        recordSize = customersList.size();

        Tooltip tooltip = new Tooltip("Click chuột hai lần để thay đổi Avatar");
        Tooltip.install(imgCustomerPhoto, tooltip);

        imgCustomerPhoto.setOnMouseClicked(e1 -> {
            if (e1.getClickCount() == 2) {
                FileChooser fc = new FileChooser();
                fc.setTitle("Chọn ảnh");

                File imgFile = (File) fc.showOpenMultipleDialog(imgCustomerPhoto.getScene().getWindow());

                if (imgFile != null) {
                    imgPath = imgFile.toURI().getPath();
                    if (imgPath.contains(".jpg") || imgPath.contains(".png") || imgPath.contains(".jpeg")) {
                        ImagePattern gg = new ImagePattern(new Image(imgPath));
                    } else {
                        new PromptDialogController("File format Error !", "Please select a valid image file. You can select JPG, PNG, JPEG");
                    }
                }
            }
        });

        btnNextEntry.setOnAction(e2 -> {
            onView = customersList.get(++recordIndex);
            recordNavigator();
            lblPageIndex.setText(recordIndex + 1 + " của " + recordSize);
            if (recordIndex == recordSize - 1) {
                btnNextEntry.setDisable(true);
                btnPrevEntry.setDisable(false);
            }
            btnPrevEntry.setDisable(false);
        });


        btnPrevEntry.setOnAction(e3 -> {
            onView = customersList.get(--recordIndex);
            recordNavigator();
            lblPageIndex.setText(recordIndex + 1 + " của " + recordSize);
            if (recordIndex == 0) {
                btnPrevEntry.setDisable(true);
            }
            btnNextEntry.setDisable(false);
        });

        if (recordSize > 0) {
            onView = customersList.get(recordIndex);
            lblPageIndex.setText(recordIndex + 1 + " của " + recordSize);
            recordNavigator();
            if (recordSize > 1) {
                btnNextEntry.setDisable(false);
            }
            btnPrevEntry.setDisable(true);
        }


    }

    private void recordNavigator() {

        customerDue.setText(String.valueOf(setDue(onView.getId())));
        customerID.setText(String.valueOf(onView.getId()));
        txtFName.setText(onView.getFirstName());
        txtLName.setText(onView.getLastName());
        address.setText(onView.getAddress());
        phone.setText(onView.getPhone());
        memberSince.setText(onView.getDate().toString());
        email.setText(onView.getEmail());
        if (onView.getGender().contains("Male")) {
            radioMale.setSelected(true);
        }
        if (onView.getGender().contains("Female")) {
            radioFemale.setSelected(true);
        }

        //Load Anh
        if (onView.getPhoto() == null) {
            ImagePattern img = new ImagePattern(new Image("/resource/icon/user.png"));
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
                    ImagePattern img = new ImagePattern(new Image("/resource/icon/user.png"));
                    imgCustomerPhoto.setFill(img);
                }
            } catch (Exception e) {
                ImagePattern img = new ImagePattern(new Image("/resource/icon/user.png"));
                imgCustomerPhoto.setFill(img);
            }
        }

    }


    public double setDue(int id) {
        Connection c = DBConnection.getConnection();
        String sqlPurchase = "SELECT SUM(amountDue) FROM purchases WHERE Customers_customerID = ?";
        double totalDue = 0.0;

        try {
            PreparedStatement sp = c.prepareStatement(sqlPurchase);
            sp.setInt(1, id);
            ResultSet r1 = sp.executeQuery();

            while (r1.next()) {
                totalDue += r1.getDouble(1);
            }

            String sqlRentals = "SELECT SUM(amountDue)\n" +
                    "FROM rentals\n" +
                    "WHERE Customers_customerID = ?";
            PreparedStatement sr = c.prepareStatement(sqlRentals);
            sr.setInt(1, id);
            ResultSet r2 = sp.executeQuery();

            while (r2.next()) {
                totalDue += r2.getDouble(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return totalDue;
    }
}
