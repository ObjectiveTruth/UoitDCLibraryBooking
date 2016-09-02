package com.objectivetruth.uoitlibrarybooking.data.models.bookinginteractionmodel;

/**
 * Types of events that can fire from the booking interaction section of the app
 */
public enum BookingInteractionEventType {
    /**
     * States for when booking is requested
     */
    BOOK,
    BOOK_ERROR,
    BOOK_RUNNING,
    BOOK_SUCCESS,

    /**
     * When the user clicks on a booking that is being competed for, this loads the initial form that must be filled
     * with data that is sent asynchronously and while that's going on, this will be {@<code>JOIN_OR_LEAVE_RUNNING_GETTING_SPINNER_VALUES</code>}
     */
    JOIN_OR_LEAVE,
    /**
     * States for getting the spinner values whe opening up JoinOrLeave
     */
    JOIN_OR_LEAVE_GETTING_SPINNER_VALUES_RUNNING,
    JOIN_OR_LEAVE_GETTING_SPINNER_VALUES_ERROR,
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
     * The case where the booking is confirmed and user clicks to open, they can either join or leave
     */
    VIEWLEAVEORJOIN,
    VIEWLEAVEORJOIN_RUNNING,
    VIEWLEAVEORJOIN_ERROR,
    VIEWLEAVEORJOIN_SUCCESS,

    /**
     * When the user isn't logged in, will throw this to tell frontend to show login screens
     */
    CREDENTIALS_LOGIN,

    /**
     * Incase a cell contains unknown information, should error gracefully if this happens
     */
    UNKNOWN
}
