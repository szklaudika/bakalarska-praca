package com.example.zapisnik;

public class RegisterResponse {
    private String status;
    private String message;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return "success".equalsIgnoreCase(status);
    }
}
