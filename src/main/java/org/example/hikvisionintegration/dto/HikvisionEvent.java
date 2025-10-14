package org.example.hikvisionintegration.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "EventNotificationAlert")
public class HikvisionEvent {

    @JacksonXmlProperty(localName = "employeeNoString")
    private String employeeNoString;

    // Getters and Setters
    public String getEmployeeNoString() {
        return employeeNoString;
    }

    public void setEmployeeNoString(String employeeNoString) {
        this.employeeNoString = employeeNoString;
    }

    @Override
    public String toString() {
        return "HikvisionEvent{" +
                "employeeNoString='" + employeeNoString + '\'' +
                '}';
    }
}