package org.example.hikvisionintegration.dto;

public class LoginResponse {
    private String status; // Masalan: "PENDING_FACE_ID", "SUCCESS", "FAILED"
    private String message;
    private String token; // Muvaffaqiyatli bo'lsa
    // Constructor, Getters and Setters
    public LoginResponse(String status, String message, String token) {
        this.status = status;
        this.message = message;
        this.token = token;
    }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}