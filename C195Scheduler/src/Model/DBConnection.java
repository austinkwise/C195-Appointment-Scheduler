/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *Server name:52.206.157.109
 * Database name: U061Zi
 * Username: U061Zi
 * Password: 53688672027
 * 
 * @author austinwise
 */
public class DBConnection {
    
    
    private static final String databaseName = "U061Zi";
    private static final String DB_URL = "jdbc:mysql://52.206.157.109/" + databaseName;
    private static final String user = "U061Zi";
    private static final String pass = "53688672027";
    private static final String driver = "com.mysql.jdbc.Driver";
    
    private static Connection conn;
    
    public static void initialize() throws ClassNotFoundException, SQLException{
        Class.forName(driver);
        conn = DriverManager.getConnection(DB_URL, user, pass);
    }
        
    public static Connection getConn(){
        return conn;
    }
    
    public static void closeConn() throws SQLException{
        conn.close();
    }
}
