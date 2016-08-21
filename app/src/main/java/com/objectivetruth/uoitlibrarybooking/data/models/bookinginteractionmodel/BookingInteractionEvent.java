package com.objectivetruth.uoitlibrarybooking.data.models.bookinginteractionmodel;

import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.TimeCell;

public class BookingInteractionEvent {
    public TimeCell timeCell;
    public BookingInteractionEventType type;
    public String dayOfMonthNumber;
    public String monthWord;

    public BookingInteractionEvent(TimeCell timeCell, BookingInteractionEventType type,
                                   String dayOfMonthNumber, String monthWord) {
        this.timeCell = timeCell;
        this.type = type;
        this.dayOfMonthNumber = dayOfMonthNumber;
        this.monthWord = monthWord;
    }
}
