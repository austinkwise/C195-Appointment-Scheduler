/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Views_Controllers;

import Model.DBConnection;
import Model.User;
import c195scheduler.C195Scheduler;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 *
 * @author austinwise
 */
public class LoginController {
    @FXML private TextField usernametf;
    @FXML private PasswordField passwordpf;
    
    private Connection conn = DBConnection.getConn();
    private C195Scheduler mainApp;
    private User user = new User();
    
    public void setLogin(C195Scheduler mainApp){
        this.mainApp = mainApp;
    }
    
   @FXML private void loginClick(ActionEvent e) throws IOException, SQLException{
       String username = usernametf.getText();
       String password = passwordpf.getText();
       
       User user = validateUser(username, password);
       if (user == null){
           return;
       }
       
       mainApp.showMain(user);
   } 
   
   @FXML private void exitClick(ActionEvent e){
       Platform.exit();
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
