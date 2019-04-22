/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.time.LocalDateTime;

/**
 *
 * @author austinwise
 */
public class Appointment {
    private int appointmentId;
    private int customerId;
    private String customerName;
    private int userId;
    private String username;
    private String title;
    private String description;
    private String location;
    private String contact;
    private String type;
    private String url;
    private LocalDateTime start;
    private LocalDateTime end;
    private String apptCount;
    private String locationOrCustomer;
    private Customer customer;
    public Appointment(){}
    
    public Appointment(int appointmentId, int customerId, String customerName, String title, 
            String description, String location, LocalDateTime start, LocalDateTime end, int userId, String username){
    this.appointmentId = appointmentId;
    this.customerId = customerId;
    this.customerName = customerName;
    this.title = title;
    this.description = description;
    this.location = location;
    this.start = start;
    this.end = end;
    this.userId = userId;
    this.username = username;
}
    
    public Appointment(Customer customer, int appointmentId, int customerId, String customerName, String title, 
            String description, String location, LocalDateTime start, LocalDateTime end, int userId, String username){
    this.appointmentId = appointmentId;
    this.customerId = customerId;
    this.customerName = customerName;
    this.title = title;
    this.description = description;
    this.location = location;
    this.start = start;
    this.end = end;
    this.userId = userId;
    this.username = username;
    this.customer = customer;
}
    
    public Appointment(String location, String apptCount){
        this.location = location;
        this.apptCount = apptCount;
    }
    
    public Appointment(String customerName, String apptCount, int customerId){
        this.customerName = customerName;
        this.apptCount = apptCount;
        this.customerId = customerId;
    }
    
    public Appointment(String title, String location, LocalDateTime start, LocalDateTime end, String customerName){
        this.title = title;
        this.location = location;
        this.start = start;
        this.end = end;
        this.customerName = customerName;
    }
    
    public int getAppointmentId(){
        return appointmentId;
    }
    
    public void setAppointmentId(int appointmentId){
        this.appointmentId = appointmentId;
    }
    
    public int getCustomerId(){
        return customerId;
    }
    
    public void setCustomerId(int customerId){
        this.customerId = customerId;
    }
    
    public int getUserId(){
        return userId;
    }
    
    public void setUserId(int userId){
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }
    
    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getApptCount() {
        return apptCount;
    }

    public void setApptCount(String apptCount) {
        this.apptCount = apptCount;
    }

    public String getLocationOrCustomer() {
        return locationOrCustomer;
    }

    public void setLocationOrCustomer(String locationOrCustomer) {
        this.locationOrCustomer = locationOrCustomer;
    }
    
}
