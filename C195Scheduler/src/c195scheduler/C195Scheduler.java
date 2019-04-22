/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c195scheduler;

import Model.DBConnection;
import Model.User;
import Views_Controllers.CustomersController;
import Views_Controllers.LoginController;
import Views_Controllers.MainController;
import Views_Controllers.NewApptController;
import Views_Controllers.ReportsController;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 *
 * @author austinwise
 */
public class C195Scheduler extends Application {
    
    private Stage primaryStage;
    private static Connection conn;
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        
        showLoginScreen();
    }

    public void showLoginScreen() throws IOException{
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(C195Scheduler.class.getResource("/Views_Controllers/Login.fxml"));
        AnchorPane loginScreen = (AnchorPane) loader.load();
        
        LoginController controller = loader.getController();
        controller.setLogin(this);
        
        Scene scene = new Scene(loginScreen);
        primaryStage.setScene(scene);
        
        primaryStage.show();
    }
    
    public void showMain(User activeUser) throws IOException, SQLException{
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/Views_Controllers/Main.fxml"));
        AnchorPane main = (AnchorPane) loader.load();
        
        Scene scene = new Scene(main);
        primaryStage.setScene(scene);
        
        MainController controller = loader.getController();
        controller.setMain(this, activeUser);
        
        primaryStage.show();
    }
  
    public void showCustomerScreen(User activeUser) throws IOException, SQLException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/Views_Controllers/Customers.fxml"));
        AnchorPane custScreen = (AnchorPane) loader.load();
        
        Scene scene = new Scene(custScreen);
        primaryStage.setScene(scene);
        
        CustomersController controller = loader.getController();
        controller.setCustomerScreen(this, activeUser);
        
        primaryStage.show();
    }
    
    public void showNewApptScreen(User activeUser) throws IOException, SQLException{
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/Views_Controllers/NewAppt.fxml"));
        AnchorPane custScreen = (AnchorPane) loader.load();
        
        Scene scene = new Scene(custScreen);
        primaryStage.setScene(scene);
        
        NewApptController controller = loader.getController();
        controller.setNewApptScreen(this, activeUser);
        
        primaryStage.show();
    }
    
    public void showReports(User activeUser) throws IOException, SQLException{
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/Views_Controllers/Reports.fxml"));
        AnchorPane custScreen = (AnchorPane) loader.load();
        
        Scene scene = new Scene(custScreen);
        primaryStage.setScene(scene);
        
        ReportsController controller = loader.getController();
        controller.setReport(this, activeUser);
        
        primaryStage.show();
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        DBConnection.initialize();
        conn = DBConnection.getConn();
        
        launch(args);
        DBConnection.closeConn();
    }
}
