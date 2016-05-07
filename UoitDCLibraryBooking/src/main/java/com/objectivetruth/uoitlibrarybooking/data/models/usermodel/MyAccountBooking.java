package com.objectivetruth.uoitlibrarybooking.data.models.usermodel;

public class MyAccountBooking {
    public String room;
    public String date;
    public String startTime;
    public String endTime;

    @Override
    public String toString() {
        String returnString = "";
        returnString += "room: ";
        if(notNull(room)) {returnString += room + ", ";} else {returnString += "NULL, ";}

        returnString += "date: ";
        if(notNull(date)) {returnString += date + ", ";} else {returnString += "NULL, ";}

        returnString += "startTime: ";
        if(notNull(startTime)) {returnString += startTime + ", ";} else {returnString += "NULL, ";}

        returnString += "endTime: ";
        if(notNull(endTime)) {returnString += endTime;} else {returnString += "NULL";}

        return returnString;
    }

    private boolean notNull(Object object) {
        return (object != null);
    }
}
