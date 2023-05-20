package controller;

import com.jfoenix.controls.JFXProgressBar;
import entites.Customer;
import entites.Item;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import repository.DBConnection;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.TreeMap;

public class InitializerController implements Initializable {

    private static final int THREAD_SLEEP_INTERVAL = 50;

    @FXML
    private JFXProgressBar progressIndicator;

    @FXML
    private Label taskName;

    public String sessionUser = LoginController.loggerUsername;
    // Trường này sẽ lưu tên người dùng trong hệ thống.
    //  Nó bắt nguồn từ class LoginCotroller


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LoadRecords initializerTask = new LoadRecords();
        progressIndicator.progressProperty().unbind();
        taskName.textProperty().unbind();
        taskName.textProperty().bind(initializerTask.messageProperty());

        new Thread(initializerTask).start();

        //Loading Main Application upon initializer task's succession
        initializerTask.setOnScheduled(e->{
           // closing current stage
            Stage currentStage = (Stage) taskName.getScene().getWindow();
            currentStage.close();
            loadApplication();
        });


    }

    // Khởi tạo base
    private void loadApplication(){
        //Tạo một cái new stage cho Application (tạo base)
        Parent root = null;
        Stage base = new Stage();

        try {
            root = FXMLLoader.load(getClass().getResource("/resource/view/base.fxml"));
            Scene scene = new Scene(root);
            String css = this.getClass().getResource("/resource/css/base.css").toExternalForm();
            scene.getStylesheets().add(css);
            base.setTitle("Hệ thống quản lý hàng tồn kho");
            base.getIcons().add(new Image("/resource/icon/Logo.png"));
            base.setScene(scene);
            base.setMaximized(true);
            base.show();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    class LoadRecords extends Task {

        @Override
        protected Object call() throws Exception {

            // //Creating OLs to save values from result set

            Connection connection = DBConnection.getConnection();
            ObservableList<Customer> customersList = FXCollections.observableArrayList();
            ObservableList<Item> itemList = FXCollections.observableArrayList();
            ObservableList<String> itemTypeName = FXCollections.observableArrayList();


            // Creating OLs to save values from result set
            PreparedStatement getCustomerList = connection.prepareStatement("SELECT * FROM customers");
            PreparedStatement getItemList = connection.prepareStatement("SELECT *" +
                    "FROM item, itemtype WHERE item.ItemType_itemTypeId = itemtype.itemTypeId ORDER BY itemID");
            PreparedStatement getSellsList = connection.prepareStatement("SELECT * FROM purchases WHERE User_username ='"
                    + sessionUser + "'" + " ORDER BY(purchaseID) DESC");
            PreparedStatement getRentalList = connection.prepareStatement("SELECT * FROM rentals WHERE User_username ='"
                    + sessionUser + "'" + " ORDER BY(rentalID) DESC");
            PreparedStatement getAccountList = connection.prepareStatement("SELECT  customers.firstName, customers.lastName, accounts.acccountID, accounts.accountName, accounts.paymethod " +
                    "FROM accounts, customers WHERE User_username ='"
                    + sessionUser + "' AND Customers_customerID = customerID");
            PreparedStatement getItemType = connection.prepareStatement("SELECT * FROM itemtype");
            PreparedStatement getOutOfStock = connection.prepareStatement("SELECT * FROM item, itemtype WHERE itemTypeId = ItemType_itemTypeId AND stock =" + 0);

            ResultSet itemResultSet = getItemList.executeQuery();
            ResultSet customerResultSet = getCustomerList.executeQuery();
            ResultSet sellsList = getSellsList.executeQuery();
            ResultSet rentList = getRentalList.executeQuery();
            ResultSet accountResultSet = getAccountList.executeQuery();
            ResultSet itemType = getItemType.executeQuery();
            ResultSet stockOut = getOutOfStock.executeQuery();


            //DashboardController
            PreparedStatement getRentalDue = connection.prepareStatement("SELECT COUNT(*), SUM(amountDue) FROM rentals WHERE amountDue <> 0");
            PreparedStatement getPurchaseDue = connection.prepareStatement("SELECT COUNT(*), SUM(amountDue) FROM purchases WHERE amountDue <> 0");
            PreparedStatement getTodaysSell = connection.prepareStatement("SELECT COUNT(*), SUM(payAmount) FROM purchases WHERE purchaseDate = '" + Date.valueOf(LocalDate.now()) + "'");
            PreparedStatement getTodaysRent = connection.prepareStatement("SELECT COUNT(*), SUM(paid) FROM rentals WHERE rentalDate = '" + Date.valueOf(LocalDate.now()) + "'");
            PreparedStatement getTodaysRentalDue = connection.prepareStatement("SELECT COUNT(*), SUM(amountDue) FROM rentals WHERE amountDue <> 0 AND rentalDate = '" + Date.valueOf(LocalDate.now()) + "'");
            PreparedStatement getTodaysPurchaseDue = connection.prepareStatement("SELECT COUNT(*), SUM(amountDue) FROM purchases WHERE amountDue <> 0 AND purchaseDate = '" + Date.valueOf(LocalDate.now()) + "'");


            ResultSet rentalDue = getRentalDue.executeQuery();
            ResultSet purchaseDue = getPurchaseDue.executeQuery();
            ResultSet todaysSell = getTodaysSell.executeQuery();
            ResultSet todaysRent = getTodaysRent.executeQuery();
            ResultSet todaysRentDue = getTodaysRentalDue.executeQuery();
            ResultSet todysPurchaseDue = getTodaysPurchaseDue.executeQuery();
//--------------------------------------------------------------------------------------------------------
            // Updating task manager
            this.updateMessage("Loading customer...");
            Thread.sleep(THREAD_SLEEP_INTERVAL);

            ArrayList<String> customerIDNameHolder = new ArrayList<>(); //Will store ID and Name from ResultSet
            ArrayList<String> itemIDNameForSale = new ArrayList<>(); //Will hold item id name for sell
            ArrayList<String> customerName = new ArrayList<>();
            ArrayList<Integer> itemIDForSale = new ArrayList<>();
            ArrayList<String> itemIDNameForRentals = new ArrayList<>(); //Will hold item id name for rent
            ArrayList<Integer> itemIDForRent = new ArrayList<>();
            ArrayList<String> itemNames = new ArrayList<>();
            ArrayList<Integer> customerID = new ArrayList<>();
            TreeMap<String, Integer> itemTypeTree = new TreeMap<>();


            // Getting values from customers result set
            while (customerResultSet.next()) {
                customerIDNameHolder.add(customerResultSet.getInt(1) + " | "
                        + customerResultSet.getString(2) + "  "
                        + customerResultSet.getString(3));

                customerName.add(customerResultSet.getString(2)); //Adding first Name
                customerName.add(customerResultSet.getString(3)); //Adding last name

                Customer newCustomer = new Customer(
                        customerResultSet.getInt("customerID"),
                        customerResultSet.getString("firstName"),
                        customerResultSet.getString("lastName"),
                        customerResultSet.getString("address"),
                        customerResultSet.getString("phone"),
                        customerResultSet.getString("email"),
                        customerResultSet.getString("photo"),
                        customerResultSet.getString("gender"),
                        customerResultSet.getDate("memberSince"));

                customersList.add(newCustomer);

                customerID.add(customerResultSet.getInt(1));
            }

            //Setting fields in Customers List
            CustomerController.customersList = customersList;
            CustomerController.customerNames = customerName;


            //Setting Id and Name to SellsController, RentalsController, Accounts
            SellsController.customerID = customerIDNameHolder;
            SellsController.customerIDName = customerID;
            RentalsController.customerIDName = customerIDNameHolder;
            RentalsController.customerID= customerID;
            AccountController.customerIDName = customerIDNameHolder;


//--------------------------------------------------------------------------------------------------
//            Thread.sleep(THREAD_SLEEP_INTERVAL);
//            //Updating Task status
//            this.updateMessage("Loading Items...");
//
//
//            while(itemResultSet.next()) {
//                Item item = new Item(itemResultSet.getInt("itemID"),
//                        itemResultSet.getString("itemName"),
//                        itemResultSet.getInt("stock"),
//                        false,
//                        false,
//                        itemResultSet.getDouble("salePrice"),
//                        itemResultSet.getDouble("rentRate"),
//                        itemResultSet.getString("photo"),
//                        itemResultSet.getString("typeName"));
//
//                itemNames.add(itemResultSet.getString("itemName"));
//
//                if(itemResultSet.getString("rentalOrSale").contains("Rental"))
//                {
//                    item.setRent(true);
//                    itemIDNameForRentals.add(itemResultSet.getInt("itemID") + " | " +
//                            itemResultSet.getString("itemName"));
//                    itemIDForRent.add(itemResultSet.getInt("itemID"));
//                }
//                if(itemResultSet.getString("rentalOrSale").contains("Sale")) {
//                    itemIDNameForSale.add(itemResultSet.getInt("itemID") + " | " + itemResultSet.getString("itemName"));
//                    itemIDForSale.add(itemResultSet.getInt("itemID"));
//                    item.setSale(true);
//                }
//
//                itemList.add(item);
//
//            }
//
//            //Setting Observable Lists to the static field of InventoryController
//            InventoryController.itemList = itemList;
//            InventoryController.itemNames = itemNames;
//            SellsController.inven
//
//
//
//            Thread.sleep(THREAD_SLEEP_INTERVAL);
//





            return null;
        }
    }
}
