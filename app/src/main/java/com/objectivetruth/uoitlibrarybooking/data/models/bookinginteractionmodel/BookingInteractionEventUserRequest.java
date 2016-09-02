package com.objectivetruth.uoitlibrarybooking.data.models.bookinginteractionmodel;

import com.objectivetruth.uoitlibrarybooking.data.models.bookinginteractionmodel.requestoptions.RequestOptions;
import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.TimeCell;

public class BookingInteractionEventUserRequest {
    public TimeCell timeCell;
    public BookingInteractionEventUserRequestType type;
    public String dayOfMonthNumber;
    public String monthWord;
    public RequestOptions requestOptions;

    public BookingInteractionEventUserRequest(TimeCell timeCell,
                                              BookingInteractionEventUserRequestType type,
                                              String dayOfMonthNumber,
                                              String monthWord,
                                              RequestOptions requestOptions) {
        this.timeCell = timeCell;
        this.type = type;
        this.dayOfMonthNumber = dayOfMonthNumber;
        this.monthWord = monthWord;
        this.requestOptions = requestOptions;
    }
}
