package controller;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import repository.DBConnection;

import java.awt.event.ActionEvent;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ResourceBundle;


/**
 * Author: HienVo
 * Written on: May 19th 23.
 * Project: DATN
 **/

public class DashBoardController implements Initializable {
    @FXML
    private JFXButton btnTodaySell;
    @FXML
    private Label lblTodaySellCtr;
    @FXML
    private Label lblTodaysSellAmount;
    @FXML
    private JFXButton btnTodayRental;
    @FXML
    private Label lblTodaysRentalCtr;
    @FXML
    private Label lblTodaysRentalAmount;
    @FXML
    private JFXButton loadAgain;
    @FXML
    private Label lblOutOfStock;
    @FXML
    private Label lblTotalDueAmount;
    @FXML
    private Label lblTodaysDueAmount;
    public static Integer todaysRentalCtr = 0;
    public static Integer totalDueCtr = 0;
    public static Integer todaySellCtr = 0;
    public static Double todaysTotalDue = 0.0;
    public static Double todaysTotalSell = 0.0;
    public static Double todayTotalRental = 0.0;
    public static Double totalDueAmount = 0.0;
    public static Integer stockOut = 0;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setFields();
    }


    private void setFields() {

        //Tổng số tiền nợ.
        lblTotalDueAmount.setText(totalDueAmount.toString() + "VND");

        //Số lượng đơn hàng
        lblTodaySellCtr.setText(todaySellCtr.toString());
        //Doanh thu trong ngay
        lblTodaysSellAmount.setText(todaysTotalSell.toString());

        //Số lượng cho thuê trong ngày.
        lblTodaysRentalCtr.setText(todaysRentalCtr.toString());
        //Doanh thu cho thuê trong ngày.
        lblTodaysRentalAmount.setText(todayTotalRental.toString());

        //Tiền nợ trong ngày.
        lblTodaysDueAmount.setText(todaysTotalDue.toString() + "VND");


        //Hết hàng
        lblOutOfStock.setText(stockOut.toString());
    }



    @FXML
    public void showRent(javafx.event.ActionEvent event) {

    }

    @FXML
    public void showSell(javafx.event.ActionEvent event) {
    }


    @FXML
    public void loadAgain(javafx.event.ActionEvent event) {
        Connection cn = DBConnection.getConnection();

        try {
            //Tổng số tiền nợ của khách hàng (Cho thuê).
            PreparedStatement getTotalRentalDue = cn.prepareStatement("SELECT COUNT(*), SUM(amountDue) FROM rentals WHERE amountDue <> 0");

            //Tổng số tiền nợ của khách hàng(Bán)
            PreparedStatement getTotalPurchaseDue = cn.prepareStatement("SELECT COUNT(*), SUM(amountDue) FROM purchases WHERE amountDue <> 0");

            //Doanh thu trong ngày (Bán)
            PreparedStatement getTodayPurchase = cn.prepareStatement("SELECT COUNT(*), SUM(payAmount) FROM purchases WHERE purchaseDate =" + Date.valueOf(LocalDate.now()));

            //Doanh thu trong ngày(Thuê)
            PreparedStatement getTodayRental = cn.prepareStatement("SELECT COUNT(*), SUM(paid) FROM rentals WHERE rentalDate =" + Date.valueOf(LocalDate.now()));

            //Tổng số tiền cho nợ trong ngày (Thuê)
            PreparedStatement getTodayRentalDue = cn.prepareStatement("SELECT COUNT(*), SUM(amountDue) FROM rentals WHERE rentalDate = " + Date.valueOf(LocalDate.now()));

            //Tổng số tiền cho nợ trong ngày(Bán)
            PreparedStatement getTodayPurchaseDue = cn.prepareStatement("SELECT COUNT(*), SUM(amountDue) FROM purchases WHERE purchaseDate =" + Date.valueOf(LocalDate.now()));

            //Hàng bị hết.
            PreparedStatement getStock = cn.prepareStatement("SELECT * FROM item, itemtype WHERE itemTypeId = ItemType_itemTypeId and stock = " + 0);


            ResultSet totalRentalDue = getTotalRentalDue.executeQuery();
            ResultSet totalPurchaseDue = getTotalPurchaseDue.executeQuery();
            ResultSet todayPurchase = getTodayPurchase.executeQuery();
            ResultSet todayRental = getTodayRental.executeQuery();
            ResultSet todayRentalDue = getTodayRentalDue.executeQuery();
            ResultSet todayPurchaseDue = getTodayPurchaseDue.executeQuery();
            ResultSet stock = getStock.executeQuery();

            // Tổng số tiền nợ (Thuê) + (Bán)
            double totalDue = 0;
            while (totalRentalDue.next()){
                totalDue += totalRentalDue.getDouble(2);
            }

            while(totalPurchaseDue.next()){
                totalDue += totalPurchaseDue.getDouble(2);
            }


            //Doanh thu trong ngày (Bán)
            int purchaseCtr = 0;
            double dayPAmount = 0;
            while(todayPurchase.next()){
                dayPAmount +=  todayPurchase.getDouble(2);
                purchaseCtr += todayPurchase.getInt(1);
            }

            //Doanh thu trong ngay (Thue)
            double dayR = 0;
            int dRenCtr =0;
            while(todayRental.next()){
                dayR += todayRental.getDouble(2);
                dRenCtr += todayRental.getInt(1);
            }

            //Tổng số tiền cho nợ trong ngày (Thuê)
            double dayRD = 0;
            while(todayRentalDue.next()){
                dayRD += todayRentalDue.getDouble(2);
            }

            //Tổng số iền cho cho nợ trong ngày(Bán)
            double dayPD = 0;
            while(todayPurchaseDue.next()){
                dayPD += todayPurchaseDue.getDouble(2);
            }

            //Hàng bị hết
            int st = 0;
            while(stock.next()){
                st+=stock.getInt(2);
            }

            totalDueAmount = totalDue;
            todaySellCtr = purchaseCtr;
            todaysTotalSell = dayPAmount;
            todaysRentalCtr = dRenCtr;
            todayTotalRental = dayR;
            todaysTotalDue = dayPD;
            stockOut = st;

            setFields();


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
