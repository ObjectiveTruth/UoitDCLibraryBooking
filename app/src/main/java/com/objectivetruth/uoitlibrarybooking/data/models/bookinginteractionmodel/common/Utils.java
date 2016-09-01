package com.objectivetruth.uoitlibrarybooking.data.models.bookinginteractionmodel.common;

import com.objectivetruth.uoitlibrarybooking.data.models.bookinginteractionmodel.BookingInteractionEventType;
import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.TimeCell;

public class Utils {
    /**
     * Returns the event type based on the timecell provided. This is useful for designating what event corresponds
     * to what timecell type
     * @param timeCell
     * @return
     */
    public static BookingInteractionEventType getBookingInteractionEventTypeBasedOnTimeCell(TimeCell timeCell) {
        switch(timeCell.param_next) {
            case "book.aspx":
                return BookingInteractionEventType.BOOK;
            case "joinorleave.aspx":
                return BookingInteractionEventType.JOIN_OR_LEAVE;
            case "viewleaveorjoin.aspx":
                return BookingInteractionEventType.VIEWLEAVEORJOIN;
            default:
                return BookingInteractionEventType.UNKNOWN;
        }
    }
}
