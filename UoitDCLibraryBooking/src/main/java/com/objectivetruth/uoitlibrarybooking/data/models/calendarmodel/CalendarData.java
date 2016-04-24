package com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel;

import java.util.List;

public class CalendarData {
    public List<CalendarDay> days;
    public String viewstatemain;
    public String eventvalidation;
    public String viewstategenerator;

    @Override
    public String toString() {
        try{
            String returnString = "viewstatemain: " + viewstatemain +
                    ", eventvalidation: " + eventvalidation +
                    ", viewstategenerator: " + viewstategenerator;

            for(CalendarDay day: days) {
                returnString += ", " + day.toString();
            }
            return returnString;
        }catch(Exception e) {
            return "";
        }
    }
}
