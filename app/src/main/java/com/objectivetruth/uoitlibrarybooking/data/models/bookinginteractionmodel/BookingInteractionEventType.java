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
     * When the user clicks on a booking that is being competed for
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
