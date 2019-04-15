/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author austinwise
 */
public class Country {
    private int countryId;
    private String country;


    public Country() {}

    public int getCountryId() {
        return countryId;
    }

    public void setCountryId(int countryId) {
        this.countryId = countryId;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
    
    public static Country validateCountry(String country1) throws SQLException{
        Country country2 = new Country();
        PreparedStatement pst = DBConnection.getConn().prepareStatement("SELECT * FROM country WHERE country=?");
            pst.setString(1, country1); 
            ResultSet rs = pst.executeQuery();                        
            if(rs.next()){
                country2.setCountry(rs.getString("country"));
                country2.setCountryId(rs.getInt("countryId"));
            } else {
                return null;    
            }
            return country2;
    }
}
