package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import main.java.others.Rent;
import repository.DBConnection;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class CustomersRentalListController implements Initializable {
    @FXML
    private Label lblSellCount;
    @FXML
    private Label lblDue;
    @FXML
    private Label lblAmount;
    @FXML
    private Label today;
    @FXML
    private TableView<Rent> tblRecent;
    @FXML
    private TableColumn<entites.Rent, Integer> rentID;
    @FXML
    private TableColumn<entites.Rent, Integer> cusID;
    @FXML
    private TableColumn<entites.Rent, Integer> itemID;
    @FXML
    private TableColumn<entites.Rent, String> rentalDate;
    @FXML
    private TableColumn<entites.Rent, String> returnDate;
    @FXML
    private TableColumn<entites.Rent, String> empName;
    @FXML
    private TableColumn<entites.Rent, Double> paid;
    @FXML
    private TableColumn<entites.Rent, Double> due;
    public static int customerID = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        today.setText(LocalDate.now().toString());

        rentID.setCellValueFactory(new PropertyValueFactory<>("rentID"));
        itemID.setCellValueFactory(new PropertyValueFactory<>("itemID"));
        cusID.setCellValueFactory(new PropertyValueFactory<>("cusID"));
        rentalDate.setCellValueFactory(new PropertyValueFactory<>("rentDate"));
        returnDate.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        paid.setCellValueFactory(new PropertyValueFactory<>("payAmount"));
        due.setCellValueFactory(new PropertyValueFactory<>("amountDue"));
        empName.setCellValueFactory(new PropertyValueFactory<>("user"));

        Connection connection = DBConnection.getConnection();
        String sqlGetRental = "SELECT *\n" +
                "FROM rentals\n" +
                "WHERE Customers_customerID = ?";
        try {
            PreparedStatement pr = connection.prepareStatement(sqlGetRental);
            pr.setInt(1, customerID);
            ResultSet resultSet = pr.executeQuery();

            double totalAmount = 0.0;
            double totalDue = 0.0;
            int ctn  =0;

            ObservableList<Rent> rentsListByUser = FXCollections.observableArrayList();

            while (resultSet.next()){

                Rent rent = new Rent(
                        resultSet.getInt(1),
                        resultSet.getInt(7),
                        resultSet.getInt(8),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getDouble(4),
                        resultSet.getDouble(5),
                        resultSet.getString(6)

                );


                rentsListByUser.add(rent);
                totalAmount += resultSet.getDouble(4);
                totalDue += resultSet.getDouble(5);
                ctn++;
            }

            tblRecent.setItems(rentsListByUser);

            lblSellCount.setText(String.valueOf(ctn));
            lblDue.setText(String.valueOf(totalDue));
            lblAmount.setText(String.valueOf(totalAmount));
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }
}
