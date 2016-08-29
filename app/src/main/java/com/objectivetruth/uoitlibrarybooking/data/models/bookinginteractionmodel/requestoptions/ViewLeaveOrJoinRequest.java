package com.objectivetruth.uoitlibrarybooking.data.models.bookinginteractionmodel.requestoptions;

import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.CalendarDay;
import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.TimeCell;

public class ViewLeaveOrJoinRequest implements RequestOptions{
    public CalendarDay calendarDay;
    public String groupValue;
    public TimeCell timeCell;

    public ViewLeaveOrJoinRequest(CalendarDay calendarDay, String groupValue, TimeCell timeCell) {
        this.calendarDay = calendarDay;
        this.groupValue = groupValue;
        this.timeCell = timeCell;
    }
}
