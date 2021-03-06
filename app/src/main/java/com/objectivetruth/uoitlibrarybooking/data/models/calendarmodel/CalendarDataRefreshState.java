package com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel;

/**
 * Represents the last state of the refreshing subject. Will be passed through the Behaviour Subject
 * If the {@link CalendarDataRefreshStateType} is
 * type = SUCCESS, calendarData = *null*, then the request was successful, but there's no days available
 * type = SUCCESS, calendarData = *something*, then the request was successful, and calendarData will be not null
 * type = INITIAL, calendarData = *null*, then the request was successful, but there's no days available
 * type = INITIAL, calendarData = *something*, then the request was successful, and calendarData will be not null
 * type = ERROR,   calendarData = *something*, exception = *exception*,
 * there was an error and exception will contain more details as to why, calendarDatawill have something or null
 * type = RUNNING, calendarData = *something*, there is a refresh request in flight, but attached the latest true
 * calendar or null
 */
public class CalendarDataRefreshState {
    public CalendarDataRefreshStateType type;
    public Throwable exception;
    public CalendarData calendarData;

    public CalendarDataRefreshState(CalendarDataRefreshStateType calendarDataRefreshStateType,
                                    CalendarData calendarData,
                                    Throwable exception) {
        this.type = calendarDataRefreshStateType;
        this.exception = exception;
        this.calendarData = calendarData;
    }
}
