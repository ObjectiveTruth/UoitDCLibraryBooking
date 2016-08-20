package com.objectivetruth.uoitlibrarybooking.data.models.bookinginteractionmodel;

import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.TimeCell;

public class BookinginteractionEventWithDateInfo  extends BookingInteractionEvent{
    public String dayOfMonthNumber;
    public String monthWord;

    public BookinginteractionEventWithDateInfo(TimeCell timeCell, BookingInteractionEventType type,
                                               String dayOfMonthNumber, String monthWord) {
        super(timeCell, type);
        this.dayOfMonthNumber = dayOfMonthNumber;
        this.monthWord = monthWord;
    }
}
