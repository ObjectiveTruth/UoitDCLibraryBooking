package com.objectivetruth.uoitlibrarybooking.data.models.bookinginteractionmodel;

import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.TimeCell;

public class BookingInteractionEvent {
    public TimeCell timeCell;
    public BookingInteractionEventType type;

    public BookingInteractionEvent(TimeCell timeCell, BookingInteractionEventType type) {
        this.timeCell = timeCell;
        this.type = type;
    }
}
