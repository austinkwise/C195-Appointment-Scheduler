/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Views_Controllers;

import Model.Address;
import Model.City;
import Model.Country;
import Model.Customer;
import Model.DBConnection;
import Model.User;
import c195scheduler.C195Scheduler;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * FXML Controller class
 *
 * @author austinwise
 */
public class CustomersController{
   
    //CustomersController(){}
    
    @FXML private TableView<Customer> customerTable;
    @FXML private TableColumn<Customer, String> customerNameColumn;
    @FXML private TableColumn<Customer, String> customerPhoneColumn;
    
    @FXML private TextField customerIdtf;
    @FXML private TextField customerNametf;
    @FXML private TextField customerAddresstf;
    @FXML private TextField customerAddress2tf;
    @FXML private TextField customerCitytf;
    @FXML private TextField customerPostalCodetf;
    @FXML private TextField customerPhonetf;
    @FXML private TextField customerCountrytf;
    
    @FXML private Label customerDetailsHeader;
    
    private C195Scheduler mainApp;
    private User currentUser;
    private static Customer modCustomerSelected;
    private static int modIdx = 0;
    private Connection conn = DBConnection.getConn();

    @FXML private void addNewCustomerBtn(ActionEvent e){
        clearCustomerDetails();
        resetModCustomer();
        customerDetailsHeader.setText("Add New Customer");
        customerIdtf.setText("System Generated");
        modIdx = -1;
        enableTextFields();
    }
    
    @FXML private void cancelClick(){
        clearCustomerDetails();
        disableTextFields();
        modIdx = 0;
        resetModCustomer();
    }
    
    @FXML private void editClick() throws IOException, SQLException{
        if(modCustomerSelected != null){
            modIdx = -2;
            enableTextFields();
            setModCustomer(customerTable.getSelectionModel().getSelectedItem());
            mainApp.showCustomerScreen(currentUser);
        }
    }

    @FXML private void saveCustomerButton() throws SQLException{
        String name = customerNametf.getText();
        String address = customerAddresstf.getText();
        String address2 = customerAddress2tf.getText();
        String city = customerCitytf.getText();
        String postalCode = customerPostalCodetf.getText();
        String phone = customerPhonetf.getText();
        String country = customerCountrytf.getText();
        
        if(modIdx == -1){
            //adding a customer not editing
            Country currentCountry = null;
            currentCountry = Country.validateCountry(country);
            if(currentCountry == null){
                insertNewCountry(country, currentCountry);
            }
            
            City currentCity = null;
            int getCountryid = currentCountry.getCountryId(); //using this to try and figure out where we're having issues
            currentCity = City.validateCity(city, getCountryid);
            System.out.println("city: " + currentCity);//****
            if(currentCity == null){
                insertNewCity(city, currentCity, currentCountry);
            }
            
            Address currentAddress = null;
            int getCityid = currentCity.getCityId();
            currentAddress = Address.validateAddress(address, address2, getCityid, postalCode, phone);
            System.out.println("address: " + currentAddress);//****
            if(currentAddress == null){
                currentAddress = insertNewAddress(address, address2, postalCode, phone, currentCity, currentAddress);
                System.out.println("new address inserted");
            }
            insertNewCustomer(name, currentAddress);
        }
        else if(modIdx == -2){
            //editing a customer, not adding
            String updateQuery = "UPDATE customer, address, city, country"
            + " SET customerName = ?,"
            + " customer.lastUpdate = CURRENT_TIMESTAMP,"
            + " customer.lastUpdateBy = ?,"
            + " address.address = ?,"
            + " address.address2 = ?,"
            + " address.postalCode = ?,"
            + " address.phone = ?,"        
            + " address.lastUpdate = CURRENT_TIMESTAMP,"
            + " address.lastUpdateBy = ?,"
            + " city.city = ?,"
            + " city.lastUpdate = CURRENT_TIMESTAMP,"
            + " city.lastUpdateby = ?,"
            + " country.country = ?,"
            + " country.lastUpdate = CURRENT_TIMESTAMP,"
            + " country.lastUpdateBy = ?"
            + " WHERE customer.customerId = ?"
            + " AND customer.addressId = address.addressId"
            + " AND address.cityId = city.cityId"
            + " AND city.countryId = country.countryId";
            int i = 1;
            PreparedStatement st = conn.prepareStatement(updateQuery);
            st.setString(i++, name);
            st.setString(i++, currentUser.getUsername());
            st.setString(i++, address);
            st.setString(i++, address2);
            st.setString(i++, postalCode);
            st.setString(i++, phone);
            st.setString(i++, currentUser.getUsername());
            st.setString(i++, city);
            st.setString(i++, currentUser.getUsername());
            st.setString(i++, country);
            st.setString(i++, currentUser.getUsername());
            st.setInt(i++, CustomersController.getModCustomer().getCustomerId());
            st.executeUpdate();
            st.close();
              
            resetModCustomer();
            //mainApp.closePopup(currentUser);
        }
        modIdx = 0;
    }
    
