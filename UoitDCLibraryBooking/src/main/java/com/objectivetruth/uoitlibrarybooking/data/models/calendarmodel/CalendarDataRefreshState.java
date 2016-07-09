package com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel;

/**
 * Represents the last state of the refreshing subject. Will be passed through the Behaviour Subject
 */
public class CalendarDataRefreshState {
    public CalendarDataRefreshStateType type;
    public Exception exception;
    public CalendarData calendarData;

    public CalendarDataRefreshState(CalendarDataRefreshStateType calendarDataRefreshStateType,
                                    CalendarData calendarData,
                                    Exception exception) {
        this.type = calendarDataRefreshStateType;
        this.exception = exception;
        this.calendarData = calendarData;
    }
}
