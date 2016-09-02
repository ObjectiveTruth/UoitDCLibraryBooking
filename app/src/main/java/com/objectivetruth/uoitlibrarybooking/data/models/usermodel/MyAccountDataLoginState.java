package com.objectivetruth.uoitlibrarybooking.data.models.usermodel;

public class MyAccountDataLoginState {
    public MyAccountDataLoginStateType type;
    public Throwable exception;
    public UserData userData;

    public MyAccountDataLoginState(MyAccountDataLoginStateType type, UserData userData, Throwable exception) {
        this.type = type;
        this.userData = userData;
        this.exception = exception;
    }

    @Override
    public String toString() {
        return "MyAccountDataLoginState{" +
                "type=" + type +
                ", exception=" + exception +
                ", userData=" + userData +
                '}';
    }
}
