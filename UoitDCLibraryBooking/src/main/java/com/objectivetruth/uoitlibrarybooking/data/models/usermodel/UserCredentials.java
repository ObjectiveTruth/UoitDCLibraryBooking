package com.objectivetruth.uoitlibrarybooking.data.models.usermodel;

public class UserCredentials {
    String username;
    String password;
    String institutionId;

    public UserCredentials(String username, String password, String institutionId) {
        this.institutionId = institutionId;
        this.password = password;
        this.username = username;
    }
}

