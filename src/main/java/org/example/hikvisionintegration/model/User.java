package org.example.hikvisionintegration.model;

public class User {
    private String username;
    private String password;
    private String employeeNo; // Hikvision'dagi xodim raqami

    public User(String username, String password, String employeeNo) {
        this.username = username;
        this.password = password;
        this.employeeNo = employeeNo;
    }

    // Getters
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getEmployeeNo() { return employeeNo; }
}