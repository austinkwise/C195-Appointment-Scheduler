/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Views_Controllers;

import Model.Appointment;
import Model.Customer;
import Model.DBConnection;
import Model.User;
import c195scheduler.C195Scheduler;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * FXML Controller class
 *
 * @author austinwise
 */
public class NewApptController {
    @FXML private TableView<Customer> customerTable;
    @FXML private TableColumn<Customer, String> customerNameColumn;
    @FXML private TableColumn<Customer, String> customerPhoneColumn;
    
    @FXML private ComboBox<Integer> hoursCb;
    @FXML private ComboBox<Integer> minutesCb;
    @FXML private ComboBox<Integer> durationCb;
    @FXML private ComboBox<String> locationCb;
    @FXML private ComboBox<String> customerCb;
    
    @FXML private TextField titleTf;
    @FXML private TextArea descriptionTa;
    @FXML private Label errorLabel;
    
    @FXML private DatePicker datePicked;
    
    private C195Scheduler mainApp;
    private User currentUser;
    private Connection conn = DBConnection.getConn();
    private Appointment apptModSelected = MainController.getSelectedAppt();
    private int hoursOffset;
    
    @FXML private void cancelClick(ActionEvent e) throws IOException, SQLException{
        mainApp.showMain(currentUser);
    }
    
    @FXML private void saveNewApptClick(ActionEvent e) throws SQLException, IOException{
        int customerId = 0;
        String customerName = customerCb.getSelectionModel().getSelectedItem();
        String apptTitle = titleTf.getText();
        Integer hour = hoursCb.getSelectionModel().getSelectedItem();
        Integer minute = minutesCb.getSelectionModel().getSelectedItem();
        Integer duration = durationCb.getSelectionModel().getSelectedItem();
        String location = locationCb.getSelectionModel().getSelectedItem();
        int userId = currentUser.getUserId();
        String username = currentUser.getUsername();
        LocalDate date = null;
        if(datePicked != null){
            date = datePicked.getValue();
        }
        String description = descriptionTa.getText();
        
        LocalDateTime startDate = LocalDateTime.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth(), hour, minute);
        LocalDateTime endDate = startDate.plusMinutes(duration);
        LocalDateTime now = LocalDateTime.now();
        
        int time = (hour-hoursOffset)*60+minute+duration;
        
        PreparedStatement custIdSmt = conn.prepareStatement("SELECT customer.customerId"
                        + " FROM customer"
                        + " WHERE customer.customerName = ?");
                custIdSmt.setString(1, customerName);
                ResultSet custIdRs = custIdSmt.executeQuery();
                if (custIdRs.next())
                        customerId = custIdRs.getInt("customer.customerId");
        
        
        
        if(time > 990){
            errorLabel.setText("Please choose a time within business hours.\n ");
        }
        
        else if (overlappingAppts(startDate, endDate)){
            errorLabel.setText("Please choose a different time.\n Current appointment has overlap.");
        }
        
        else if(now.compareTo(startDate) > 0){
            errorLabel.setText("Please choose a time that is in the future.\n ");
        }
        
