package com.objectivetruth.uoitlibrarybooking.data.models.bookinginteractionmodel;

import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.CalendarDay;
import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.TimeCell;
import com.objectivetruth.uoitlibrarybooking.statelessutilities.Triple;

import java.util.HashMap;

public class BookingInteractionEvent {
    public TimeCell timeCell;
    public BookingInteractionEventType type;
    public String dayOfMonthNumber;
    public String monthWord;
    public String message;
    public Triple<HashMap<String, String>, HashMap<String, String>, CalendarDay> joinOrLeaveGetSpinnerResult;

    public BookingInteractionEvent(TimeCell timeCell, BookingInteractionEventType type,
                                   String dayOfMonthNumber, String monthWord) {
        this.timeCell = timeCell;
        this.type = type;
        this.dayOfMonthNumber = dayOfMonthNumber;
        this.monthWord = monthWord;
    }

    @Override
    public String toString() {
        return "BookingInteractionEvent{" +
                "timeCell=" + timeCell +
                ", type=" + type +
                ", dayOfMonthNumber='" + dayOfMonthNumber + '\'' +
                ", monthWord='" + monthWord + '\'' +
                ", message='" + message + '\'' +
                ", joinOrLeaveGetSpinnerResult=" + joinOrLeaveGetSpinnerResult +
                '}';
    }
}
