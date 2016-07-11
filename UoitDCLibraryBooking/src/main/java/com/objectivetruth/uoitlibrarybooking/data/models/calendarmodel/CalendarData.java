package com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel;

import java.util.List;

public class CalendarData {
    public List<CalendarDay> days;
    public int computedHashCode;

    @Override
    public String toString() {
        String returnString = "days:";

        if(days == null) {
            returnString += " NULL";
            return returnString;
        }

        if(_notEmpty(days)) {
            int i = 0;
            for(CalendarDay day: days) {
                if(notNull(day)){
                    returnString += " (" + i + ")" + day.toString();
                }else{
                    returnString += " (" + i + ")NULL";
                }
                i++;
            }
        }else{
            returnString += " EMPTY";
        }
        return returnString;
    }

    private boolean notNull(Object object) {
        return (object != null);
    }

    private boolean _notEmpty(List<CalendarDay> calendarDays) {
        return !calendarDays.isEmpty();
    }

    public boolean isEqualTo(CalendarData calendarData) {
        return this.computedHashCode == calendarData.computedHashCode;
    }

    public boolean isNOTEqualTo(CalendarData calendarData) {
        return !isEqualTo(calendarData);
    }
}
