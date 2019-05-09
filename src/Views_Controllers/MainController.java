package Views_Controllers;

import Model.Appointment;
import Model.Customer;
import Model.DBConnection;
import Model.User;
import c195scheduler.C195Scheduler;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * FXML Controller class
 *
 * @author austinwise
 */
public class MainController {
    private Stage primaryStage;
    private C195Scheduler mainApp;
    private User currentUser;
    private DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("MM/dd/yy HH:mm");
    private static Appointment apptModSelected;
    private String monthWeekQuery = "";
    
    @FXML private TableView<Appointment> appointmentTable;
    @FXML private TableColumn<Appointment, String> titleColumn;
    @FXML private TableColumn<Appointment, String> descriptionColumn;
    @FXML private TableColumn<Appointment, String> locationColumn;
    @FXML private TableColumn<Appointment, LocalDateTime> startColumn;
    @FXML private TableColumn<Appointment, LocalDateTime> endColumn;
    @FXML private TableColumn<Appointment, String> customerNameColumn;
    
    @FXML private RadioButton monthrb;
    @FXML private RadioButton weekrb;
    private ToggleGroup sortBytg;
    
    public void setMain(C195Scheduler mainApp, User activeUser) throws SQLException{
        this.mainApp = mainApp;
        this.currentUser = activeUser;
        resetSelectedAppt();

        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        startColumn.setCellValueFactory(new PropertyValueFactory<>("start"));
        startColumn.setCellFactory(column -> new TableCell<Appointment, LocalDateTime>() {
            protected void updateItem(LocalDateTime date, boolean empty) {
                super.updateItem(date, empty);
                if (empty) 
                    setText("");
                else 
                    setText(formatDate.format(date));
            }
        });
        endColumn.setCellValueFactory(new PropertyValueFactory<>("end"));
        endColumn.setCellFactory(column -> new TableCell<Appointment, LocalDateTime>() {
        
            protected void updateItem(LocalDateTime date, boolean empty) {
                super.updateItem(date, empty);
                if (empty) 
                    setText("");
                else 
                    setText(formatDate.format(date));
            }
        });
        customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        
        appointmentTable.getItems().setAll(getAppointmentData());
        setToggleGroups();
        
        //this makes it easier for someone to login and view the appointment they want rather than selecting appointment then clicking edit for details
        appointmentTable.setRowFactory(ttv -> {
            TableRow<Appointment> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    setSelectedAppt(appointmentTable.getSelectionModel().getSelectedItem());
                    
                    try {
                        mainApp.showNewApptScreen(currentUser);
                    } catch (IOException ex) {
                        Logger.getLogger(CustomersController.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (SQLException ex) {
                        Logger.getLogger(CustomersController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                }
            });
            return row;
        });
    }
    
    @FXML private void logoutClick(ActionEvent e) throws IOException{
        mainApp.showLoginScreen();
    }

    @FXML private void customersClick(ActionEvent e) throws IOException, SQLException{
        mainApp.showCustomerScreen(currentUser);
    }
    
    @FXML private void newApptClick(ActionEvent e) throws IOException, SQLException {
        resetSelectedAppt();
        mainApp.showNewApptScreen(currentUser);
    }
    
    @FXML private void editApptClick(ActionEvent e) throws IOException, SQLException{
        apptModSelected = appointmentTable.getSelectionModel().getSelectedItem();
        if(apptModSelected != null){
            mainApp.showNewApptScreen(currentUser);
        }
    }
    
    @FXML private void deleteApptClick(ActionEvent e) throws SQLException, IOException{
        apptModSelected = appointmentTable.getSelectionModel().getSelectedItem();
        if(apptModSelected != null){
            String deleteQuery = "DELETE appointment.*"
                        + " FROM appointment"
                        + " WHERE appointment.appointmentId = ?";
            PreparedStatement deleteSmt = DBConnection.getConn().prepareStatement(deleteQuery);
            deleteSmt.setInt(1, apptModSelected.getAppointmentId());
            deleteSmt.executeUpdate();
            mainApp.showMain(currentUser);
        }
    }
    
    @FXML private void monthSwitch(ActionEvent e) throws SQLException{
        monthrb.setSelected(true);
        monthWeekQuery = "AND DATE(appointment.start) BETWEEN \n" +
                            "DATE_ADD(DATE_ADD(LAST_DAY(now()), INTERVAL 1 DAY),INTERVAL - 1 MONTH) \n" +
                            "AND LAST_DAY(now())";
        updateTable();
    }
    
    @FXML private void weekSwitch(ActionEvent e) throws SQLException{
        weekrb.setSelected(true);
        monthWeekQuery = "AND DATE(appointment.start) BETWEEN  DATE(NOW()) AND DATE(NOW() + INTERVAL 7 DAY)";
        updateTable();
    }
    
    @FXML private void appointmentsClick(ActionEvent e) throws IOException, SQLException{
        mainApp.showMain(currentUser);
    }
    
    @FXML private void reportsClick(ActionEvent e) throws IOException, SQLException{
        mainApp.showReports(currentUser);
    }
    
    private void updateTable() throws SQLException{
        resetSelectedAppt();

        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        startColumn.setCellValueFactory(new PropertyValueFactory<>("start"));
        endColumn.setCellValueFactory(new PropertyValueFactory<>("end"));
        customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        
        appointmentTable.getItems().setAll(getAppointmentData());
    }
    
    public List<Appointment> getAppointmentData() throws SQLException{
        ObservableList<Appointment> apptList = FXCollections.observableArrayList();
        String apptQuery = "SELECT appointment.appointmentId, appointment.title, appointment.description, appointment.location, appointment.start, appointment.end, customer.customerId, customer.customerName, user.username, user.userId\n" +
                                "FROM appointment, customer, user\n" +
                                "WHERE appointment.customerId = customer.customerId\n" +
                                "AND appointment.createdBy = user.username\n"+
                                monthWeekQuery;
                                    
            PreparedStatement smt = DBConnection.getConn().prepareStatement(apptQuery);
            ResultSet appointmentsFound = smt.executeQuery();
            
            while (appointmentsFound.next()) {
                Integer appointmentId = appointmentsFound.getInt("appointment.appointmentId");
                int customerId = appointmentsFound.getInt("customer.customerId");
                String customer = appointmentsFound.getString("customer.customerName");
                String title = appointmentsFound.getString("appointment.title");
                String description = appointmentsFound.getString("appointment.description");
                String location = appointmentsFound.getString("appointment.location");  
                LocalDateTime start = appointmentsFound.getTimestamp("appointment.start").toLocalDateTime();
                LocalDateTime end  = appointmentsFound.getTimestamp("appointment.end").toLocalDateTime();
                String user = appointmentsFound.getString("user.userName");
                int userId = appointmentsFound.getInt("user.userId");
                apptList.add(new Appointment (appointmentId, customerId, customer,
                        title, description, location, start, end, userId, user));
                
    }
    return apptList;
}
    public static Appointment getSelectedAppt(){
        return apptModSelected;
    }
    
    public static void setSelectedAppt(Appointment a){
        apptModSelected = a;
    }
    
    public static void resetSelectedAppt(){
        apptModSelected = null;
    }
    
    private void setToggleGroups(){
        sortBytg = new ToggleGroup();
        this.monthrb.setToggleGroup(sortBytg);
        this.weekrb.setToggleGroup(sortBytg);
    }
    
    
}
