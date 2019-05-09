/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Views_Controllers;

import Model.Appointment;
import Model.DBConnection;
import Model.User;
import c195scheduler.C195Scheduler;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * FXML Controller class
 *
 * @author austinwise
 */
public class ReportsController {
    private C195Scheduler mainApp;
    private User currentUser;  
    private String locationOrCustomerName;
    
    @FXML private ComboBox locationMonthcb;
    @FXML private ComboBox customerMonthcb;
    @FXML private ComboBox userCb;
    
    @FXML private TableView<Appointment> locationTable;
    @FXML private TableColumn<Appointment, String> locationColumn;
    @FXML private TableColumn<Appointment, String> apptCountColumn;
    
    @FXML private TableView<Appointment> customerTable;
    @FXML private TableColumn<Appointment, String> customerColumn;
    @FXML private TableColumn<Appointment, String> customerApptCountColumn;
    
    @FXML private TableView<Appointment> userTable;
    @FXML private TableColumn<Appointment, String> userTitleColumn;
    @FXML private TableColumn<Appointment, String> userLocationColumn;
    @FXML private TableColumn<Appointment, LocalDateTime> userStartColumn;
    @FXML private TableColumn<Appointment, LocalDateTime> userEndColumn;
    @FXML private TableColumn<Appointment, String> userCustomerNameColumn;
    
    @FXML private void locationGoClick() throws SQLException{
            updateLocationTable();
    }
    
    private void updateLocationTable() throws SQLException{
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        apptCountColumn.setCellValueFactory(new PropertyValueFactory<>("apptCount"));
        
        locationTable.getItems().setAll(getLocationData());
    }
    
    public List<Appointment> getLocationData() throws SQLException{
        ObservableList<Appointment> apptLocationList = FXCollections.observableArrayList();
        String monthChosen = (String)locationMonthcb.getSelectionModel().getSelectedItem();
        int convertedMonth = convertMonth(monthChosen);
        
        String locationQuery = "SELECT location, COUNT(*) as apptcount FROM appointment WHERE MONTH(start) = ? GROUP BY location";
            PreparedStatement statement = DBConnection.getConn().prepareStatement(locationQuery);
            statement.setInt(1, convertedMonth);
            ResultSet rs = statement.executeQuery();
            
            while (rs.next()) {
                String location = rs.getString("location");  
                String apptcount = rs.getString("apptcount");
                apptLocationList.add(new Appointment(location, apptcount));
    }
    return apptLocationList;
}
    
    @FXML private void customerGoClick() throws SQLException{
        updateCustomerTable();
    }
    
    private void updateCustomerTable() throws SQLException{
        customerColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        customerApptCountColumn.setCellValueFactory(new PropertyValueFactory<>("apptCount"));
        
        customerTable.getItems().setAll(getCustomerData());
    }
    
    public List<Appointment> getCustomerData() throws SQLException{
        ObservableList<Appointment> apptCustomerList = FXCollections.observableArrayList();
        String monthChosen = (String)customerMonthcb.getSelectionModel().getSelectedItem();
        int convertedMonth = convertMonth(monthChosen);
        
        String locationQuery = "SELECT appointment.customerId, customer.customerName, Count(*) as apptcount FROM appointment INNER JOIN customer WHERE appointment.customerId = customer.customerId AND MONTH(start) = ? GROUP BY customerId";
            PreparedStatement statement = DBConnection.getConn().prepareStatement(locationQuery);
            statement.setInt(1, convertedMonth);
            ResultSet rs = statement.executeQuery();
            
            while (rs.next()) {
                String customerName = rs.getString("customer.customerName");  
                String apptcount = rs.getString("apptcount");
                int customerId = rs.getInt("appointment.customerId");
                
                System.out.println(customerName);
                apptCustomerList.add(new Appointment(customerName, apptcount, customerId));
    }
    return apptCustomerList;
}
    
    private int convertMonth(String monthChosen){
        if(monthChosen.equals("January")){
            return 1;
        }
        else if(monthChosen.equals("February")){
            return 2;
        }
        else if(monthChosen.equals("March")){
            return 3;
        }
        else if(monthChosen.equals("April")){
            return 4;
        }
        else if(monthChosen.equals("May")){
            return 5;
        }
        else if(monthChosen.equals("June")){
            return 6;
        }
        else if(monthChosen.equals("July")){
            return 7;
        }
        else if(monthChosen.equals("August")){
            return 8;
        }
        else if(monthChosen.equals("September")){
            return 9;
        }
        else if(monthChosen.equals("October")){
            return 10;
        }
        else if(monthChosen.equals("November")){
            return 11;
        }
        else{
            return 12;
        }
    }
    
    @FXML public void goBackClick(ActionEvent e) throws IOException, SQLException{
        mainApp.showMain(currentUser);
    }
    
    @FXML private void userGoClick() throws SQLException{
        updateUserTable();
    }
    
    private void updateUserTable() throws SQLException{
        userTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        userLocationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        userStartColumn.setCellValueFactory(new PropertyValueFactory<>("start"));
        userEndColumn.setCellValueFactory(new PropertyValueFactory<>("end"));
        userCustomerNameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        
        userTable.getItems().setAll(getUserData());
    }
    
    public List<Appointment> getUserData() throws SQLException{
        ObservableList<Appointment> apptUserList = FXCollections.observableArrayList();
        String userChosen = (String)userCb.getSelectionModel().getSelectedItem();
        
        String locationQuery = "SELECT a.createdBy, a.title, a.location, a.start, a.end, a.customerId, c.customerName\n" +
                                    "FROM appointment a\n" +
                                    "INNER JOIN customer c\n" +
                                    "WHERE a.customerId = c.customerId\n" +
                                    "AND a.createdBy = ?";
            PreparedStatement statement = DBConnection.getConn().prepareStatement(locationQuery);
            statement.setString(1, userChosen);
            ResultSet rs = statement.executeQuery();
            
            while (rs.next()) {
                String title = rs.getString("a.title");  
                String location = rs.getString("a.location");
                LocalDateTime start = rs.getTimestamp("a.start").toLocalDateTime();
                LocalDateTime end  = rs.getTimestamp("a.end").toLocalDateTime();
                String customerName = rs.getString("c.customerName");
                
                apptUserList.add(new Appointment(title, location, start, end, customerName));
    }
    return apptUserList;
}
   
    public void setReport(C195Scheduler mainApp, User activeUser) throws SQLException{
        this.mainApp = mainApp;
        this.currentUser = activeUser;
        
        populateUserCb();
        
        locationMonthcb.setItems(months);
        customerMonthcb.setItems(months);
        userCb.setItems(users);
    }
    
    private void populateUserCb() throws SQLException{
        String userQuery = "SELECT userName FROM user";
            PreparedStatement statement = DBConnection.getConn().prepareStatement(userQuery);
            ResultSet rs = statement.executeQuery();
            
            while (rs.next()) {
                String userName = rs.getString("userName");               
                users.add(userName);
       }
    }
    
    ObservableList<String> users = FXCollections.observableArrayList();
    ObservableList<String> months = FXCollections.observableArrayList( 
    "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"
    );
}
