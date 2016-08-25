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
     * A request is in flight to do a booking
     */
    BOOK_RUNNING,

    /**
     * When the user clicks on a booking that is being competed for, this loads the initial form that must be filled
     * with data that is sent asynchronously and while that's going on, this will be {@<code>JOIN_OR_LEAVE_RUNNING_GETTING_SPINNER_VALUES</code>}
     */
    JOIN_OR_LEAVE,
    /**
     * When request is in flight for the spinner values
     */
    JOIN_OR_LEAVE_GETTING_SPINNER_VALUES_RUNNING,
    /**
     * When the request for the spinner values errors out
     */
    JOIN_OR_LEAVE_GETTING_SPINNER_VALUES_ERROR,
    /**
     * When the request for the spinner values finishes successfully
     */
    JOIN_OR_LEAVE_GETTING_SPINNER_VALUES_SUCCESS,
    /**
     * When the request to actually carry out the leave request is running
     */
    JOIN_OR_LEAVE_LEAVE_RUNNING,
    /**
     * When the request to actually carry out the leave request errors
     */
    JOIN_OR_LEAVE_LEAVE_ERROR,
    /**
     * When the request to actually carry out the leave request succeeds
     */
    JOIN_OR_LEAVE_LEAVE_SUCCESS,

    /**
     * When an interaction flow succeeds
     */
    SUCCESS,

    /**
     * Incase a cell contains unknown information, should error gracefully if this happens
     */
    UNKNOWN
}
