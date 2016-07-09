package com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel;

public enum CalendarDataRefreshStateType {
    /**
     * When the app first starts, this is the initial or default state
     */
    INITIAL,

    /**
     * If an error happens during refresh, check the other fields for more info on the exact error
     */
    ERROR,

    /**
     * Refresh suceeded and will be in the payload
     */
    COMPLETED
}
