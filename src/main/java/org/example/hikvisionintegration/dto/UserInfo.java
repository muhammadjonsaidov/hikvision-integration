package org.example.hikvisionintegration.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "UserInfo")
public class UserInfo {

    @JacksonXmlProperty(localName = "employeeNo")
    private String employeeNo;

    @JacksonXmlProperty(localName = "name")
    private String name;

    @JacksonXmlProperty(localName = "userType")
    private String userType = "normal"; // "normal" yoki "visitor" bo'lishi mumkin

    // Getter va Setterlar
    public String getEmployeeNo() { return employeeNo; }
    public void setEmployeeNo(String employeeNo) { this.employeeNo = employeeNo; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }
}