        else{
        
        String apptQuery = null;
        
        if(apptModSelected == null){
            apptQuery = "INSERT INTO appointment ("
                    + " customerId, title, description, location, contact, url, start, end, createDate, createdBy, lastUpdate, lastUpdateBy) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, ?, CURRENT_TIMESTAMP, ?)";
        }
        else{
            System.out.println("edit sql reached");
            //something is going wrong here, not updating
            apptQuery = "UPDATE appointment "
                            + " SET customerId = ?, title = ?, description = ?, location = ?, contact = ?," 
                            + " url = ?, start = ?, end = ?, lastUpdate = CURRENT_TIMESTAMP, lastUpdateBy = ?"
                            + " WHERE appointmentId = ?";
        }
        PreparedStatement statement = conn.prepareStatement(apptQuery);
            int i = 1;
            statement.setInt(i++, customerId);
            statement.setString(i++, apptTitle);
            statement.setString(i++, description);
            statement.setString(i++, location);
            statement.setString(i++, username);
            statement.setString(i++, "No URL");
            statement.setTimestamp(i++, Timestamp.valueOf(startDate));
            statement.setTimestamp(i++, Timestamp.valueOf(endDate));
            statement.setString(i++, username);
            
            if (apptModSelected == null)
                statement.setString(i++, username);
            else
                statement.setInt(i++, apptModSelected.getAppointmentId());
            statement.executeUpdate();
            statement.close();
        

        mainApp.showMain(currentUser);
        }
    }
    
    private void populateCustomerCb() throws SQLException{
        String userQuery = "SELECT customerName FROM customer";
            PreparedStatement statement = DBConnection.getConn().prepareStatement(userQuery);
            ResultSet rs = statement.executeQuery();
            
            while (rs.next()) {
                String customerName = rs.getString("customerName");               
                customers.add(customerName);
       }
    }
    
    ObservableList<String> customers = FXCollections.observableArrayList();
    
    public void setNewApptScreen(C195Scheduler mainApp, User activeUser) throws SQLException{
        this.mainApp = mainApp;
        this.currentUser = activeUser;
        OffsetDateTime offset = OffsetDateTime.now();
        ZoneOffset zoneOffset = offset.getOffset();
        hoursOffset = zoneOffset.getTotalSeconds()/60/60;
        hoursOffset = hoursOffset - (-4);//set for New Yorks eastern time
        
        populateCustomerCb();
    
        durationCb.setItems(durations);
        hoursCb.setItems(hours);
        minutesCb.setItems(minutes);
        locationCb.setItems(locations);
        customerCb.setItems(customers);
        
        customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        customerPhoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        
        customerTable.getItems().setAll(getCustomerData());
       
        if(apptModSelected != null){
            titleTf.setText(apptModSelected.getTitle());
            datePicked.setValue(apptModSelected.getStart().toLocalDate());
            descriptionTa.setText(apptModSelected.getDescription());
            Integer hour = apptModSelected.getStart().getHour();
            hoursCb.getSelectionModel().select(hour);
            Integer minute = apptModSelected.getStart().getMinute();
            minutesCb.getSelectionModel().select(minute);
            Integer endHour = apptModSelected.getEnd().getHour();
            Integer endMinute = apptModSelected.getEnd().getMinute();
            Integer duration = (endHour-hour)*60 + (endMinute-minute);
            durationCb.getSelectionModel().select(duration);
            locationCb.getSelectionModel().select(apptModSelected.getLocation());
            customerCb.getSelectionModel().select(apptModSelected.getCustomerName());
            
            customerTable.getSelectionModel().select(apptModSelected.getCustomerId());
        }
    }

    ObservableList<String> locations = FXCollections.observableArrayList( 
    "Phone", "Video Conference", "Phoenix Office", "New York Office", "London Office");
    ObservableList<Integer> hours = FXCollections.observableArrayList( 
    7 + hoursOffset, 8 + hoursOffset, 9 + hoursOffset, 10 + hoursOffset, 11 + hoursOffset, 12 + hoursOffset, 13 + hoursOffset, 14 + hoursOffset, 15 + hoursOffset, 16 + hoursOffset);
    ObservableList<Integer> minutes = FXCollections.observableArrayList( 
    00, 15, 30, 45);
    ObservableList<Integer> durations = FXCollections.observableArrayList( 
    15, 30, 45, 60);
    
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
    
    public boolean overlappingAppts(LocalDateTime startDate, LocalDateTime endDate) throws SQLException{
        int apptId;
        String consultant;
        if (apptModSelected != null) {
            //edited appointment
            apptId = apptModSelected.getAppointmentId();
            consultant = apptModSelected.getUsername();
        } else {
            //new appointment
            apptId = 0;
            consultant = currentUser.getUsername();
        }
        
        PreparedStatement pst = DBConnection.getConn().prepareStatement(
        "SELECT * FROM appointment "
	+ "WHERE (? BETWEEN start AND end OR ? BETWEEN start AND end OR ? < start AND ? > end) "
	+ "AND (createdBy = ? AND appointmentID != ?)");
        pst.setTimestamp(1, Timestamp.valueOf(startDate));
	pst.setTimestamp(2, Timestamp.valueOf(endDate));
        pst.setTimestamp(3, Timestamp.valueOf(startDate));
	pst.setTimestamp(4, Timestamp.valueOf(endDate));
        pst.setString(5, consultant);
        pst.setInt(6, apptId);
        ResultSet rs = pst.executeQuery();
           
        if(rs.next()) {
            return true;
	}
        return false;
    }
    
    
    
}
