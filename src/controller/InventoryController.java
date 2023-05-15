package controller;

import entites.Item;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.TreeMap;

public class InventoryController {
    private static int recordIndex = 0;
    private static int recordSize = 0;




    private static boolean addFlag = false;
    private static boolean searchDone = false;
    private static String imgPath = null;
    public static TreeMap<String, Integer> itemType = new TreeMap<>();
    public static ObservableList<Item> itemList = FXCollections.observableArrayList(); //This field will auto set from InitializerController Class
    public static ObservableList<Item> tempList = FXCollections.observableArrayList(); //Will hold the main list while searching
    public static ArrayList<String> itemNames = new ArrayList<>();
    public static ObservableList<String> itemTypeNames = FXCollections.observableArrayList();

}
