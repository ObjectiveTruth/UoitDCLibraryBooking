package com.objectivetruth.uoitlibrarybooking.data.models.usermodel;

import java.util.ArrayList;

public class UserData {
    public String errorMessage;
    public ArrayList<MyAccountBooking> pastBookings;
    public ArrayList<MyAccountBooking> completeBookings;
    public ArrayList<MyAccountBooking> incompleteBookings;

    @Override
    public String toString() {
        String returnString = "";
        returnString += "errorMessage: ";
        if(notNull(errorMessage)) {returnString += errorMessage + ", ";} else {returnString += "NULL, ";}

        returnString += "pastBookings:";
        if(pastBookings == null) {
            returnString += " NULL";
        }else{
            if(_notEmpty(pastBookings)) {
                int i = 0;
                for(MyAccountBooking booking: pastBookings) {
                    if(notNull(booking)){
                        returnString += " (" + i + ")" + booking.toString();
                    }else{
                        returnString += " (" + i + ")NULL";
                    }
                    i++;
                }
            }else{
                returnString += " EMPTY";
            }
        }

        returnString += ", completeBookings:";
        if(completeBookings == null) {
            returnString += " NULL";
        }else{
            if(_notEmpty(completeBookings)) {
                int i = 0;
                for(MyAccountBooking booking: completeBookings) {
                    if(notNull(booking)){
                        returnString += " (" + i + ")" + booking.toString();
                    }else{
                        returnString += " (" + i + ")NULL";
                    }
                    i++;
                }
            }else{
                returnString += " EMPTY";
            }
        }

        returnString += ", incompleteBookings:";
        if(incompleteBookings == null) {
            returnString += " NULL";
        }else{
            if(_notEmpty(incompleteBookings)) {
                int i = 0;
                for(MyAccountBooking booking: incompleteBookings) {
                    if(notNull(booking)){
                        returnString += " (" + i + ")" + booking.toString();
                    }else{
                        returnString += " (" + i + ")NULL";
                    }
                    i++;
                }
            }else{
                returnString += " EMPTY";
            }
        }
        return returnString;
    }

    private boolean notNull(Object object) {
        return (object != null);
    }

    private boolean _notEmpty(ArrayList<MyAccountBooking> arrayList) {
        return !arrayList.isEmpty();
    }
}
