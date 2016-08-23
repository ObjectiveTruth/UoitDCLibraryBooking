package com.objectivetruth.uoitlibrarybooking.data.models.bookinginteractionmodel;

/**
 * Types of events that can fire from the booking interaction section of the app
 */
public enum BookingInteractionEventType {
    /**
     * When Booking is requested
     */
    BOOK,

    /**
     * An Error occured trying to do a book request
     */
    BOOK_ERROR,

    /**
     * When the user clicks on a booking that is being competed for, they can join it or leave
     */
    JOIN_OR_LEAVE,

    /**
     * When an interaction flow succeeds
     */
    SUCCESS,

    /**
     * Incase a cell contains unknown information, should error gracefully if this happens
     */
    UNKNOWN
}
