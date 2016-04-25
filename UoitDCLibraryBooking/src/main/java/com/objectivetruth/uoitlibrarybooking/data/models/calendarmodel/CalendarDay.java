package com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel;

import timber.log.Timber;

import java.util.ArrayList;

public class CalendarDay {
    public String extDayOfMonthNumber;
    public String extMonthWord;
    public String extEventArgument;
    public String extEventTarget;
    public ArrayList<TimeCell> timeCells;

    @Override
    public String toString() {
        try {
            String returnString =
                    "extDayOfMonthNumber: " + extDayOfMonthNumber + " (example: 25)" +
                    ", extMonthWord: " + extMonthWord + " (example: April)" +
                    ", extEventArgument: " + extEventArgument + " (example: ctl00$ContentPlaceHolder1$Calendar1)" +
                    ", extEventMonth: " + extEventTarget + " (example: 5959)";
            if(_notEmptyOrNull(timeCells)) {
                returnString += ", TimeCells: |";
                for(TimeCell timeCell: timeCells) {
                    returnString += timeCell.toString() + "|";
                }
            }
            return returnString;
        }catch(Exception e) {
            Timber.w(e, "Exception when calling toString");
            return "";
        }
    }

    private boolean _notEmptyOrNull(ArrayList<TimeCell> timeCells) {
        return (timeCells != null && timeCells.size() > 0);
    }
}
