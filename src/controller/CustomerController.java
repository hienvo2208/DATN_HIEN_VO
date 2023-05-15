package controller;

import entites.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;

public class CustomerController {
    public static ObservableList<Customer> customersList = FXCollections.observableArrayList();
    public static ArrayList<String> customerNames = new ArrayList<>();
}
