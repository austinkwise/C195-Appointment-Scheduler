package Views_Controllers;

import Model.User;
import c195scheduler.C195Scheduler;
import java.io.IOException;
import java.sql.SQLException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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
    
    public void setMain(C195Scheduler mainApp, User activeUser){
        this.mainApp = mainApp;
        this.currentUser = activeUser;
    }
    
    @FXML private void logoutClick(ActionEvent e) throws IOException{
        mainApp.showLoginScreen();
    }

    @FXML private void customersClick(ActionEvent e) throws IOException, SQLException{
        mainApp.showCustomerScreen(currentUser);
    }
}
