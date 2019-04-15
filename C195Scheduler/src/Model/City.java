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
public class City {
    private int cityId;
    private String city;
    private int countryId;
    
    public City(){}
    
    public City(int CityId){
        this.cityId = cityId;
    }
    
    public City(int cityId, String city, int countryId) {
        this.cityId = cityId;
        this.city = city;
        this.countryId = countryId;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getCountryId() {
        return countryId;
    }

    public void setCountryId(int countryId) {
        this.countryId = countryId;
    }
    
    public static City validateCity(String city1, Integer country) throws SQLException {
        City city2 = new City();          
            PreparedStatement pst = DBConnection.getConn().prepareStatement("SELECT * FROM city WHERE city=? AND countryId=?");
            pst.setString(1, city1); 
            pst.setInt(2, country); 
            ResultSet rs = pst.executeQuery();                        
            if(rs.next()){
                city2.setCityId(rs.getInt("cityId"));
                city2.setCity(rs.getString("city"));
                city2.setCountryId(rs.getInt("countryId"));
             
            } else {
                return null;    
            }            
        return city2;
    } 
}
