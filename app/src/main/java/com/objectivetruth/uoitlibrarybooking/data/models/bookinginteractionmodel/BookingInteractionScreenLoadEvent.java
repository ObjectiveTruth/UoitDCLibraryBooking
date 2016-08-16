package com.objectivetruth.uoitlibrarybooking.data.models.bookinginteractionmodel;

import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.TimeCell;

public class BookingInteractionScreenLoadEvent {
    public TimeCell timeCellInQuestion;
    public BookingInteractionEventType type;

    public BookingInteractionScreenLoadEvent(TimeCell timeCellInQuestion, BookingInteractionEventType type) {
        this.timeCellInQuestion = timeCellInQuestion;
        this.type = type;
    }
}
