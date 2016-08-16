package com.objectivetruth.uoitlibrarybooking.data.models.bookinginteractionmodel;

/**
 * Types of events that can fire from the booking interaction section of the app
 */
public enum BookingInteractionEventType {
    /**
     * When a booking interaction flow is in progress (they're choosing their room name/code, etc)
     */
    IN_PROGRESS,

    /**
     * When an interaction flow succeeds
     */
    SUCCESS
}
