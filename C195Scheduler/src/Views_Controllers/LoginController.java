/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Views_Controllers;

import Model.DBConnection;
import Model.User;
import c195scheduler.C195Scheduler;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 *
 * @author austinwise
 */
public class LoginController {
    @FXML private TextField usernametf;
    @FXML private PasswordField passwordpf;
    @FXML private Label errorMessage;
    @FXML private Label titleLabel;
    @FXML private Label usernameLabel;
    @FXML private Label passwordLabel;
    @FXML private Button loginButton;
    @FXML private Button exitButton;
    
    private Connection conn = DBConnection.getConn();
    private C195Scheduler mainApp;
    private User user = new User();
    ResourceBundle rb = ResourceBundle.getBundle("Resources/login", Locale.getDefault());
    
    public void setLogin(C195Scheduler mainApp){
        this.mainApp = mainApp;
        
        titleLabel.setText(rb.getString("title"));
        usernameLabel.setText(rb.getString("username"));
        passwordLabel.setText(rb.getString("password"));
        loginButton.setText(rb.getString("login"));
        exitButton.setText(rb.getString("exit"));
    }
    
   @FXML private void loginClick(ActionEvent e) throws IOException, SQLException{
       String username = usernametf.getText();
       String password = passwordpf.getText();
       
       if(username.length() == 0 || password.length() == 0){
           errorMessage.setText(rb.getString("empty"));
       }
       else{
           User user = validateUser(username, password);
       if (user == null){
           errorMessage.setText(rb.getString("incorrect"));
           return;
       }
       }
       apptReminder();
       trackLogins();
       mainApp.showMain(user);
   } 
   
   @FXML private void exitClick(ActionEvent e){
       Platform.exit();
   }
   
   private void apptReminder() throws SQLException{
       String reminderQuery = "SELECT appointment.*"
                         + " FROM appointment"
                         + " WHERE (appointment.createdBy = ? AND appointment.start BETWEEN ? AND ?)";
             PreparedStatement statement = conn.prepareStatement(reminderQuery);
             int i = 1;
             statement.setString (i++, user.getUsername());
             statement.setTimestamp(i++, Timestamp.valueOf(LocalDateTime.now()));
             statement.setTimestamp(i++, Timestamp.valueOf(LocalDateTime.now()
                     .plusMinutes(15)));
             ResultSet rs = statement.executeQuery();
             if (rs.next()) {
                 Alert upcomingAlert = new Alert(Alert.AlertType.INFORMATION);
                 upcomingAlert.setTitle("Don't be late yah filthy animal");
                 upcomingAlert.setHeaderText("ALERT: Appointment soon");
                 Long minutesUntil = ((rs.getTimestamp("start").getTime())-
                         Timestamp.valueOf(LocalDateTime.now()
                         ).getTime())/60000;
                 upcomingAlert.setContentText("You have an appointment soon: " 
                         + rs.getString("title"));
                 upcomingAlert.showAndWait();
            }
       }

   private void trackLogins() throws UnsupportedEncodingException, IOException{
       File loginTracker = new File("loginTracker.txt");
       
       if(!loginTracker.exists()){
                    try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream("loginTracker.txt"), "utf-8"))) 
                    {
                            writer.write(user.getUsername() + " signed in at " + Calendar.getInstance().getTime() +  "\n");
                    }
                }        
                else {
                    try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream("loginTracker.txt", true), "utf-8"))) 
                    {
                            writer.write(user.getUsername() + " signed in at " + Calendar.getInstance().getTime() + "\n");
                    }
                }
   }
   
   private User validateUser(String username, String password) throws SQLException{
       PreparedStatement pst = DBConnection.getConn().prepareStatement
                ("SELECT * FROM user WHERE userName=? AND password =?");
            pst.setString(1, username);
            pst.setString(2, password);
            ResultSet rs = pst.executeQuery();
            if (rs.next()){
                String passwordHolder = rs.getString("password");
                if (!passwordHolder.contentEquals(password)){
                    return null;
                }
                user.setUsername(rs.getString("userName"));
                user.setPassword(rs.getString("password"));
                user.setUserId(rs.getInt("userId"));
              }
            else{
                return null;
            }
            return user;
   }
}
