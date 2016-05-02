package com.objectivetruth.uoitlibrarybooking.data.models.usermodel;

public class UserCredentials {
    public String username;
    public String password;
    public String institutionId;
    public String viewState;
    public String eventValidation;
    public String viewStateGenerator;

    public UserCredentials(String username, String password, String institutionId) {
        this.institutionId = institutionId;
        this.password = password;
        this.username = username;
    }
}

