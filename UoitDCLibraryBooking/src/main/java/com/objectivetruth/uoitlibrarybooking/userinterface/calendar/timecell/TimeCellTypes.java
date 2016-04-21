package com.objectivetruth.uoitlibrarybooking.userinterface.calendar.timecell;

/**
 * Types of cells to show in the calendar, corresponds to the ones found on the website
 * https://rooms.library.dc-uoit.ca/uoit_studyrooms/calendar.aspx
 */
enum TimeCellTypes {

    /**
     * Column header in the table view. Usually the room number example: LIB302
     */
    TABLE_COLUMN_HEADER,

    /**
     * Row header in the table view. Usually the time in 30 min slots
     */
    TABLE_ROW_HEADER,

    /**
     * Slot of time that's completely open (nobody has tried to book)
     */
    BOOKING_OPEN,

    /**
     * Slot with at least 1 reservation trying to confirm it
     */
    BOOKING_COMPETING,

    /**
     * Slot that's completely locked. Usually proceeds a BOOKING_CONFIRMED slot
     */
    BOOKING_LOCKED,

    /**
     * Slot that's been confirmed by someone (not necessarily the user). One can still
     * enter the booking and either add themselves to the already confirmed booking
     * Or remove themselves from the booking if they were part of it previously
     */
    BOOKING_CONFIRMED
}
