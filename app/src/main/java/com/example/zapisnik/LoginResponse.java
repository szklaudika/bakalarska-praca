package com.example.zapisnik;

public class LoginResponse {
    private boolean success;
    private String message;
    private User user; // Contains user details such as id, username, etc.

    public boolean isSuccess() {
        return success;
    }
    public String getMessage() {
        return message;
    }
    public User getUser() {
        return user;
    }
}
