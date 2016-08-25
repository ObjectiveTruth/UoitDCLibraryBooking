package com.objectivetruth.uoitlibrarybooking.data.models.bookinginteractionmodel.requestoptions;

import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.CalendarDay;
import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.TimeCell;

public class JoinOrLeaveLeaveRequest implements RequestOptions{
    public CalendarDay calendarDay;
    public String leaveGroupValue;
    public String leaveGroupLabel;
    public TimeCell timeCell;

    public JoinOrLeaveLeaveRequest(CalendarDay calendarDay, String leaveGroupLabel, String leaveGroupValue,
                                   TimeCell timeCell) {
        this.calendarDay = calendarDay;
        this.leaveGroupValue = leaveGroupValue;
        this.leaveGroupLabel = leaveGroupLabel;
        this.timeCell = timeCell;
    }
}
