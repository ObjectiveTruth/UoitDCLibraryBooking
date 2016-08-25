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
     * States for joining a room in the JOINORLEAVE flow
     */
    JOIN_OR_LEAVE_LEAVE_RUNNING,
    JOIN_OR_LEAVE_LEAVE_ERROR,
    JOIN_OR_LEAVE_LEAVE_SUCCESS,

    /**
     * States for leaving a room in the JOINORLEAVE flow
     */
    JOIN_OR_LEAVE_JOIN_RUNNING,
    JOIN_OR_LEAVE_JOIN_ERROR,
    JOIN_OR_LEAVE_JOIN_SUCCESS,
    /**
     * Specific case when no spinner values were received, this means the reference doesn't exist, should push user out
     */
    JOIN_OR_LEAVE_GETTING_SPINNER_VALUES_ERROR_NO_VALUES,

    /**
     * When an interaction flow succeeds
     */
    SUCCESS,

    /**
     * Incase a cell contains unknown information, should error gracefully if this happens
     */
    UNKNOWN
}
