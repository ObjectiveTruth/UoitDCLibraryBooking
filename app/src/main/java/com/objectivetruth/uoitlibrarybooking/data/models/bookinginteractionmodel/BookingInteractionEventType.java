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
     * When the user clicks on a booking that is being competed for, they can join it or leave
     */
    JOIN_OR_LEAVE,

    /**
     * When an interaction flow succeeds
     */
    SUCCESS,

    /**
     * An Error occured trying to do a request
     */
    ERROR,

    /**
     * Incase a cell contains unknown information, should error gracefully if this happens
     */
    UNKNOWN
}
