/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import com.mysql.cj.conf.IntegerProperty;
import com.mysql.cj.conf.StringProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 *
 * @author austinwise
 */
public class Customer {
    private int customerId;
    private String name;
    private String address;
    private String address2;
    private String postalCode;
    private String phone;
    private String city;
    private String country;
    
    //Constructor
    public Customer(){}

    public Customer(int customerId, String customerName, String address, String address2, String city, String country, String postalCode, String phone) {
        this.customerId = customerId;
        this.name = customerName;
        this.address = address;
        this.address2 = address2;
        this.city = city;
        this.country = country;
        this.postalCode = postalCode;
        this.phone = phone;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return name;
    }

    public void setCustomerName(String customerName) {
        this.name = customerName;
    }
    
    public String getAddress() {
        return address;
    }

    public void setAddress(String address){
        this.address = address;
    }
    
    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode){
        this.postalCode = postalCode;
    }
    
    public String getAddress2() {
        return address2;
    }
    
    public void setAddress2(String address){
        this.address2 = address2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }    
}
