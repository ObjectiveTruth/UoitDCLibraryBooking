package com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel;

public class CalendarDay {
    public String extDayOfMonthNumber;
    public String extMonthWord;
    public String extEventArgument;
    public String extEventMonth;

    @Override
    public String toString() {
        try {
            return "extDayOfMonthNumber: " + extDayOfMonthNumber +
                    ", extMonthWord: " + extMonthWord +
                    ", extEventArgument: " + extEventArgument +
                    ", extEventMonth" + extEventMonth;
        }catch(Exception e) {
            return "";
        }
    }
}
