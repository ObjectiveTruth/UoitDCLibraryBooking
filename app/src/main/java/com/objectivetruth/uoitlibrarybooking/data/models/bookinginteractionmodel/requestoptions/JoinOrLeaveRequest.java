package com.objectivetruth.uoitlibrarybooking.data.models.bookinginteractionmodel.requestoptions;

import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.CalendarDay;
import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.TimeCell;

public class JoinOrLeaveRequest implements RequestOptions{
    public CalendarDay calendarDay;
    public String groupValue;
    public String groupLabel;
    public TimeCell timeCell;

    public JoinOrLeaveRequest(CalendarDay calendarDay, String groupLabel, String groupValue,
                                   TimeCell timeCell) {
        this.calendarDay = calendarDay;
        this.groupValue = groupValue;
        this.groupLabel = groupLabel;
        this.timeCell = timeCell;
    }
}
