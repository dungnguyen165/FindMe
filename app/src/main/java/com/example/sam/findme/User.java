package com.example.sam.findme;

/**
 * Created by Sam on 12/3/2017.
 */

public class User {
    private String email, status;

    public User() {
    }

    public User(String email, String status) {
        this.email = email;
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
