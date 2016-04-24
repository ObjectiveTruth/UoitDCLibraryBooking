package com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel;

public class CalendarDay {
    public String extDayOfMonthNumber;
    public String extMonthWord;
    public String extEventArgument;
    public String extEventTarget;

    @Override
    public String toString() {
        try {
            return "extDayOfMonthNumber: " + extDayOfMonthNumber + " (example: 25)" +
                    ", extMonthWord: " + extMonthWord + " (example: April)" +
                    ", extEventArgument: " + extEventArgument + " (example: ctl00$ContentPlaceHolder1$Calendar1)" +
                    ", extEventMonth: " + extEventTarget + " (example: 5959)";
        }catch(Exception e) {
            return "";
        }
    }
}
