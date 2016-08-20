package com.objectivetruth.uoitlibrarybooking.data.models.bookinginteractionmodel;

import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.TimeCell;

public class BookingInteractionScreenLoadEvent extends BookinginteractionEventWithDateInfo{

    public BookingInteractionScreenLoadEvent(TimeCell timeCell, BookingInteractionEventType type, String dayOfMonthNumber, String monthWord) {
        super(timeCell, type, dayOfMonthNumber, monthWord);
    }
}
