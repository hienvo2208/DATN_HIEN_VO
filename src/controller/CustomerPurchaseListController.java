package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import main.java.others.Purchase;
import repository.DBConnection;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;
import java.util.ResourceBundle;

public class CustomerPurchaseListController implements Initializable {
    @FXML
    private TableView<Purchase> tblRecent;
    @FXML
    private TableColumn<Purchase, Integer> purID;
    @FXML
    private TableColumn<Purchase, Integer> cusID;
    @FXML
    private TableColumn<Purchase, Integer> itemID;
    @FXML
    private TableColumn<Purchase, String> date;
    @FXML
    private TableColumn<Purchase, Integer> qty;
    @FXML
    private TableColumn<Purchase, Double> paidAmmount;
    @FXML
    private TableColumn<Purchase, Double> dueAmount;
    @FXML
    private TableColumn<Purchase, String> empName;
    @FXML
    private Label lblSellCount;
    @FXML
    private Label lblDue, today;
    @FXML
    private Label lblAmount;
    public static int customerID = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        today.setText(LocalDate.now().toString());

        purID.setCellValueFactory(new PropertyValueFactory<>("purID"));
        cusID.setCellValueFactory(new PropertyValueFactory<>("cusID"));
        itemID.setCellValueFactory(new PropertyValueFactory<>("itemID"));
        date.setCellValueFactory(new PropertyValueFactory<>("date"));
        qty.setCellValueFactory(new PropertyValueFactory<>("qty"));
        paidAmmount.setCellValueFactory(new PropertyValueFactory<>("paid"));
        dueAmount.setCellValueFactory(new PropertyValueFactory<>("due"));
        empName.setCellValueFactory(new PropertyValueFactory<>("user"));


        String sqlPurchase = "\n" +
                "SELECT *\n" +
                "FROM purchases\n" +
                "WHERE Customers_customerID = ?";

        Connection connection = DBConnection.getConnection();

        try {
            PreparedStatement pr = connection.prepareStatement(sqlPurchase);
            pr.setInt(1, customerID);
            ResultSet resultSet = pr.executeQuery();

            ObservableList<Purchase> purchaseObservableList = FXCollections.observableArrayList();

            double totalAmount = 0.0;
            double totalDue = 0.0;
            int ctn = 0;

            while (resultSet.next()) {
                Purchase purchase = new Purchase(
                        resultSet.getInt(1),
                        resultSet.getInt(7),
                        resultSet.getInt(6),
                        resultSet.getString(2),
                        resultSet.getInt(3),
                        resultSet.getInt(8),
                        resultSet.getDouble(4),
                        resultSet.getString(5));

                purchaseObservableList.add(purchase);
                totalAmount += resultSet.getDouble(8);
                totalDue += resultSet.getDouble(4);
                ctn++;
            }

            lblSellCount.setText(String.valueOf(ctn));
            lblAmount.setText(String.valueOf(totalAmount));
            lblDue.setText(String.valueOf(totalDue));
            tblRecent.setItems(purchaseObservableList);

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
