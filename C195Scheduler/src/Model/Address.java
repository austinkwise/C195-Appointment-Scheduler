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
public class Address {
    private int addressId;
    private String address;
    private String address2;
    private int cityId;
    private String postalCode;
    private String phone;
    
    public Address(){}
    
    public Address(int addressId) {
        this.addressId = addressId;
    }

    public Address(String address, String address2, int cityId, String postalCode, String phone) {
        this.address = address;
        this.address2 = address2;
        this.cityId = cityId;
        this.postalCode = postalCode;
        this.phone = phone;
    }
    
    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public static Address validateAddress(String address1, String address2b, int city1, 
             String postal1, String phone1) throws SQLException {
        Address address2 = new Address();          
        PreparedStatement pst = DBConnection.getConn().prepareStatement("SELECT * FROM address "
                        + "WHERE address.address=? AND address.cityId=? "
                        + "AND address.postalCode =? AND address.phone = ? "
                        + "AND address.address2=? ");
        pst.setString(1, address1); 
        pst.setInt(2, city1); 
        pst.setString(3, postal1); 
        pst.setString(4, phone1); 
        pst.setString(5, address2b);
        ResultSet rs = pst.executeQuery();                        
            if(rs.next()){
            
            address2.setAddressId(rs.getInt("addressId"));
            address2.setAddress(rs.getString("address"));
            address2.setAddress2(rs.getString("address2"));
            address2.setCityId(rs.getInt("cityId"));
            address2.setPostalCode(rs.getString("postalCode"));
            address2.setPhone(rs.getString("phone"));
                System.out.println("some data found");
            }
            else {
                System.out.println("no data found");
                return null;   
            } 
        System.out.println("address2 works");
        return address2;
    }
}