    public void setCustomerScreen(C195Scheduler mainApp, User activeUser) throws SQLException{
        this.mainApp = mainApp;
        this.currentUser = activeUser;
        
        disableTextFields();
        //setting up cellvalue factories goes here setupCustomers
        customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        customerPhoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        
        customerTable.setRowFactory(ttv -> {
            TableRow<Customer> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    setModCustomer(customerTable.getSelectionModel().getSelectedItem());
                    
                    try {
                        mainApp.showCustomerScreen(currentUser);
                    } catch (IOException ex) {
                        Logger.getLogger(CustomersController.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (SQLException ex) {
                        Logger.getLogger(CustomersController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                }
            });
            return row;
        });
        
        customerTable.getItems().setAll(getCustomerData());
        
        if (modCustomerSelected != null){
            customerIdtf.setText(Integer.toString(modCustomerSelected.getCustomerId()));
            customerNametf.setText(modCustomerSelected.getCustomerName());
            customerAddresstf.setText(modCustomerSelected.getAddress());
            customerAddresstf.setText(modCustomerSelected.getAddress2());
            customerCitytf.setText(modCustomerSelected.getCity());
            customerPostalCodetf.setText(modCustomerSelected.getPostalCode());
            customerCountrytf.setText(modCustomerSelected.getCountry());
            customerPhonetf.setText(modCustomerSelected.getPhone());
        }
    }
    
    
    
    private void resetModCustomer(){
        modCustomerSelected = null;
    }

    private static void setModCustomer(Customer customer){
        modCustomerSelected = customer;
    }
    
    private static Customer getModCustomer(){
        return modCustomerSelected;
    }
    
    private void clearCustomerDetails(){
        customerIdtf.clear();
        customerNametf.clear();
        customerAddresstf.clear();
        customerAddress2tf.clear();
        customerCitytf.clear();
        customerPostalCodetf.clear();
        customerPhonetf.clear();
        customerCountrytf.clear();
    }
    
    private void disableTextFields(){
        customerIdtf.setEditable(false);
        customerNametf.setEditable(false);
        customerAddresstf.setEditable(false);
        customerAddress2tf.setEditable(false);
        customerCitytf.setEditable(false);
        customerPostalCodetf.setEditable(false);
        customerPhonetf.setEditable(false);
        customerCountrytf.setEditable(false);
    }
    
    private void enableTextFields(){
        customerIdtf.setEditable(true);
        customerNametf.setEditable(true);
        customerAddresstf.setEditable(true);
        customerAddress2tf.setEditable(true);
        customerCitytf.setEditable(true);
        customerPostalCodetf.setEditable(true);
        customerPhonetf.setEditable(true);
        customerCountrytf.setEditable(true);
    }
    
    private Country insertNewCountry(String country, Country currentCountry) throws SQLException{
        String countryQuery = "INSERT INTO "
            + "country (country, createDate, createdBy, lastUpdate, lastUpdateBy) "
            + "VALUES (?, CURRENT_TIMESTAMP, ?, CURRENT_TIMESTAMP, ?)"; 

        PreparedStatement countryStatement = conn.prepareStatement(countryQuery);
            countryStatement.setString(1, country);
            countryStatement.setString(2, currentUser.getUsername());
            countryStatement.setString(3, currentUser.getUsername());
            countryStatement.executeUpdate();
            currentCountry = Country.validateCountry(country);
            countryStatement.close();
            
            return currentCountry;
    }
    
    private City insertNewCity(String city, City currentCity, Country currentCountry) throws SQLException{
        String cityQuery = "INSERT INTO "
            + "city (city, countryId, createDate, createdBy, lastUpdate, lastUpdateBy) "
            + "VALUES (?, ?, CURRENT_TIMESTAMP, ?, CURRENT_TIMESTAMP, ?)"; 
        PreparedStatement cityStatement = conn.prepareStatement(cityQuery);
            cityStatement.setString(1, city);
            cityStatement.setInt(2, currentCountry.getCountryId());
            cityStatement.setString(3, currentUser.getUsername());
            cityStatement.setString(4, currentUser.getUsername());
            cityStatement.executeUpdate();
            currentCity = City.validateCity(city, currentCountry.getCountryId());
            cityStatement.close();
            
            return currentCity;
    }
    
    private Address insertNewAddress(String address, String address2, String postalCode, String phone, City currentCity, Address currentAddress) throws SQLException{
        String addressQuery = "INSERT INTO "
            + "address (address, address2, cityId, postalCode, phone, createDate, createdBy, lastUpdate, lastUpdateBy) "
            + "VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP, ?, CURRENT_TIMESTAMP, ?)";
        PreparedStatement addressStatement = conn.prepareStatement(addressQuery);
            addressStatement.setString(1, address);
            addressStatement.setString(2, address2);
            addressStatement.setInt(3, currentCity.getCityId());
            addressStatement.setString(4, postalCode);
            addressStatement.setString(5, phone);
            addressStatement.setString(6, currentUser.getUsername());
            addressStatement.setString(7, currentUser.getUsername());

            addressStatement.executeUpdate();
            currentAddress = Address.validateAddress(address, address2, currentCity.getCityId(), postalCode, phone);
            System.out.println("address: " + currentAddress);//****
            addressStatement.close();
            
            return currentAddress;
    }

    private void insertNewCustomer(String name, Address currentAddress) throws SQLException{
        int getCurrentAddressid = currentAddress.getAddressId();
        
        String customerQuery = "INSERT INTO "              
            + "customer (customerName, addressId, active, createDate, createdBy, lastUpdate, lastUpdateBy) "
            + "VALUES (?, ?, ?, CURRENT_TIMESTAMP, ?, CURRENT_TIMESTAMP, ?)";            
        PreparedStatement customerStatement = conn.prepareStatement(customerQuery);
            customerStatement.setString(1, name);
            customerStatement.setInt(2, getCurrentAddressid);  
            customerStatement.setInt(3, 1); 
            customerStatement.setString(4, currentUser.getUsername());
            customerStatement.setString(5, currentUser.getUsername());
            customerStatement.executeUpdate();
            customerStatement.close();
    }

    public List<Customer> getCustomerData() throws SQLException{
        ObservableList<Customer> customerList = FXCollections.observableArrayList();
        String customerQuery = "SELECT customer.customerId, customer.customerName, "
            + "address.address, address.address2, address.postalCode, city.cityId, "
            + "city.city, country.country, address.phone " 
            + "FROM customer, address, city, country " 
            + "WHERE customer.addressId = address.addressId "
            + "AND address.cityId = city.cityId AND city.countryId = country.countryId "
            + "ORDER BY customer.customerId";

        PreparedStatement smt = DBConnection.getConn().prepareStatement(customerQuery);
        ResultSet customersFound = smt.executeQuery();

        while (customersFound.next()) {
            Integer dCustomerId = customersFound.getInt("customer.customerId");
            String dCustomerName = customersFound.getString("customer.customerName");
            String dAddress = customersFound.getString("address.address");
            String dAddress2 = customersFound.getString("address.address2");
            String dCity = customersFound.getString("city.city");
            String dPostalCode = customersFound.getString("address.postalCode");
            String dCountry = customersFound.getString("country.country");
            String dPhone = customersFound.getString("address.phone");
            customerList.add(new Customer (dCustomerId, dCustomerName, 
                    dAddress, dAddress2, dCity, dCountry, dPostalCode, dPhone));
        }
         
        return customerList;
    }
   

}